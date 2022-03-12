package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.Image;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.ImageService;
import ru.emiljan.servicedevdevices.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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
    public String loadImage(Principal user,
                            @RequestParam("image") MultipartFile image, HttpServletRequest request) throws IOException {
        User authUser = userService.findUserByNickname(user.getName());
        Map<String,Object> map = new HashMap<>();
        map.put("user",authUser);
        map.put("currentUser",authUser);
        map.put("vip", true);
        request.setAttribute("map",map);
        imageService.load(image, authUser);
        return "redirect:/users/"+authUser.getId();
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<?> viewImage(@PathVariable("id") Long id){
        Image image = imageService.findImageById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(image.getContentType()))
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }
}
