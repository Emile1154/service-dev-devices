package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.payment.PaypalPayment;
import ru.emiljan.servicedevdevices.services.payservices.PaymentService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/payment")
public class PaypalController {
    private final PaymentService paymentService;
    private static final String FAIL_PAY_PAGE = "/paypalFail";

    @Autowired
    public PaypalController(@Qualifier("paypalServiceImpl") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payment-successfully")
    public String thanks(Model model){
        return "payment/thanks";
    }

    @GetMapping("/capture")
    public String paymentDone(@RequestParam String token){
        if(paymentService.captureOrder(token)){
            paymentService.update(token);
            return "redirect:/users/orders";
        }
        return "redirect:"+FAIL_PAY_PAGE;
    }

    @PostMapping("/pay/current-order/{id}")
    public String initBuy(@PathVariable("id") Long orderId,
                             HttpServletRequest request,
                             @AuthenticationPrincipal UserDetails user){
        final URI captureUrl = buildCaptureUrl(request);
        PaypalPayment payment = (PaypalPayment) paymentService.createOrder(captureUrl, orderId);
        paymentService.save(payment, user.getUsername(), orderId);
        return "redirect:"+ payment.getPaypalApproveLink();
    }

    private URI buildCaptureUrl(HttpServletRequest request) {
        try {
            URI url = URI.create(request.getRequestURL().toString());
            return new URI(url.getScheme(),
                    url.getUserInfo(),
                    url.getHost(),
                    url.getPort(),
                    "/payment/capture",
                    null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
