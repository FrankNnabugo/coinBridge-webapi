package com.example.paymentApi.users;

import com.example.paymentApi.event.user.UserEventPublisher;
import com.example.paymentApi.messaging.AccountVerificationEmailService;
import com.example.paymentApi.messaging.PasswordResetEmailService;
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
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Verifier verifier;
    private final PasswordHashUtil passwordHashUtil;
    private final AccountVerificationEmailService accountVerificationEmailService;
    private final UserEventPublisher userCreatedEventPublisher;
    private final RedisOtpService redisOtpService;
    private final PasswordResetEmailService passwordResetEmailService;
    private static final Duration TTL = Duration.ofMinutes(5);


    public UserService(ModelMapper modelMapper, JwtService jwtService, UserRepository userRepository,
                       Verifier verifier, PasswordHashUtil passwordHashUtil, AccountVerificationEmailService emailService,
                       UserEventPublisher userCreatedEventPublisher, RedisOtpService userOtpService,
                       PasswordResetEmailService passwordResetEmailService) {
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.verifier = verifier;
        this.passwordHashUtil = passwordHashUtil;
        this.accountVerificationEmailService = emailService;
        this.userCreatedEventPublisher = userCreatedEventPublisher;
        this.redisOtpService = userOtpService;
        this.passwordResetEmailService = passwordResetEmailService;
    }


    public SignUpResponse signUp(UserRequest userRequest) {

        userRepository.findByEmailAddress(userRequest.getEmailAddress())
                .ifPresent(user-> {
                    throw new DuplicateRecordException("user already exist");
                });

       userRepository.findByPhoneNumber(userRequest.getPhoneNumber())
                       .ifPresent(phoneNumber->{
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
            throw new ValidationException("You must accept terms and conditions to create account.");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setEmailAddress(userRequest.getEmailAddress());
        user.setPassword(passwordHashUtil.hashPassword(userRequest.getPassword()));
        user.setAcceptedTerms(userRequest.getAcceptedTerms());

        User savedUser = userRepository.save(user);

        userCreatedEventPublisher.publishUserCreatedEvent(savedUser.getId(), savedUser.getEmailAddress());

        return modelMapper.map(savedUser, SignUpResponse.class);

    }

    public UserResponse authenticate (LoginRequest loginRequest, HttpServletResponse httpServletResponse) {

        User user = userRepository.findByEmailAddress(loginRequest.getEmailAddress()).orElseThrow(() ->
               new ResourceNotFoundException("User does not exist"));

        if(!user.isDeleted()){
            throw new ValidationException("User account does not exist");
        }

        boolean passwordMatch = passwordHashUtil.verifyPassword(loginRequest.getPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new ValidationException("Invalid Login Credential");
        }
        if (!user.isVerified()) {
            throw new ValidationException("Please verify your account to enable login");
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

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException("User does not exist"));

        if(!user.isDeleted()){
            throw new ValidationException("User account does not exist");
        }

        Verifier.verifyEmail(emailAddress);

        OtpGenerator.OtpData otp = OtpGenerator.generateOtp();

        log.info("Generated otp {}", otp);

        redisOtpService.saveOtp(user.getId(), "EMAIL_VERIFICATION", otp.getOtp(), time);

        log.info("saved otp on redis server");

        accountVerificationEmailService.sendOtpEmail(emailAddress, otp.getOtp(), time);

        return "Otp successfully sent";
    }


    public String verifyOtp(String otp, String emailAddress) {

        long time = TTL.toMinutes();

        User user = userRepository.findByEmailAddress(emailAddress).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));

        if(!user.isDeleted()){
            throw new ValidationException("User account does not exist");
        }

        redisOtpService.verifyOtp(user.getId(), "EMAIL_VERIFICATION", otp, time);

        Verifier.verifyOtpFormat(otp);
        Verifier.verifyEmail(emailAddress);

        user.setVerified(true);

        userRepository.save(user);
//ToDo: publish mail to user letting them know their email has been verified.
        return "Email successfully verified.";

    }


    public UserResponse refreshToken(String id, HttpServletResponse httpServletResponse,
                                     HttpServletRequest httpServletRequest) {

        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist"));


        if(!user.isDeleted()){
            throw new ValidationException("User account does not exist");
        }

        if(!user.isVerified()){
            throw new ValidationException("Please verify your account to proceed");
        }

        String refreshToken = TokenUtil.extractRefreshTokenFromRequest(httpServletRequest);

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new ValidationException("Invalid or expired refreshToken");
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

        if(!user.isDeleted()){
            throw new ValidationException("User account does not exist");
        }

        if(!user.isVerified()){
            throw new ValidationException("Please verify your account to proceed");
        }

        OtpGenerator.OtpData otp = OtpGenerator.generateOtp();
        redisOtpService.saveOtp(user.getId(), "PASSWORD_RESET", otp.getOtp(), time);

        passwordResetEmailService.sendOtpEmail(user.getEmailAddress(), otp.getOtp(), time);

        return "OTP sent successfully";
    }

    public String resetPassword(String id, String newPassword, String newOtp) {

        long time = TTL.toMinutes();

        User user = userRepository.findById(id).orElseThrow(() ->
               new ResourceNotFoundException("User does not exist"));

        if(!user.isDeleted()){
            throw new ValidationException("User account does not exist");
        }

        if(!user.isVerified()){
            throw new ValidationException("Please verify your account to proceed");
        }

       redisOtpService.verifyOtp(user.getId(),"PASSWORD_RESET", newOtp, time );

        Verifier.verifyOtpFormat(newOtp);

        Verifier.verifyPasswordFormat(newPassword);

        user.setPassword(passwordHashUtil.hashPassword(newPassword));

        userRepository.save(user);

        return "Password Reset successful";
    }

   public String deleteUser(String id){
        User user = userRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("User does not exist"));
        user.setDeleted(true);
        user.setAccountDeletedAt(LocalDateTime.now());

        return "User successfully deleted";
    }

}

