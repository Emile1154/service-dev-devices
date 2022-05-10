package ru.emiljan.servicedevdevices.controllers.payControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.payment.Parameters;
import ru.emiljan.servicedevdevices.models.payment.VKPayPayment;
import ru.emiljan.servicedevdevices.services.URIBuilder;
import ru.emiljan.servicedevdevices.services.payservices.PaymentService;

import javax.servlet.http.HttpServletRequest;

/**
 * RestController class for {@link ru.emiljan.servicedevdevices.models.payment.VKPayPayment}
 *
 * @author EM1LJAN
 */
@RestController
@RequestMapping("/api/v1/vk-pay")
public class VKPayController {

    private final PaymentService paymentService;

    @Autowired
    public VKPayController(@Qualifier("VKPayServiceImpl") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/capture", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String paymentDone(HttpEntity<String> httpEntity){
        if(paymentService.captureOrder(httpEntity.getBody())){
            return "redirect:/payment/successfully";
        }
        return "redirect:/payment/failed";
    }

    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Parameters getParams(@PathVariable("id") Long orderId,
                              @AuthenticationPrincipal UserDetails user,
                              HttpServletRequest request){
        VKPayPayment payment = (VKPayPayment) paymentService
                .createOrder(URIBuilder.buildURI(request,"/api/v1/vk-pay/capture"), orderId);
        paymentService.save(payment,user.getUsername(),orderId);
        return payment.getParam();
    }
}
