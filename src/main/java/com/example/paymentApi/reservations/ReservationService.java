package com.example.paymentApi.reservations;

import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.wallets.Wallet;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;

    public Reservation createReservation(Wallet wallet, ReservationRequest request){

        Reservation reservation = new Reservation();
        reservation.setWallet(wallet);
        reservation.setAmount(request.getAmount());
        reservation.setReservationType(request.getReservationType());
        reservation.setProviderTransactionId(request.getProviderTransactionId());
        reservation.setReason(request.getReason());

       return reservationRepository.save(reservation);
    }

    public ReservationResponse getReservation(String id){
        Reservation reservation = reservationRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Reservation record with id " + id + "does not exist"));

        return modelMapper.map(reservation, ReservationResponse.class);

    }
}
