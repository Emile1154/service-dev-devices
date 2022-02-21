package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.payment.Parameters;
import ru.emiljan.servicedevdevices.models.payment.VKPayPayment;
import ru.emiljan.servicedevdevices.services.payservices.PaymentService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/vk-pay")
public class VKPayController {

    private final PaymentService paymentService;

    @Autowired
    public VKPayController(@Qualifier("VKPayServiceImpl") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Parameters getParams(@PathVariable("id") Long orderId,
                              @AuthenticationPrincipal UserDetails user,
                              HttpServletRequest request){
        VKPayPayment payment = (VKPayPayment) paymentService
                .createOrder(buildCaptureUrl(request), orderId);
        paymentService.save(payment,user.getUsername(),orderId);
        return payment.getParam();
    }

    private URI buildCaptureUrl(HttpServletRequest request) {
        try {
            URI url = URI.create(request.getRequestURL().toString());
            return new URI(url.getScheme(),
                    url.getUserInfo(),
                    url.getHost(),
                    url.getPort(),
                    "/api/v1/vk-pay/capture",
                    null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
