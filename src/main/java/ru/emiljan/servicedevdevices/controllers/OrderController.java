package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.Order;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model,
                       @AuthenticationPrincipal UserDetails currentUser){

        User user = userService.findUserByNickname(currentUser.getUsername());
        Order order = orderService.findById(id);
        User customer = order.getUser();

        if(user == customer || user.getRoles().stream().anyMatch(role ->
                role.getId()==2)){
            model.addAttribute("customer", customer);
            model.addAttribute("order", order);
            model.addAttribute("user", user);
            return "orders/show_order";
        }
        return "error403";
    }

    @GetMapping("/new")
    public String newOrder(@ModelAttribute("order") Order order,
                           Model model, Principal user){
        model.addAttribute("user",
                userService.findUserByNickname(user.getName()));
        return "orders/create_order";
    }

    @PostMapping
    public String createOrder(Model model, Principal user,
                              @ModelAttribute("order") @Valid Order order,
                              BindingResult bindingResult){
        User customer = userService.findUserByNickname(user.getName());
        model.addAttribute("user", customer);
        List<String> errors = orderService.checkRepeats(order);
        if(!errors.isEmpty()){
            for (int i = 0; i < errors.size()/2+1; i+=2) {
                bindingResult.rejectValue(errors.get(i), "error.user",
                        errors.get(i+1));
            }
        }
        if(bindingResult.hasErrors()){
            return "orders/create_order";
        }
        order.setUser(customer);
        orderService.saveOrder(order);
        return "success";
    }
}
