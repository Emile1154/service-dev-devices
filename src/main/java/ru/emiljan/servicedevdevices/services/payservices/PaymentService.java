package ru.emiljan.servicedevdevices.services.payservices;

import ru.emiljan.servicedevdevices.models.payment.Payment;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * Service class for {@link ru.emiljan.servicedevdevices.models.payment.PaypalPayment}
 *               and {@link ru.emiljan.servicedevdevices.models.payment.VKPayPayment}
 *
 * @author EM1LJAN
 */
public interface PaymentService {
    Payment createOrder(URI returnURI, Long order_id);
    boolean captureOrder(String orderId);
    void save(Payment payment, String username, Long orderId);
    void update(String token, HttpServletRequest request);
}
