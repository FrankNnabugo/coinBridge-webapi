package com.example.paymentApi.offramp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/initiate/offRamp")
@Slf4j
@RequiredArgsConstructor
public class OffRampController {

    @PostMapping
    public void initiateOffRamp(){

    }
}
