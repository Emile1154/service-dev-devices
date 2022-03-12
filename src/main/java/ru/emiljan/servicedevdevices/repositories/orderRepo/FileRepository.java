package ru.emiljan.servicedevdevices.repositories.orderRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.emiljan.servicedevdevices.models.order.FileInfo;

import java.util.List;

public interface FileRepository extends JpaRepository<FileInfo, Long> {
    @Query(value = "SELECT coalesce(max(id),0) FROM files_info", nativeQuery = true)
    Long getMaxId();
    List<FileInfo> findAllByProjectId(Long id);
    void deleteAllByProjectId(Long id);
}
