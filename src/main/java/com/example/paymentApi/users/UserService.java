package com.example.paymentApi.users;

import com.example.paymentApi.event.user.UserCreatedEventPublisher;
import com.example.paymentApi.messaging.OtpEmailService;
import com.example.paymentApi.shared.ExceptionThrower;
import com.example.paymentApi.shared.HttpRequestUtil;
import com.example.paymentApi.shared.utility.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final ExceptionThrower exceptionThrower;
    private final UserRepository userRepository;
    private final Verifier verifier;
    private final PasswordHashUtil passwordHashUtil;
    private final OtpEmailService emailService;
    private final UserCreatedEventPublisher userCreatedEventPublisher;

    public UserService(ModelMapper modelMapper, JwtService jwtService,
                       ExceptionThrower exceptionThrower, UserRepository userRepository,
                       Verifier verifier, PasswordHashUtil passwordHashUtil, OtpEmailService emailService,
                       UserCreatedEventPublisher userCreatedEventPublisher) {
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.exceptionThrower = exceptionThrower;
        this.userRepository = userRepository;
        this.verifier = verifier;
        this.passwordHashUtil = passwordHashUtil;
        this.emailService = emailService;
        this.userCreatedEventPublisher = userCreatedEventPublisher;
    }


    public UserResponse signUp(UserRequest userRequest) {
        String path = HttpRequestUtil.getServletPath();

        userRepository.findByEmailAddress(userRequest.getEmailAddress())
                .ifPresent(user ->
                        exceptionThrower.throwUserAlreadyExistException(path));
        verifier.setResourceUrl(HttpRequestUtil.getServletPath()).verifyParams(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getPassword(),
                userRequest.getPhoneNumber()
        );
        verifier.verifyEmail(userRequest.getEmailAddress());
        verifier.verifyPasswordFormat(userRequest.getPassword(), path);

        if (!userRequest.getAcceptedTerms()) {
            exceptionThrower.throwTermsNotAcceptedException(path);
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setEmailAddress(userRequest.getEmailAddress());
        user.setPassword(passwordHashUtil.hashPassword(userRequest.getPassword()));
        user.setAcceptedTerms(userRequest.getAcceptedTerms());

        User savedUser = userRepository.save(user);

        userCreatedEventPublisher.publishUserCreatedEvent(user.getId());

        return modelMapper.map(savedUser, UserResponse.class);

    }

    public UserResponse login(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        String path = HttpRequestUtil.getServletPath();

        User user = userRepository.findByEmailAddress(loginRequest.getEmailAddress()).orElseThrow(() ->
                exceptionThrower.throwUserNotFoundExistException(path));

        boolean passwordMatches = passwordHashUtil.verifyPassword(loginRequest.getPassword(), user.getPassword());
        if (!passwordMatches) {
            exceptionThrower.throwInvalidLoginException(path);
        }
        if (!user.isVerified()) {
            exceptionThrower.throwUserNotVerifiedException(path);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        TokenUtil.setAccessTokenHeader(httpServletResponse, accessToken);
        TokenUtil.setRefreshTokenCookie(httpServletResponse, refreshToken);

        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        userResponse.setAccessToken(accessToken);
        userResponse.setRefreshToken(refreshToken);
        return userResponse;

    }

    @Transactional
    public String sendOtp(String emailAddress) {
        String path = HttpRequestUtil.getServletPath();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
                exceptionThrower.throwUserNotFoundExistException(path));

                verifier.verifyEmail(emailAddress);

        OtpGenerator.OtpData otp = OtpGenerator.generateOtp();

        user.setOtp(otp.getOtp());

        user.setOtpExpiryTime(otp.getExpiryTime());

        userRepository.save(user);

        emailService.sendOtpEmail(emailAddress, otp.getOtp(),
                OtpGenerator.getExpiryDurationString(otp.getExpiryTime()));

        return "Otp successfully sent";
    }

    public String verifyOtp(String otp, String emailAddress) {
        String path = HttpRequestUtil.getServletPath();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
                exceptionThrower.throwUserNotFoundExistException(path));

        if (user.getOtp() == null || user.getOtpExpiryTime() == null) {
            exceptionThrower.throwOtpNotFoundException(path);
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            exceptionThrower.throwOtpExpiredException(path);
        }

        if (!otp.equals(user.getOtp())) {
            exceptionThrower.throwInvalidOtpException(path);
        }

        verifier.verifyOtpFormat(otp, path);
        verifier.verifyEmail(emailAddress);


        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiryTime(null);

        userRepository.save(user);

        return "Email successfully verified.";

    }

    public UserResponse refreshToken(String id, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        String path = HttpRequestUtil.getServletPath();

        User user = userRepository.findById(id).orElseThrow(() ->
                exceptionThrower.throwUserNotFoundExistException(path));

        String refreshToken = TokenUtil.extractRefreshTokenFromRequest(httpServletRequest);

        if (!jwtService.validateRefreshToken(refreshToken)) {
            exceptionThrower.throwInvalidRefreshTokenException(path);
        }
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        TokenUtil.setAccessTokenHeader(httpServletResponse, newAccessToken);
        TokenUtil.setRefreshTokenCookie(httpServletResponse, newRefreshToken);

        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        return response;

    }

    @Transactional
    public String requestPasswordReset(String emailAddress){
        String path = HttpRequestUtil.getServletPath();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(()->
                exceptionThrower.throwUserNotFoundExistException(path));

        OtpGenerator.OtpData otp = OtpGenerator.generateOtp();
        user.setOtp(otp.getOtp());
        user.setOtpExpiryTime(otp.getExpiryTime());

        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmailAddress(), otp.getOtp(),
                OtpGenerator.getExpiryDurationString(otp.getExpiryTime()));

        return "OTP sent successfully";
    }

    public String resetPassword(String id, String newPassword, String newOtp) {
        String path = HttpRequestUtil.getServletPath();

        User user = userRepository.findById(id).orElseThrow(() ->
                exceptionThrower.throwUserNotFoundExistException(path));

        verifier.verifyOtpFormat(newOtp, path);

        if (user.getOtp() == null) {
            exceptionThrower.throwOtpNotFoundException(path);
        }

        if(!user.getOtp().equals(newOtp)){
            exceptionThrower.throwInvalidOtpException(path);
        }

        if(LocalDateTime.now().isAfter(user.getOtpExpiryTime())){
            exceptionThrower.throwOtpExpiredException(path);

        }
        verifier.verifyPasswordFormat(newPassword, path);

        user.setPassword(passwordHashUtil.hashPassword(newPassword));
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        return "Password Reset successfully";
    }

}

