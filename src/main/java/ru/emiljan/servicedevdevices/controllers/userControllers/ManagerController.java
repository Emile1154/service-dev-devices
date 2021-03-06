package ru.emiljan.servicedevdevices.controllers.userControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.order.CustomOrder;
import ru.emiljan.servicedevdevices.models.Status;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.payrepo.PaymentRepository;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;
import ru.emiljan.servicedevdevices.specifications.PaymentSpecification;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/**
 * Controller class for {@link ru.emiljan.servicedevdevices.models.User} with Manager role
 *
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/manager")
public class ManagerController {
    private final UserService userService;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;

    @Autowired
    public ManagerController(UserService userService,
                             OrderService orderService,
                             PaymentRepository paymentRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/orders")
    public String indexOrders(Model model, Principal user,
                              @RequestParam(value = "columns", required = false)
                                      List<String> columns, String keyword){
        final User currentUser = this.userService.findUserByNickname(user.getName());
        model.addAttribute("user", currentUser);
        model.addAttribute("alarm",this.userService.checkNewNotifies(currentUser));
        if(columns!=null){
            model.addAttribute("key", keyword);
            model.addAttribute("orders",
                    orderService.findOrdersByKeyword(columns, keyword));
            return "admin/index_orders";
        }
        model.addAttribute("orders", orderService.findAllOrders());
        return "admin/index_orders";
    }

    @PostMapping("/orders/update/{id}")
    public String setOrderStatus(@PathVariable("id") Long id, String input,
                                 BigDecimal price, HttpServletRequest request){
        CustomOrder order = orderService.findById(id);
        if(price.equals(BigDecimal.ZERO) || price.equals(order.getPrice())){
            orderService.update(order, Status.valueOf(input),request);
            return "redirect:/manager/orders";
        }
        orderService.update(order,price,request);
        return "redirect:/manager/orders";
    }

    @GetMapping("/payments")
    public String paymentsAll(Model model, @AuthenticationPrincipal UserDetails user,
                              @RequestParam(value = "columns", required = false)
                                      List<String> columns, String keyword){
        final User currentUser = this.userService.findUserByNickname(user.getUsername());
        model.addAttribute("user", currentUser);
        model.addAttribute("alarm",this.userService.checkNewNotifies(currentUser));
        if(columns!=null){
            model.addAttribute("key",keyword);
            model.addAttribute("history",
                    this.paymentRepository.findAll(PaymentSpecification.findByKeyword(keyword,columns)));
            return "payment/payment_list";
        }
        model.addAttribute("history", this.paymentRepository.findAll());
        return "payment/payment_list";
    }

    @GetMapping("/payments/{id}")
    public String viewPayments(@PathVariable("id") Long id, Model model,
                                @AuthenticationPrincipal UserDetails currentUser){
        final User checkoutUser = this.userService.findById(id);
        if(checkoutUser==null){
            return null;
        }
        final User user = this.userService.findUserByNickname(currentUser.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("alarm",this.userService.checkNewNotifies(user));
        model.addAttribute("history", this.userService.findAllPayListByUserId(checkoutUser.getId()));
        return "payment/payment_list";
    }

}
