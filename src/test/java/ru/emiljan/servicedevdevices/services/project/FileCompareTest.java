package ru.emiljan.servicedevdevices.services.project;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileCompareTest {

    private File file1;
    private File file2;

    @BeforeEach
    public void setUp(){
        file1 = new File("./src/main/resources/resources/test/compare1.txt");
        file2 = new File("./src/main/resources/resources/test/compare2.txt");
        try(FileWriter writer1 = new FileWriter(file1);
            FileWriter writer2 = new FileWriter(file2)){
            writer1.write("test text");
            writer2.write("test text");
            writer1.close();
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void compareTrue() throws IOException {
        boolean result = FileCompare.compare(file1, file2);
        Assertions.assertTrue(result);
    }

    @Test
    void compareFalse() throws IOException {
        FileWriter writer = new FileWriter(file2);
        writer.write("another text");
        writer.close();
        boolean result = FileCompare.compare(file1, file2);
        Assertions.assertFalse(result);
    }

}