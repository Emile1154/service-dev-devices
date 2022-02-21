package ru.emiljan.servicedevdevices.services.payservices;

import ru.emiljan.servicedevdevices.models.payment.Payment;

import java.net.URI;

/**
 * @author EM1LJAN
 */
public interface PaymentService {
    Payment createOrder(URI returnURI, Long order_id);
    boolean captureOrder(String orderId);
    void save(Payment payment, String username, Long orderId);
    void update(String token);
}
