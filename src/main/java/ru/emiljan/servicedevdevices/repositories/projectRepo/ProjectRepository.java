package ru.emiljan.servicedevdevices.repositories.projectRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.portfolio.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {
    @Query(value = "SELECT coalesce(max(id),0) FROM projects", nativeQuery = true)
    Long getMaxId();


}
