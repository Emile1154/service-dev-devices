package ru.emiljan.servicedevdevices.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class UploadBuilderTest {

    @Autowired
    private UploadBuilder uploadBuilder;

    @MockBean
    private @Qualifier("transferProject") TransferInfo transferInfo;

    @BeforeEach
    void setUp() {
        String types = "application/pdf,image/png,image/jpeg";

        transferInfo = TransferInfo.builder()
                    .allowedTypes(Arrays.asList(types.split(",")))
                    .path("C:/Users/HOME-PC/Desktop/project/projects/")
                    .id(2)
                .build();
    }

    @Test
    void uploadFile() throws IOException {
//        File testFile = new File("test.jpg");
//        if (testFile.createNewFile()) {
//            FileWriter writer = new FileWriter(testFile);
//            writer.write("this is a test file");
//            writer.close();
//        }
//        FileInputStream fileInputStream = new FileInputStream(testFile);
//        MultipartFile mf =
//                new MockMultipartFile(
//                        "file",
//                        testFile.getName(),
//                        "image/jpeg",
//                        fileInputStream
//                );
//        FileInfo fileInfo = uploadBuilder.uploadFile(mf, transferInfo);
//        Assertions.assertNotNull(fileInfo.getFilename());
//        Assertions.assertNotNull(fileInfo.getContentType());
    }
}