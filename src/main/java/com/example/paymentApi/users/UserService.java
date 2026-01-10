package com.example.paymentApi.users;

import com.example.paymentApi.event.user.UserEventPublisher;
import com.example.paymentApi.messaging.OtpEmailService;
import com.example.paymentApi.shared.ExceptionThrower;
import com.example.paymentApi.shared.HttpRequestUtil;
import com.example.paymentApi.shared.exception.DuplicateRecordException;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.shared.utility.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UserService {
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final ExceptionThrower exceptionThrower;
    private final UserRepository userRepository;
    private final Verifier verifier;
    private final PasswordHashUtil passwordHashUtil;
    private final OtpEmailService emailService;
    private final UserEventPublisher userCreatedEventPublisher;
    private final RedisOtpService redisOtpService;
    private static final Duration TTL = Duration.ofMinutes(5);


    public UserService(ModelMapper modelMapper, JwtService jwtService,
                       ExceptionThrower exceptionThrower, UserRepository userRepository,
                       Verifier verifier, PasswordHashUtil passwordHashUtil, OtpEmailService emailService,
                       UserEventPublisher userCreatedEventPublisher, RedisOtpService userOtpService) {
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.exceptionThrower = exceptionThrower;
        this.userRepository = userRepository;
        this.verifier = verifier;
        this.passwordHashUtil = passwordHashUtil;
        this.emailService = emailService;
        this.userCreatedEventPublisher = userCreatedEventPublisher;
        this.redisOtpService = userOtpService;
    }


    public SignUpResponse signUp(UserRequest userRequest) {
        String path = HttpRequestUtil.getServletPath();

        userRepository.findByEmailAddress(userRequest.getEmailAddress())
                .ifPresent(user-> {
                    throw new DuplicateRecordException("user already exist");
                });
        verifier.verifyParams(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getPassword(),
                userRequest.getPhoneNumber()
        );
        Verifier.verifyEmail(userRequest.getEmailAddress());
        Verifier.verifyPasswordFormat(userRequest.getPassword());

        if (!userRequest.getAcceptedTerms()) {
            throw new ValidationException("You must accept terms and conditions to create an account.");
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

        return modelMapper.map(savedUser, SignUpResponse.class);

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

        long time = TTL.toMinutes();

        String path = HttpRequestUtil.getServletPath();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
                exceptionThrower.throwUserNotFoundExistException(path));

                Verifier.verifyEmail(emailAddress);

        OtpGenerator.OtpData otp = OtpGenerator.generateOtp();

        log.info("Generated otp {}", otp);

        redisOtpService.saveOtp(user.getId(), "EMAIL_VERIFY", otp.getOtp(), time);

        log.info("saved otp on redis server");

        emailService.sendOtpEmail(emailAddress, otp.getOtp(), time);

        return "Otp successfully sent";
    }

    public String verifyOtp(String otp, String emailAddress) {

        long time = TTL.toMinutes();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));

        boolean valid = redisOtpService.verifyOtp(user.getId(), "EMAIL_VERIFY", otp, time);

        if(!valid){
            throw new ValidationException("Invalid or expired otp, request a new otp");
        }

        Verifier.verifyOtpFormat(otp);
        Verifier.verifyEmail(emailAddress);

        user.setVerified(true);

        userRepository.save(user);
//ToDo: publish a mail to user letting them know their email has been verified.
        return "Email successfully verified.";

    }

    public UserResponse refreshToken(String id, HttpServletResponse httpServletResponse,
                                     HttpServletRequest httpServletRequest) {
        String path = HttpRequestUtil.getServletPath();

        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));

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

        long time = TTL.toMinutes();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(()->
                new ResourceNotFoundException("User does not exist"));

        OtpGenerator.OtpData otp = OtpGenerator.generateOtp();
        redisOtpService.saveOtp(user.getId(), "PASSWORD_RESET", otp.getOtp(), time);

        emailService.sendOtpEmail(user.getEmailAddress(), otp.getOtp(), time);

        return "OTP sent successfully";
    }

    public String resetPassword(String id, String newPassword, String newOtp) {

        long time = TTL.toMinutes();

        User user = userRepository.findById(id).orElseThrow(() ->
               new ResourceNotFoundException("User does not exist"));


       boolean valid = redisOtpService.verifyOtp(user.getId(),"PASSWORD_RESET", newOtp, time );

        if(!valid){
            throw new ValidationException("Invalid or expired otp, request a new otp");
        }

        Verifier.verifyOtpFormat(newOtp);

        Verifier.verifyPasswordFormat(newPassword);

        user.setPassword(passwordHashUtil.hashPassword(newPassword));

        userRepository.save(user);

        return "Password Reset successfully";
    }

    public String deleteUser(String id){
        User user = userRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("User not found"));
        userRepository.delete(user);

        return "Wallet creation failed, user deleted & process rolled back";
    }

}

