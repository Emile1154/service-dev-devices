package ru.emiljan.servicedevdevices.services.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.repositories.orderRepo.FileRepository;

import java.io.File;
import java.util.List;

@Service
public class FileService {
    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileInfo getFileInfoById(Long id){
        return fileRepository.getById(id);
    }
    @Transactional
    public List<FileInfo> getAllFilesBProjectId(Long id){
        return this.fileRepository.findAllByProjectId(id);
    }

    @Transactional
    public void deleteAllByProjectId(Long id, TransferInfo transfer){
        deleteProjectsFiles(this.fileRepository.findAllByProjectId(id),transfer);
        fileRepository.deleteAllByProjectId(id);
    }

    private void deleteProjectsFiles(List<FileInfo> removeList, TransferInfo ti){
        removeList.forEach(fileInfo ->
                new File(ti.getPath() + fileInfo.getFilename()).delete());
    }

    @Transactional
    public void save(FileInfo fileInfo){
        fileRepository.save(fileInfo);
    }
}
