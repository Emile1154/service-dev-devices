package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.emiljan.servicedevdevices.services.UserService;

import java.security.Principal;

/**
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
    public String menu(Model model, Principal user){
        if(user != null){
            model.addAttribute("user", userService.findUserByNickname(user.getName()));
        }
        return "navigation/main_menu";
    }

    @GetMapping("/developing")
    public String develop(Model model, Principal user){
        if(user != null){
            model.addAttribute("user", userService.findUserByNickname(user.getName()));
        }
        return "navigation/dev_menu";
    }
    @GetMapping("/collection")
    public String portfolio(Model model, Principal user){
        if(user != null){
            model.addAttribute("user", userService.findUserByNickname(user.getName()));
        }
        return "navigation/portfolio";
    }

    @GetMapping("/about")
    public String about(Model model, Principal user){
        if(user != null){
            model.addAttribute("user", userService.findUserByNickname(user.getName()));
        }
        return "navigation/about";
    }

    @GetMapping("/contacts")
    public String contacts(Model model, Principal user){
        if(user != null){
            model.addAttribute("user", userService.findUserByNickname(user.getName()));
        }
        return "navigation/contacts";
    }

}
