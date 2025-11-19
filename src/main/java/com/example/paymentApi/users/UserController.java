package com.example.paymentApi.users;

import com.example.paymentApi.shared.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<UserResponse> signUp(@RequestBody UserRequest userRequest){
        UserResponse response = userService.signUp(userRequest);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }

    @PostMapping("/authenticate")
    public ApiResponse<UserResponse> login(@RequestBody LoginRequest loginRequest,
                                           HttpServletResponse httpServletResponse){
        UserResponse response = userService.login(loginRequest, httpServletResponse);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }

    @PostMapping("/otp/mail")
    public ApiResponse<String> sendOtp(@RequestParam String emailAddress){
        String response = userService.sendOtp(emailAddress);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }

    @PostMapping("otp/verify")
    public ApiResponse<String> verifyOtp(@RequestParam String otp, @RequestParam String emailAddress){
        String response = userService.verifyOtp(otp, emailAddress);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }

    @PostMapping("/refresh-token/{id}")
    public ApiResponse<UserResponse> refreshToken(@PathVariable String id, HttpServletResponse httpServletResponse,
                                                  HttpServletRequest httpServletRequest){
        UserResponse response = userService.refreshToken(id, httpServletResponse, httpServletRequest);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }
    @PostMapping("/reset-password/request")
    public ApiResponse<String> requestPasswordReset(@RequestBody String emailAddress){
        String response = userService.requestPasswordReset(emailAddress);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }

    @PutMapping("/reset-password/confirm/{id}")
    public ApiResponse<String> resetPassword(@PathVariable String id, @RequestBody String newPassword,
                                             @RequestBody String newOtp){
        String response = userService.resetPassword(id, newPassword, newOtp);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }

}
