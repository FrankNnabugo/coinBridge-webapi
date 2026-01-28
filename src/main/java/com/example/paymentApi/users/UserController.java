package com.example.paymentApi.users;

import com.example.paymentApi.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "user authentication", description = "user onboarding & authentication controller")
public class UserController {

    private final UserService userService;

    @Operation(summary = "user signup")
    @PostMapping
    public ApiResponse<SignUpResponse> signUp(@RequestBody UserRequest userRequest){
        SignUpResponse response = userService.signUp(userRequest);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }

    @Operation(summary = "user login")
    @PostMapping("/authenticate")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody LoginRequest loginRequest,
                                           HttpServletResponse httpServletResponse){
        AuthenticationResponse response = userService.authenticate(loginRequest, httpServletResponse);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }

    @Operation(summary = "send Otp")
    @PostMapping("/otp/mail")
    public ApiResponse<String> sendOtp(@RequestParam String emailAddress){
        String response = userService.sendOtp(emailAddress);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }

    @Operation(summary = "verify Otp")
    @PostMapping("otp/verify")
    public ApiResponse<String> verifyOtp(@RequestBody String otp, @RequestParam String emailAddress){
        String response = userService.verifyOtp(otp, emailAddress);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }

    @Operation(summary = "refresh token")
    @PostMapping("/{userId}/refresh-token")
    public ApiResponse<AuthenticationResponse> refreshToken(@PathVariable String userId, HttpServletResponse httpServletResponse,
                                                            HttpServletRequest httpServletRequest){
        AuthenticationResponse response = userService.refreshToken(userId, httpServletResponse, httpServletRequest);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }
    @Operation(summary = "request password reset")
    @PostMapping("/request/reset-password")
    public ApiResponse<String> requestPasswordReset(@RequestParam String emailAddress){
        String response = userService.requestPasswordReset(emailAddress);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }

    @Operation(summary = "change password")
    @PutMapping("/{userId}/reset-password")
    public ApiResponse<String> resetPassword(@PathVariable("UserId") String userId, @RequestBody ResetPasswordRequest request){
        String response = userService.resetPassword(userId, request);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable("UserId") String userId){
        String response = userService.deleteUser(userId);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }

}
