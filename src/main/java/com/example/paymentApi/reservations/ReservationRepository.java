package com.example.paymentApi.reservations;
import com.example.paymentApi.shared.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    Reservation findByProviderTransactionId(String providerTransactionId);

}
