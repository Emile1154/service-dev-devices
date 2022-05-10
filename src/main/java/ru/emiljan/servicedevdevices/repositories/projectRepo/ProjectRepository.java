package ru.emiljan.servicedevdevices.repositories.projectRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.dto.ProjectDTO;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.portfolio.Project;

import java.util.List;


@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {
    @Query(value = "SELECT coalesce(max(id),0) FROM projects", nativeQuery = true)
    Long getMaxId();

    @Query(value = "SELECT NEW ru.emiljan.servicedevdevices.models.dto.ProjectDTO" +
            "( p.id, " +
            " p.title, " +
            " p.description ) " +
            "FROM Project p  " +
            "WHERE p.id = :id")
    ProjectDTO getDTOById(@Param("id") Long id);

    @Query(value = "SELECT pf FROM Project p LEFT JOIN p.fileList pf WHERE p.id =:id")
    List<FileInfo> getAllFilesByProjectId(@Param("id") Long id);

}
