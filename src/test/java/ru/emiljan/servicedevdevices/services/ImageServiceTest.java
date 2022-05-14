package ru.emiljan.servicedevdevices.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.Image;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.ImageRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private Image image;
    @BeforeEach
    void setUp() {
        image = Image.builder()
                    .id(99L)
                .build();
        user = User.builder()
                    .nickname("test")
                    .password("pwd")
                    .phone("+7(999)999-99-99")
                    .email("test@test.ru")
                    .image(image)
                .build();
    }

    @Test
    void load() throws IOException {
        Long id = user.getImage().getId();
        File newIcon = new File("./src/main/resources/resources/test/test.jpg");
        if(newIcon.length() == 0){
            FileWriter writer = new FileWriter(newIcon);
            writer.write("icon");
            writer.close();
        }
        FileInputStream fileInputStream = new FileInputStream(newIcon);
        MultipartFile mf =
                new MockMultipartFile(
                        "file",
                        newIcon.getName(),
                        "image/jpeg",
                        fileInputStream
                );
        imageService.load(mf, user);
        if(id != 1){
            Mockito.verify(imageRepository, Mockito.times(1)).delete(
                    ArgumentMatchers.any()
            );
        }
        Mockito.verify(imageRepository, Mockito.times(1)).save(
            ArgumentMatchers.any()
        );
        Mockito.verify(userRepository, Mockito.times(1)).save(
                ArgumentMatchers.eq(user)
        );
        Assertions.assertNotEquals(user.getImage(), image);
    }

    @Test
    void findImageById() {
        Long id = 99L;
        Mockito.when(imageRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(image));
        Image f = this.imageService.findImageById(id);
        Assertions.assertEquals(image, f);
    }
}