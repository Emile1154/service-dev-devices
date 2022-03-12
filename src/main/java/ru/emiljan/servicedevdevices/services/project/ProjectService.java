package ru.emiljan.servicedevdevices.services.project;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.dto.CommentDTO;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.models.portfolio.Project;
import ru.emiljan.servicedevdevices.repositories.projectRepo.CommentRepository;
import ru.emiljan.servicedevdevices.repositories.projectRepo.ProjectRepository;
import ru.emiljan.servicedevdevices.services.UploadBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UploadBuilder uploadBuilder;
    private final TransferInfo transferInfo;
    private final FileService fileService;
    private final CommentRepository commentRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          UploadBuilder uploadBuilder,
                          @Qualifier("transferProject") TransferInfo transferInfo,
                          FileService fileService, CommentRepository commentRepository) {
        this.projectRepository = projectRepository;
        this.uploadBuilder = uploadBuilder;
        this.transferInfo = transferInfo;
        this.fileService = fileService;
        this.commentRepository = commentRepository;
    }

    public List<CommentDTO> getAllCommentsByProjectId(Long id, User user){
        return this.commentRepository.findAllByProjectId(id, user);
    }

    public List<Project> getAllProjects(){
        return this.projectRepository.findAll();
    }

    public Project getProjectById(Long id){
        return this.projectRepository.getById(id);
    }

    @Transactional
    public void save(Project project){
        this.projectRepository.save(project);
    }

    @Transactional
    public List<FileInfo> upload(List<MultipartFile> files) throws IOException {
        List<FileInfo> result = new ArrayList<>();
        if(files.stream().anyMatch(file -> file.getSize()==0)){
            throw new FileUploadException("Файл не загружен");
        }
        for(MultipartFile file : files){
            FileInfo f = this.uploadBuilder.uploadFile(file,transferInfo);
            fileService.save(f);
            result.add(f);
        }
        return result;
    }

    public List<FileInfo> getAllFilesByProjectId(Long id){
        return fileService.getAllFilesBProjectId(id);
    }

    @Transactional
    public void saveAllFileInfo(List<FileInfo> list){
        list.forEach(fileService::save);
    }

    @Transactional
    public void deleteById(Long id){
        fileService.deleteAllByProjectId(id, transferInfo);
        projectRepository.deleteById(id);
    }

    @Transactional
    public ResponseEntity<?> drawImage(Long id) throws IOException {
        FileInfo info = fileService.getFileInfoById(id);
        String filename = info.getFilename();
        MediaType mediaType = MediaType.valueOf(info.getContentType());
        File file = new File(transferInfo.getPath()+filename);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new InputStreamResource(new ByteArrayInputStream(Files.readAllBytes(file.toPath()))));
    }


}
