package ru.emiljan.servicedevdevices.services.project;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
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

            ch1.close();
            ch2.close();

            boolean result = m1.equals(m2);

            //closing MappedByteBuffer.class
            closeByteBuffer(m1);
            closeByteBuffer(m2);

            return result;
        }
    }

    private static void closeByteBuffer(MappedByteBuffer buffer){
        try{
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            Method invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
            invokeCleaner.invoke(unsafe, buffer);
        }catch (NoSuchFieldException | ClassNotFoundException |
                InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
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
