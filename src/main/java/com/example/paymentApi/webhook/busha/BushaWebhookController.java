package com.example.paymentApi.webhook.busha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/vi/busha")
@Slf4j
public class BushaWebhookController{

    @PostMapping("/finalize/offRamp")
    public void handleOffRampPayment(){

    }

    @PostMapping("/finalize/onRamp")
    public void handleOnRampPayment(){

    }

}
