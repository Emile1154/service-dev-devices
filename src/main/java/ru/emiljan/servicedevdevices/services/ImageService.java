package ru.emiljan.servicedevdevices.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.Image;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.ImageRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;

import java.io.IOException;

/**
 * @author EM1LJAN
 */
@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private static final int DEFAULT_IMAGE = 1;

    public ImageService(ImageRepository imageRepository,
                        UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public void load(MultipartFile image, User user){
        try{
            if(image.getBytes().length == 0){
                return;
            }
            Image prevIcon = user.getImage();
            if(prevIcon.getId() != DEFAULT_IMAGE){
                imageRepository.delete(prevIcon);
            }

            String icon64 = Base64.encodeBase64String(image.getBytes());
            Image newIcon = new Image(icon64);
            imageRepository.save(newIcon);
            user.setImage(newIcon);
            userRepository.save(user);

        }catch (IOException e){
            System.out.println(e);
        }
    }

}
