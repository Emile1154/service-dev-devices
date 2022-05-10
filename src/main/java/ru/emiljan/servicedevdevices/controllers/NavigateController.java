package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.UserService;

import java.security.Principal;

/**
 * Navigation controller class.
 *
 * @author EM1LJAN
 */
@Controller
public class NavigateController {
    private final UserService userService;

    @Autowired
    public NavigateController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String menu(Model model, Principal currentUser){
        if(currentUser != null){
            final User user = this.userService.findUserByNickname(currentUser.getName());
            model.addAttribute("user", user);
            model.addAttribute("alarm",this.userService.checkNewNotifies(user));
        }
        return "navigation/main_menu";
    }

    @GetMapping("/developing")
    public String develop(Model model, Principal currentUser){
        if(currentUser != null){
            final User user = this.userService.findUserByNickname(currentUser.getName());
            model.addAttribute("user", user);
            model.addAttribute("alarm",this.userService.checkNewNotifies(user));
        }
        return "navigation/dev_menu";
    }

    @GetMapping("/about")
    public String about(Model model, Principal currentUser){
        if(currentUser != null){
            final User user = this.userService.findUserByNickname(currentUser.getName());
            model.addAttribute("currentUser", user);
            model.addAttribute("alarm",this.userService.checkNewNotifies(user));
        }
        model.addAttribute("admins",userService.findAllByRole(2L));
        model.addAttribute("developers",userService.findAllByRole(4L));
        model.addAttribute("managers",userService.findAllByRole(3L));
        return "navigation/about";
    }

    @GetMapping("/contacts")
    public String contacts(Model model, Principal currentUser){
        if(currentUser != null){
            final User user = this.userService.findUserByNickname(currentUser.getName());
            model.addAttribute("currentUser", user);
            model.addAttribute("alarm",this.userService.checkNewNotifies(user));
        }
        return "navigation/contacts";
    }

}
