package ru.emiljan.servicedevdevices.repositories.payrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.payment.VKPayPayment;

@Repository
public interface VKPayRepository extends JpaRepository<VKPayPayment, Long> {
    @Query(value = "SELECT coalesce(max(id),0) FROM payments", nativeQuery = true)
    Long getMaxId();
}
