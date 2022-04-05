package ru.emiljan.servicedevdevices.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.models.portfolio.Project;
import ru.emiljan.servicedevdevices.repositories.projectRepo.ProjectRepository;
import ru.emiljan.servicedevdevices.repositories.orderRepo.FileRepository;
import ru.emiljan.servicedevdevices.repositories.orderRepo.OrderRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author EM1LJAN
 */
@Component
public class UploadBuilder {
    private final FileRepository fileRepository;
    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public UploadBuilder(FileRepository fileRepository, OrderRepository orderRepository, ProjectRepository projectRepository) {
        this.fileRepository = fileRepository;
        this.orderRepository = orderRepository;
        this.projectRepository = projectRepository;
    }

    public FileInfo uploadFile(MultipartFile file, TransferInfo transferInfo) throws IOException {
        final String path = transferInfo.getPath();
        final String contentType = file.getContentType();
        checkTypes(transferInfo,contentType);
        Path dir = Path.of(path);
        if(Files.notExists(dir)){
            Files.createDirectory(dir);
        }
        String filename = createFilename(transferInfo.getId())+getSuffixFile(file.getOriginalFilename());
        Path absolutePath = Path.of(path + filename);
        Files.copy(file.getInputStream(),absolutePath);
        return FileInfo.builder()
                    .filename(filename)
                    .contentType(contentType)
                .build();
    }

    public FileInfo uploadFile(File file, TransferInfo transferInfo) throws IOException {
        final String path = transferInfo.getPath();
        final String contentType = Files.probeContentType(file.toPath());
        checkTypes(transferInfo, contentType);
        Path dir = Path.of(path);
        if(Files.notExists(dir)){
            Files.createDirectory(dir);
        }
        String filename = createFilename(transferInfo.getId())+getSuffixFile(file.getName());
        Path absolutePath = Path.of(path + filename);
        return null;
    }

    private void checkTypes(TransferInfo transferInfo, String contentType) throws FileUploadException {
        if(transferInfo.getAllowedTypes().stream().noneMatch(type->contentType.contains(type))){
            String types = StringUtils.join(transferInfo.getAllowedTypes(),",");
            throw new FileUploadException("Ошибка формата, поддерживаются только: " + types);
        }
    }

    private String createFilename(int id){
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy"));
        if(id==1){
            return "id#"+(this.orderRepository.getMaxId()+1)+"_"+date;
        }
        return "project#"+(this.projectRepository.getMaxId()+1)+"_"+(this.fileRepository.getMaxId()+1)+"_"+date;
    }

    private String getSuffixFile(String filename){
        int dotIndex = filename.lastIndexOf(".");
        if(dotIndex != 0){
            return filename.substring(dotIndex);
        }
        return null;
    }
}
