package ru.emiljan.servicedevdevices.controllers.userControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;

/**
 * Controller class for {@link ru.emiljan.servicedevdevices.models.User} with Developer role
 *
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/developer")
public class DeveloperController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public DeveloperController(UserService userService,
                               OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/orders/{id}")
    public String viewOrders(@PathVariable("id") Long id, Model model,
                             @AuthenticationPrincipal UserDetails user){
        User checkoutUser = this.userService.findById(id);
        if(checkoutUser == null){
            return null; // user not found add
        }
        final User currentUser = this.userService.findUserByNickname(user.getUsername());

        model.addAttribute("user", currentUser);
        model.addAttribute("orders", this.orderService.findOrdersByUserId(id));
        model.addAttribute("alarm",this.userService.checkNewNotifies(currentUser));
        return "orders/orders_list";
    }
}
