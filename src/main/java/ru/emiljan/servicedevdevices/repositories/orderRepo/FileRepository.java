package ru.emiljan.servicedevdevices.repositories.orderRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.order.FileInfo;

/**
 *
 * @author EM1LJAN
 */
@Repository
public interface FileRepository extends JpaRepository<FileInfo, Long> {
    @Query(value = "SELECT coalesce(max(id),0) FROM files_info", nativeQuery = true)
    Long getMaxId();

    @Query(value = "SELECT " +
                   "CASE WHEN SIZE(fp) = 0 THEN TRUE ELSE FALSE END " +
                   "FROM FileInfo f LEFT JOIN f.projects fp " +
                   "WHERE f = :file")
    boolean confirmDelete(@Param("file") FileInfo file);

    FileInfo findFileInfoByFilename(String filename);
    FileInfo findFileInfoById(Long id);
}
