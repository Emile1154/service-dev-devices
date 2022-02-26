package ru.emiljan.servicedevdevices.services.payservices;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.paypal.orders.Order;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.*;
import ru.emiljan.servicedevdevices.models.payment.PayStatus;
import ru.emiljan.servicedevdevices.models.payment.Payment;
import ru.emiljan.servicedevdevices.models.payment.PaypalPayment;
import ru.emiljan.servicedevdevices.repositories.OrderRepository;
import ru.emiljan.servicedevdevices.repositories.payrepo.PaypalRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author EM1LJAN
 */
@Service
public class PaypalServiceImpl implements PaymentService {

    private final PayPalHttpClient payPalHttpClient;
    private final PaypalRepository paypalRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final NotifyService notifyService;

    private static final String APPROVE_REL = "approve";

    @Autowired
    public PaypalServiceImpl(PayPalHttpClient payPalHttpClient,
                             PaypalRepository paypalRepository,
                             UserRepository userRepository,
                             OrderRepository orderRepository,
                             NotifyService notifyService) {
        this.payPalHttpClient = payPalHttpClient;
        this.paypalRepository = paypalRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.notifyService = notifyService;
    }

    @Override
    @SneakyThrows
    public Payment createOrder(URI returnURI, Long order_id) {
        final CustomOrder payOrder = orderRepository.findById(order_id).orElse(null);
        if(payOrder==null || payOrder.getPrice().equals(BigDecimal.ZERO)){
            return null;
        }
        final OrderRequest orderRequest = createOrderRequest(payOrder.getPrice().doubleValue(), returnURI);
        final OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        final HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(request);
        final Order order = orderHttpResponse.result();

        LinkDescription response = findApprovalLink(order);
        return new PaypalPayment(order.id(),URI.create(response.href()));
    }

    private OrderRequest createOrderRequest(Double moneyAmount, URI captureUrl) {
        final OrderRequest orderRequest = new OrderRequest();
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
            payPalHttpClient.execute(request);
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public void save(Payment payment, String username, Long orderId) {
        final User user = this.userRepository.findByNickname(username);
        final CustomOrder order = this.orderRepository.findById(orderId).orElse(null);
        if(user == null || order == null){
            System.out.println("THIS ORDER OR USER HAS NOT FOUND IN DATABASE");
            return;
        }
        payment.setPayStatus(PayStatus.CANCELED);
        payment.setUser(user);
        payment.setOrder(order);
        paypalRepository.save((PaypalPayment) payment);
    }

    @Override
    @Transactional
    public void update(String token) {
        PaypalPayment paypal = paypalRepository.findPaypalPaymentByToken(token);
        paypal.setPayStatus(PayStatus.PAYED);
        CustomOrder order = paypal.getOrder();
        order.setOrderStatus(Status.PAYED);
        order.setPrice(BigDecimal.ZERO);
        notifyService.createNotify("buy", order.getUser());
        orderRepository.save(order);
        paypalRepository.save(paypal);
    }
}
