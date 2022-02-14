package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model,
                       @AuthenticationPrincipal UserDetails currentUser){
        model.addAttribute("user", userService.findById(id));
        if(currentUser != null){
            User user = userService.findUserByNickname(currentUser.getUsername());
            model.addAttribute("currentUser", user);
            model.addAttribute("roles", user.getRoles());
        }
        return "users/show";
    }

    @GetMapping("/orders")
    public String myOrders(Model model,
                           @AuthenticationPrincipal UserDetails currentUser){
        if(currentUser != null){
            User user = userService.findUserByNickname(currentUser.getUsername());
            model.addAttribute("user", user);
            model.addAttribute("orders", orderService.
                    findOrdersByUserId(user.getId()));
        }
        return "orders/orders_list";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user,
                               @AuthenticationPrincipal UserDetails person){
        if(person != null){
            return "redirect:/users/"+userService.
                    findUserByNickname(person.getUsername()).getId();
        }
        return "authorization/registration";
    }

    @PostMapping
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult, Model model){
        List<String> errors = userService.checkRepeats(user);
        if(!errors.isEmpty()){
            for (int i = 0; i <= errors.size()/2 + 1; i+=2) {
                bindingResult.rejectValue(errors.get(i), "error.user",
                        errors.get(i+1));
            }
        }
        if(bindingResult.hasErrors()){
            return "authorization/registration";
        }

        userService.saveUser(user);
        model.addAttribute("msg", "На вашу почту было отправленно письмо с " +
                "ссылкой для активации аккаунта, вы сможете авторизоваться только после подтверждения " +
                "вашего аккаунта.");
        return "authorization/login";
    }

    @GetMapping("/login")
    public String entry(@AuthenticationPrincipal UserDetails user,
                        HttpServletRequest request, Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout){
        if(user != null){
            return "redirect:/users/"+userService.
                    findUserByNickname(user.getUsername()).getId();
        }
        if(error != null){
            model.addAttribute("error", getErrorMessage(request));
        }
        if(logout != null){
            model.addAttribute("msg", "Вы вышли из аккаунта");
        }
        return "authorization/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable("code") String code){
        boolean isActivated = userService.activateUser(code);
        if(isActivated){
            model.addAttribute("msg",
                    "Ваш аккаунт активирован! Теперь вы можете войти");
            return "authorization/login";
        }
        return null;
    }

    private String getErrorMessage(HttpServletRequest request){

        Exception exception = (Exception) request.getSession()
                .getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        String error;
        if(exception instanceof InternalAuthenticationServiceException) {
            error = exception.getMessage();
        }
        else{
            error = "Неверный пароль";
        }
        return error;
    }


}
