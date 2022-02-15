package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.ImageService;
import ru.emiljan.servicedevdevices.services.UserService;

import java.security.Principal;

/**
 * @author EM1LJAN
 */
@Controller
public class ImageController {
    private final ImageService imageService;
    private final UserService userService;

    @Autowired
    public ImageController(ImageService imageService,
                           UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    @PostMapping("/add-image")
    public String loadImage( Principal user,
                             @RequestParam("image") MultipartFile image){
        User authUser = userService.findUserByNickname(user.getName());
        imageService.load(image, authUser);
        return "redirect:/users/"+authUser.getId();
    }

}
