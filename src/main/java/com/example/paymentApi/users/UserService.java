package com.example.paymentApi.users;

import com.example.paymentApi.event.user.UserEventPublisher;
import com.example.paymentApi.messaging.AccountVerificationEmail;
import com.example.paymentApi.messaging.PasswordResetEmail;
import com.example.paymentApi.shared.exception.DuplicateRecordException;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.shared.utility.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Verifier verifier;
    private final AccountVerificationEmail accountVerificationEmail;
    private final UserEventPublisher userCreatedEventPublisher;
    private final RedisOtpService redisOtpService;
    private final PasswordResetEmail passwordResetEmail;
    private static final Duration TTL = Duration.ofMinutes(10);
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public SignUpResponse signUp(UserRequest userRequest) {

        userRepository.findByEmailAddress(userRequest.getEmailAddress())
                .ifPresent(user -> {
                    throw new DuplicateRecordException("User already exist");
                });

        userRepository.findByPhoneNumber(userRequest.getPhoneNumber())
                .ifPresent(phoneNumber -> {
                    throw new DuplicateRecordException("Phone number already exist. Please use another phone number");
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
            throw new ValidationException("You must accept terms and condition to create account.");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setEmailAddress(userRequest.getEmailAddress());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setAcceptedTerms(userRequest.getAcceptedTerms());

        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        userCreatedEventPublisher.publishUserCreatedEvent(savedUser.getId());

        return modelMapper.map(savedUser, SignUpResponse.class);

    }

    public AuthenticationResponse authenticate(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {

        User user = userRepository.findByEmailAddress(loginRequest.getEmailAddress()).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));

        if (user.isDeleted()) {
            throw new ValidationException("User account does not exist");
        }

        if (!user.isVerified()) {
            throw new ValidationException("Please verify your account to enable login");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmailAddress(),
                        loginRequest.getPassword()
                )
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        TokenUtil.setAccessTokenToHeader(httpServletResponse, accessToken);
        TokenUtil.setRefreshTokenToCookie(httpServletResponse, refreshToken);

        AuthenticationResponse response = modelMapper.map(user, AuthenticationResponse.class);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;

    }

    @Transactional
    public String sendOtp(String emailAddress) {

        long time = TTL.toMinutes();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));


        Verifier.verifyEmail(emailAddress);

        OtpGenerator.OtpData otp = OtpGenerator.generateOtp();

        log.info("Generated otp {}", otp);

        redisOtpService.saveOtp(user.getId(), "EMAIL_VERIFICATION", otp.getOtp(), time);

        log.info("saved otp on redis server");

        accountVerificationEmail.sendOtpEmail(emailAddress, otp.getOtp(), time);

        return "Otp successfully sent";
    }


    public String verifyOtp(String otp, String emailAddress) {

        long time = TTL.toMinutes();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));

        redisOtpService.verifyOtp(user.getId(), "EMAIL_VERIFICATION", otp, time);

        Verifier.verifyOtpFormat(otp);
        Verifier.verifyEmail(emailAddress);

        user.setVerified(true);

        userRepository.save(user);
//ToDo: publish mail to user letting them know their email has been verified.
        return "Email successfully verified.";

    }


    public AuthenticationResponse refreshToken(String id, HttpServletResponse servletResponse,
                                               HttpServletRequest servletRequest) {

        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));

        String refreshToken = TokenUtil.extractRefreshTokenFromRequest(servletRequest);

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new ValidationException("Invalid or expired refreshToken");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        TokenUtil.setAccessTokenToHeader(servletResponse, newAccessToken);
        TokenUtil.setRefreshTokenToCookie(servletResponse, newRefreshToken);

        AuthenticationResponse response = modelMapper.map(user, AuthenticationResponse.class);
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        return response;
    }

    @Transactional
    public String requestPasswordReset(String emailAddress) {

        long time = TTL.toMinutes();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));

        OtpGenerator.OtpData otp = OtpGenerator.generateOtp();
        redisOtpService.saveOtp(user.getId(), "PASSWORD_RESET", otp.getOtp(), time);

        passwordResetEmail.sendOtpEmail(user.getEmailAddress(), otp.getOtp(), time);

        return "OTP sent successfully";
    }


    public String resetPassword(String id, ResetPasswordRequest request) {

        long time = TTL.toMinutes();

        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));

        redisOtpService.verifyOtp(user.getId(), "PASSWORD_RESET", request.getOtp(), time);

        Verifier.verifyOtpFormat(request.getOtp());

        Verifier.verifyPasswordFormat(request.getNewPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return "Password Reset successful";
    }


    public String deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));
        user.setDeleted(true);
        user.setAccountDeletedAt(LocalDateTime.now());

        return "User successfully deleted";
    }

}

