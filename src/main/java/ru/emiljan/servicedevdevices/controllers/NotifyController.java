package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.emiljan.servicedevdevices.models.Notify;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.UserService;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;

import java.util.List;

/**
 * Controller class for {@link ru.emiljan.servicedevdevices.models.Notify}
 *
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/users/notify")
public class NotifyController {

    private final NotifyService notifyService;
    private final UserService userService;

    @Autowired
    public NotifyController(NotifyService notifyService, UserService userService) {
        this.notifyService = notifyService;
        this.userService = userService;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails user,
                        Model model){
        final User currentUser = this.userService.findUserByNickname(user.getUsername());
        List<Notify> notifies = notifyService.findAllByUser(currentUser);
        model.addAttribute("notifies", notifies);
        model.addAttribute("alarm", this.userService.checkNewNotifies(currentUser));
        model.addAttribute("user", currentUser);
        return "notify/notify";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable("id") Long id, Model model,
                       @AuthenticationPrincipal UserDetails user){
        Notify notify = this.notifyService.findById(id);
        model.addAttribute("notify", notify);
        notifyService.updateNotify(notify);
        model.addAttribute("alarm",
                this.userService.checkNewNotifies(
                        this.userService.findUserByNickname(user.getUsername())));
        return "notify/show";
    }

}
