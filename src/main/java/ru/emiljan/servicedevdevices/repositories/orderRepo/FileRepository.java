package ru.emiljan.servicedevdevices.repositories.orderRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.dto.ProjectDTO;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.portfolio.Project;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileInfo, Long> {
    @Query(value = "SELECT coalesce(max(id),0) FROM files_info", nativeQuery = true)
    Long getMaxId();

    @Query(value = "SELECT " +
                   "SUM(CASE WHEN fp.size = 1 THEN 1 ELSE 0 END ) > 0 " +
                   "FROM FileInfo f " +
                   "LEFT JOIN f.projects fp " +
                   "WHERE f.id = :id")
    boolean confirmDelete(@Param("id") Long file_id);

    FileInfo findFileInfoByFilename(String filename);
}
