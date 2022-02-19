package ru.emiljan.servicedevdevices.services;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.paypal.orders.Order;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.*;
import ru.emiljan.servicedevdevices.repositories.OrderRepository;
import ru.emiljan.servicedevdevices.repositories.PaypalRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author EM1LJAN
 */
@Service
public class PaypalServiceImpl implements PaymentService{

    private final PayPalHttpClient payPalHttpClient;
    private final PaypalRepository paypalRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private static final String APPROVE_REL = "approve";

    @Autowired
    public PaypalServiceImpl(PayPalHttpClient payPalHttpClient,
                             PaypalRepository paypalRepository,
                             UserRepository userRepository,
                             OrderRepository orderRepository) {
        this.payPalHttpClient = payPalHttpClient;
        this.paypalRepository = paypalRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    @SneakyThrows
    public Payment createOrder(Double moneyAmount, URI returnURI) {
        final OrderRequest orderRequest = createOrderRequest(moneyAmount, returnURI);
        final OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        final HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(request);
        final Order order = orderHttpResponse.result();

        LinkDescription response = findApprovalLink(order);
        return new PaypalPayment(order.id(),URI.create(response.href()));
    }

    private OrderRequest createOrderRequest(Double moneyAmount, URI captureUrl) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        orderRequest.purchaseUnits(getPurchaseList(moneyAmount));
        orderRequest.applicationContext(getContext(captureUrl));
        return orderRequest;
    }

    private List<PurchaseUnitRequest> getPurchaseList(Double moneyAmount) {
        final PurchaseUnitRequest purchase = new PurchaseUnitRequest()
                .amountWithBreakdown(
                        new AmountWithBreakdown()
                                .currencyCode("RUB")
                                .value(moneyAmount.toString())
                );
        return List.of(purchase);
    }

    private ApplicationContext getContext(URI captureUrl) {
        return new ApplicationContext()
                .brandName("Первичная оплата разработки заказа ServiceDevDevices")
                .returnUrl(captureUrl.toString())
                .userAction("PAY_NOW");
    }

    private LinkDescription findApprovalLink(Order order) {
        return order.links().stream()
                .filter(link -> APPROVE_REL.equals(link.rel()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public boolean captureOrder(String token) {
        final OrdersCaptureRequest request = new OrdersCaptureRequest(token);
        try{
            final HttpResponse<Order> response = payPalHttpClient.execute(request);
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public void save(Payment payment, String username, Long orderId) {
        payment.setPayStatus(PayStatus.CANCELED);
        payment.setUser(userRepository.findByNickname(username)); //fix this later
        payment.setOrder(orderRepository.findById(orderId).orElse(null)); //fix this later
        paypalRepository.save((PaypalPayment) payment);
    }

    @Override
    @Transactional
    public void update(String token) {
        PaypalPayment paypal = paypalRepository.findPaypalPaymentByToken(token);
        paypal.setPayStatus(PayStatus.PAYED);
        CustomOrder order = paypal.getOrder();
        order.setOrderStatus(Status.PAYED);
        orderRepository.save(order);
        paypalRepository.save(paypal);
    }
}
