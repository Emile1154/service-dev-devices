package ru.emiljan.servicedevdevices.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.Image;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.ImageRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;

import java.io.IOException;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private static final int DEFAULT_IMAGE = 1;
    private static final long MAX_UPLOAD_SIZE = 1000000L;

    @Value("#{'${upload.image.types}'.split(',')}")
    private List<String> allowedTypes;

    public ImageService(ImageRepository imageRepository,
                        UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public void load(MultipartFile image, User user) throws IOException {
        if(image.getSize() == 0){
            throw new FileUploadException("Файл не загружен");
        }
        if(image.getSize()>MAX_UPLOAD_SIZE){
            throw new FileSizeLimitExceededException("Максимальный размер файла превышен", image.getSize(), MAX_UPLOAD_SIZE);
        }
        if(allowedTypes.stream().noneMatch(type->image.getContentType().contains(type))){
            throw new FileUploadException("Ошибка формата, поддерживаются только png/jpg/gif");
        }
        Image prevIcon = user.getImage();
        if(prevIcon.getId() != DEFAULT_IMAGE){
            imageRepository.delete(prevIcon);
        }
        Image newIcon = Image.builder()
                    .bytes(image.getBytes())
                    .contentType(image.getContentType())
                .build();
        imageRepository.save(newIcon);
        user.setImage(newIcon);
        userRepository.save(user);
    }

    public Image findImageById(Long id){
        return imageRepository.findById(id).orElse(null);
    }

}
