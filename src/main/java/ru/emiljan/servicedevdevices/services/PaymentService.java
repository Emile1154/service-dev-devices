package ru.emiljan.servicedevdevices.services;

import ru.emiljan.servicedevdevices.models.Payment;

import java.net.URI;

/**
 * @author EM1LJAN
 */
public interface PaymentService {
    Payment createOrder(Double moneyAmount, URI returnURI);
    boolean captureOrder(String orderId);
    void save(Payment payment, String username, Long orderId);
    void update(String token);
}
