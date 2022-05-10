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
import ru.emiljan.servicedevdevices.models.dto.ProjectDTO;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.models.portfolio.Project;
import ru.emiljan.servicedevdevices.repositories.projectRepo.CommentRepository;
import ru.emiljan.servicedevdevices.repositories.projectRepo.ProjectRepository;
import ru.emiljan.servicedevdevices.services.UploadBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for {@link ru.emiljan.servicedevdevices.models.portfolio.Project}
 *
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

    public ProjectDTO getDTOById(Long id){
        return this.projectRepository.getDTOById(id);
    }

    public Project getProjectById(Long id){
        return this.projectRepository.getById(id);
    }

    @Transactional
    public void save(Project project){
        this.projectRepository.save(project);
    }

    /**
     * the method checks if the transferred files have already been loaded before
     * @param uploadFiles list of uploaded files pdf/png/jpg
     * @return map with two list, 1 - need load, 2 - has been loaded
     * @throws IOException
     */
    private Map<Integer,List<?>> checksToCompare(List<MultipartFile> uploadFiles) throws IOException {
        File[] files =  new File(transferInfo.getPath()).listFiles();
        if (files == null){
            return null;
        }
        Map<Integer,List<?>> result = new HashMap<>();
        List<File> loaded = new ArrayList<>();
        List<MultipartFile> copy = new ArrayList<>(uploadFiles);
        for (MultipartFile uf : uploadFiles){
            for(File f : files){
                if(f.length() == uf.getSize()){
                    if(FileCompare.compare(f, FileCompare.multipartFileToFile(uf,transferInfo.getPath()))){
                        copy.remove(uf);
                        loaded.add(f);
                    }
                }
            }
        }
        result.put(1, copy); // need load (Multipart format)
        result.put(2, loaded);  // has been loaded
        return result;
    }

    /**
     * upload files method
     * @param files pdf/png/jpg files format
     * @return FileInfo list {@link ru.emiljan.servicedevdevices.models.order.FileInfo}
     * @throws IOException
     */
    @Transactional
    public List<FileInfo> upload(List<MultipartFile> files) throws IOException {
        //add checks to compare files, if files is equals -> get fileName -> find JPA fileInfo by filename; else -> add new files
        List<FileInfo> result = new ArrayList<>();
        if(files.stream().anyMatch(file -> file.getSize()==0)){
            throw new FileUploadException("Файл не загружен");
        }
        Map<Integer,List<?>> map = checksToCompare(files);
        if(map != null){
            files = (List<MultipartFile>) map.get(1);
            List<File> fileList = (List<File>) map.get(2);
            if(!fileList.isEmpty()) {
                for (File file : fileList) {
                    result.add(this.fileService.getFileInfoByFilename(file.getName()));
                }
            }
        }
        for(MultipartFile file : files){
            FileInfo f = this.uploadBuilder.uploadFile(file,transferInfo);
            fileService.save(f);
            result.add(f);
        }
        return result;
    }

    public List<FileInfo> getAllFilesByProjectId(Long id){
        return this.projectRepository.getAllFilesByProjectId(id);
    }

    /**
     * update project method
     * @param project {@link ru.emiljan.servicedevdevices.models.portfolio.Project}
     */
    @Transactional
    public void update(Project project){
        this.projectRepository.save(project);
    }

    @Transactional
    public void deleteById(Long id){
        List<FileInfo> fileList = this.projectRepository.getAllFilesByProjectId(id);
        projectRepository.deleteById(id);
        checksToDelete(fileList);
    }

    /**
     * deleting files if they are not in use
     * @param fileList project files
     */
    private void checksToDelete(List<FileInfo> fileList){
        List<FileInfo> removeList = this.fileService.getRemoveFileList(fileList);
        if(!removeList.isEmpty()){
            this.fileService.deleteProjectsFiles(removeList,transferInfo);
        }
    }

    /**
     * draw file by id
     * @param id fileId
     * @return {@link org.springframework.http.ResponseEntity}
     * @throws IOException
     */
    public ResponseEntity<?> drawImage(Long id) throws IOException {
        FileInfo info = this.fileService.getFileInfoById(id);
        String filename = info.getFilename();
        MediaType mediaType = MediaType.valueOf(info.getContentType());
        File file = new File(transferInfo.getPath()+filename);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new InputStreamResource(new ByteArrayInputStream(Files.readAllBytes(file.toPath()))));
    }
}
