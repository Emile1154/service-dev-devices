package ru.emiljan.servicedevdevices.repositories.payrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.payment.PaypalPayment;

/**
 *
 * @author EM1LJAN
 */
@Repository
public interface PaypalRepository extends JpaRepository<PaypalPayment, String> {
    PaypalPayment findPaypalPaymentByToken(String token);
}
