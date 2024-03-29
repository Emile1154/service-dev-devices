package ru.emiljan.servicedevdevices.controllers.userControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * Controller class for {@link ru.emiljan.servicedevdevices.models.User}
 *
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService,
                          OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    @Transactional
    public String show(@PathVariable("id") Long id, Model model,
                       @AuthenticationPrincipal UserDetails currentUser){
        User user = userService.findById(id);
        model.addAttribute("user", user);
        if(currentUser != null){
            User authUser = userService.findUserByNickname(currentUser.getUsername());
            model.addAttribute("alarm",this.userService.checkNewNotifies(authUser));
            boolean authority = authUser.getRoles().stream().anyMatch(role->role.getId()>=2);   // id=1 -> USER
                                                                                               // id=2 -> ADMIN
            model.addAttribute("currentUser", authUser);                          // id=3 -> MANAGER
            model.addAttribute("vip", authority);
        }
        return "users/show";
    }

    @GetMapping("/orders")
    public String myOrders(Model model,
                           @AuthenticationPrincipal UserDetails currentUser){
        if(currentUser != null){
            User user = this.userService.findUserByNickname(currentUser.getUsername());
            model.addAttribute("alarm",this.userService.checkNewNotifies(user));
            model.addAttribute("user", user);
            model.addAttribute("orders",
                    this.orderService.findOrdersByUserId(user.getId()));
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
                         BindingResult bindingResult, Model model, boolean accept){
        Map<String,String> errors = userService.checkRepeats(user);
        model.addAttribute("ch", accept);
        if(!errors.isEmpty()){
            for(Map.Entry<String,String> pair : errors.entrySet() ){
                bindingResult.rejectValue(pair.getKey(),"error.user", pair.getValue());
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
            model.addAttribute("error", getErrorMsg(request));
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

    @GetMapping("/checklist/{id}")
    public String getCheckList(@PathVariable("id") Long id, Model model,
                               @AuthenticationPrincipal UserDetails currentUser){
        final User user = this.userService.findUserByNickname(currentUser.getUsername());
        if(id == user.getId()){
            model.addAttribute("user",user);
            model.addAttribute("alarm",this.userService.checkNewNotifies(user));
            model.addAttribute("history", this.userService.findAllPayListByUserId(id));
            return "payment/payment_list";
        }
        return "redirect:/users/"+user.getId();
    }

    private String getErrorMsg(HttpServletRequest request){
        Exception ex = (Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if(ex instanceof InternalAuthenticationServiceException){
            return ex.getMessage();
        }
        return "Пароль неверный";
    }
}
