package ru.emiljan.servicedevdevices.controllers.userControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.CustomOrder;
import ru.emiljan.servicedevdevices.models.Status;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.payrepo.PaymentRepository;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;
import ru.emiljan.servicedevdevices.specifications.PaymentSpecification;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

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
        model.addAttribute("user",
                userService.findUserByNickname(user.getName()));
        if(columns!=null){
            model.addAttribute("key", keyword);
            model.addAttribute("orders",
                    orderService.findOrdersByKeyword(columns, keyword));
            return "admin/index_orders";
        }
        model.addAttribute("orders",
                orderService.findAllOrders());
        return "admin/index_orders";
    }

    @PostMapping("/orders/update/{id}")
    public String setOrderStatus(@PathVariable("id") Long id, String input,
                                 BigDecimal price){
        CustomOrder order = orderService.findById(id);
        if(price.equals(BigDecimal.ZERO) || price.equals(order.getPrice())){
            orderService.update(order, Status.valueOf(input));
            return "redirect:/admin/orders";
        }
        orderService.update(order,price);
        return "redirect:/admin/orders";
    }

    @GetMapping("/payments")
    public String paymentsAll(Model model, @AuthenticationPrincipal UserDetails user,
                              @RequestParam(value = "columns", required = false)
                                      List<String> columns, String keyword){
        model.addAttribute("user",
                this.userService.findUserByNickname(user.getUsername()));
        if(columns!=null){
            model.addAttribute("key",keyword);
            model.addAttribute("history",
                    this.paymentRepository.findAll(PaymentSpecification.findByKeyword(keyword,columns)));
            return "payment/payment_list";
        }
        model.addAttribute("history", this.paymentRepository.findAll());
        return "payment/payment_list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrders(@PathVariable("id") Long id, Model model,
                             @AuthenticationPrincipal UserDetails moder){
        User user = this.userService.findById(id);
        if(user==null){
            return null;
        }
        model.addAttribute("user", this.userService.findUserByNickname(moder.getUsername()));
        model.addAttribute("orders", this.orderService.findOrdersByUserId(user.getId()));
        return "orders/orders_list";
    }

    @GetMapping("/payments/{id}")
    public String viewPayments(@PathVariable("id") Long id, Model model,
                                @AuthenticationPrincipal UserDetails moder){
        User user = this.userService.findById(id);
        if(user==null){
            return null;
        }
        model.addAttribute("user", this.userService.findUserByNickname(moder.getUsername()));
        model.addAttribute("history", this.paymentRepository.findPaymentsByUserId(user.getId()));
        return "payment/payment_list";
    }
}
