package ru.emiljan.servicedevdevices.services.project;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileCompare {
    public static boolean compare(File file1, File file2) throws IOException {
        try (RandomAccessFile randomAccessFile1 = new RandomAccessFile(file1, "r");
             RandomAccessFile randomAccessFile2 = new RandomAccessFile(file2, "r")) {

            FileChannel ch1 = randomAccessFile1.getChannel();
            FileChannel ch2 = randomAccessFile2.getChannel();
            if (ch1.size() != ch2.size()) {
                return false;
            }
            long size = ch1.size();
            MappedByteBuffer m1 = ch1.map(FileChannel.MapMode.READ_ONLY, 0L, size);
            MappedByteBuffer m2 = ch2.map(FileChannel.MapMode.READ_ONLY, 0L, size);

            return m1.equals(m2);
        }
    }

    public static File multipartFileToFile(MultipartFile multipartFile, String dir) throws IOException {
        Path templateFolder = Path.of(dir+"/Template");
        if(Files.notExists(templateFolder)){
            Files.createDirectory(templateFolder);
        }
        File tempFile = File.createTempFile(multipartFile.getOriginalFilename(),".tmp",templateFolder.toFile());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }
}
