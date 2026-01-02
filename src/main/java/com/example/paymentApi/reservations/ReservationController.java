package com.example.paymentApi.reservations;

import com.example.paymentApi.shared.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public ApiResponse<ReservationResponse> getReservation(@PathVariable("id") String id){
        ReservationResponse response = reservationService.getReservation(id);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }
}
