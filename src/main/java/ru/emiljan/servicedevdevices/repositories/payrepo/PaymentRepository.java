package ru.emiljan.servicedevdevices.repositories.payrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.emiljan.servicedevdevices.models.payment.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long>,
                                            JpaSpecificationExecutor<Payment> {
    @Query(value = "SELECT * FROM payments WHERE user_id = ?1",nativeQuery = true)
    List<Payment> findAllByKeyword(String userId);
    List<Payment> findPaymentsByUserId(Long id);
}
