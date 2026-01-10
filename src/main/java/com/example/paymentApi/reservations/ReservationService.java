package com.example.paymentApi.reservations;

import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.wallets.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WalletRepository walletRepository;
    private final ModelMapper modelMapper;

    public void reserveFund(String id, ReservationRequest request){

        Reservation reservation = new Reservation();
        reservation.setWallet(walletRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Wallet does not exist")));
        reservation.setAmount(request.getAmount());
        reservation.setReservationType(request.getReservationType());
        reservation.setTransactionId(request.getTransactionId());
        reservation.setReason(request.getReason());

        reservationRepository.save(reservation);

    }

    public ReservationResponse getReservation(String id){
        Reservation reservation = reservationRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Reservation record with id " + id + "does not exist"));

        return modelMapper.map(reservation, ReservationResponse.class);


    }
}
