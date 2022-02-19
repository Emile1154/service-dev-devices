package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.PaypalPayment;

@Repository
public interface PaypalRepository extends JpaRepository<PaypalPayment, String> {
    PaypalPayment findPaypalPaymentByToken(String token);
}
