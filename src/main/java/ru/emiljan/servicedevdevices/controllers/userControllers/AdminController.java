package ru.emiljan.servicedevdevices.controllers.userControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.Role;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.RoleRepository;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Set;

/**
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService,
                           OrderService orderService,
                           RoleRepository roleRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/users")
    public String index(Model model, Principal user,
                        @RequestParam(value = "columns", required = false)
                                List<String> columns,String keyword){
        final User currentUser = this.userService.findUserByNickname(user.getName());
        model.addAttribute("user", currentUser);
        model.addAttribute("alarm",this.userService.checkNewNotifies(currentUser));
        model.addAttribute("key", keyword);
        if(columns!=null){
            model.addAttribute("users", userService.getUsersByKeyword(columns, keyword));
            return "admin/index";
        }
        model.addAttribute("users", userService.findAll());
        return "admin/index";
    }

    @PostMapping("/delete-users/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/set-role/{id}")
    public String setRole(@PathVariable("id") Long id, @RequestParam(value = "role", required = false)
            List<String> listRole){
        Set<Role> roles = this.roleRepository.findAllByRoles(listRole);
        User user = this.userService.findById(id);
        user.setRoles(roles);
        userService.update(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/lock/{id}")
    public String banUser(@PathVariable("id") Long id){
        User user = userService.findById(id);

        if(user != null){
            userService.banUserById(id, !user.isAccountNonLocked());
        }
        return "redirect:/admin/users";
    }

    @GetMapping
    public String adminPanel(Model model, Principal user){
        final User currentUser = this.userService.findUserByNickname(user.getName());
        model.addAttribute("user",currentUser);
        model.addAttribute("alarm",this.userService.checkNewNotifies(currentUser));
        return "admin/admin_menu";
    }

    @PostMapping("/delete-order/{id}")
    public String deleteOrder(@PathVariable("id") Long id){
        orderService.deleteOrderById(id);
        return "redirect:/manager/orders";
    }
}
