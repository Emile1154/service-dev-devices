package ru.emiljan.servicedevdevices.controllers.payControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.payment.PaypalPayment;
import ru.emiljan.servicedevdevices.services.URIBuilder;
import ru.emiljan.servicedevdevices.services.UserService;
import ru.emiljan.servicedevdevices.services.payservices.PaymentService;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller class for {@link ru.emiljan.servicedevdevices.models.payment.PaypalPayment}
 *
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/payment")
public class PaypalController {
    private final PaymentService paymentService;
    private final UserService userService;
    private static final String FAIL_PAY_PAGE = "/payment/failed";

    @Autowired
    public PaypalController(@Qualifier("paypalServiceImpl") PaymentService paymentService,
                            UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @GetMapping("/successfully")
    public String thanks(Model model, @AuthenticationPrincipal UserDetails user){
        final User buyer = this.userService.findUserByNickname(user.getUsername());
        model.addAttribute("user",buyer);
        model.addAttribute("alarm", this.userService.checkNewNotifies(buyer));
        return "payment/thanks";
    }

    @GetMapping("/failed")
    public String fail(Model model, @AuthenticationPrincipal UserDetails user){
        final User buyer = this.userService.findUserByNickname(user.getUsername());
        model.addAttribute("user",buyer);
        model.addAttribute("alarm", this.userService.checkNewNotifies(buyer));
        return "payment/failed";
    }

    @PostMapping("/capture")
    public String paymentDone(@RequestParam String token, HttpServletRequest request){
        if(paymentService.captureOrder(token)){
            paymentService.update(token, request);
            return "redirect:/payment/successfully";
        }
        return "redirect:/"+FAIL_PAY_PAGE;
    }

    @PostMapping("/pay/current-order/{id}")
    public String initBuy(@PathVariable("id") Long orderId,
                             HttpServletRequest request,
                             @AuthenticationPrincipal UserDetails user){
        PaypalPayment payment = (PaypalPayment)
                paymentService.createOrder(URIBuilder
                        .buildURI(request,"/payment/capture"), orderId);
        paymentService.save(payment, user.getUsername(), orderId);
        return "redirect:/"+payment.getPaypalApproveLink();
    }
}
