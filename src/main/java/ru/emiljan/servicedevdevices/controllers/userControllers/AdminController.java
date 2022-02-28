package ru.emiljan.servicedevdevices.controllers.userControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;

import java.security.Principal;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public AdminController(UserService userService,
                           OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/users")
    public String index(Model model, Principal user,
                        @RequestParam(value = "columns", required = false)
                                List<String> columns,String keyword){
        model.addAttribute("user", userService.findUserByNickname(user.getName()));
        model.addAttribute("key", keyword);
        if(columns!=null){
            model.addAttribute("users", userService.getUsersByKeyword(columns, keyword));
            return "admin/index";
        }
        model.addAttribute("users", userService.findAll());
        return "admin/index";
    }

    @PostMapping("/delete-users/{id}")
    @DeleteMapping("/delete-users/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/lock/{id}")
    public String banUser(@PathVariable("id") Long id){
        User user = userService.findById(id);
        if(user != null){
            userService.BanUserById(id, !user.isAccountNonLocked());
        }
        return "redirect:/admin/users";
    }

    @GetMapping
    public String adminPanel(Model model, Principal user){
        model.addAttribute("user",
                userService.findUserByNickname(user.getName()));
        return "admin/admin_menu";
    }


    @PostMapping("/delete-order/{id}")
    @DeleteMapping("/delete-order/{id}")
    public String deleteOrder(@PathVariable("id") Long id){
        orderService.deleteOrderById(id);
        return "redirect:/manager/orders";
    }
}
