package ru.emiljan.servicedevdevices.services.project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.order.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private FileService fileService;

    private FileInfo fileInfo;

    @BeforeEach
    void setUp() {
        fileInfo = FileInfo.builder()
                    .id(99L)
                    .filename("test.png")
                    .contentType("image/png")
                .build();
    }


    @Test
    void upload() throws IOException {
        File newIcon = new File("test.jpg");
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
        List<FileInfo> result = projectService.upload(List.of(mf));
        boolean b = result.size() <= 1;
        Assertions.assertTrue(b);
    }



    @Test
    void drawImage() throws IOException {
        Long id = 99L;
        Mockito.when(fileService.getFileInfoById(id)).thenReturn(fileInfo);
        ResponseEntity<?> responseEntity = projectService.drawImage(id);
        Assertions.assertEquals("200 OK", responseEntity.getStatusCode().toString());

    }
}