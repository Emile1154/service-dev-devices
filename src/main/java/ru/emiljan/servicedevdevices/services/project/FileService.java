package ru.emiljan.servicedevdevices.services.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.repositories.orderRepo.FileRepository;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileInfo getFileInfoById(Long id){
        return this.fileRepository.findFileInfoById(id);
    }

    public FileInfo getFileInfoByFilename(String filename){
        return this.fileRepository.findFileInfoByFilename(filename);
    }

    public List<FileInfo> getRemoveFileList(List<FileInfo> files){
        return files
                .stream()
                .filter(this.fileRepository::confirmDelete)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProjectsFiles(List<FileInfo> removeList, TransferInfo ti){
        removeList.forEach(fileInfo ->
                new File(ti.getPath() + fileInfo.getFilename()).delete());
        this.fileRepository.deleteAll(removeList);

    }

    @Transactional
    public void save(FileInfo fileInfo){
        fileRepository.save(fileInfo);
    }
}
