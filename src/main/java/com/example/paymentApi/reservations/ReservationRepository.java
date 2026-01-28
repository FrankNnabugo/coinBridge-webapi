package com.example.paymentApi.reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    Reservation findByProviderTransactionId(String providerTransactionId);
    boolean existsByProviderTransactionId(String providerTransactionId);

}
