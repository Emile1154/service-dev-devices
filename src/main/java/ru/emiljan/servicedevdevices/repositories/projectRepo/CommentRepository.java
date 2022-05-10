package ru.emiljan.servicedevdevices.repositories.projectRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.dto.CommentDTO;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;

import java.util.List;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT NEW ru.emiljan.servicedevdevices.models.dto.CommentDTO " +
                   "(c, " +
                   " COUNT(cl), " +
                   " SUM(CASE WHEN cl = :user THEN 1 ELSE 0 END) > 0 ) " +
                   "FROM Comment c LEFT JOIN c.likes cl " +
                   "WHERE c.project.id = :id " +
                   "GROUP BY c")
    List<CommentDTO> findAllByProjectId(@Param("id") Long id, @Param("user") User user);

    @Query(value = "SELECT NEW ru.emiljan.servicedevdevices.models.dto.CommentDTO " +
                   "(c, " +
                   " COUNT(cl), " +
                   " SUM(CASE WHEN cl = :user THEN 1 ELSE 0 END) > 0 ) " +
                   "FROM Comment c LEFT JOIN c.likes cl " +
                   " GROUP BY c")
    List<CommentDTO> findAll(@Param("user") User user);


    @Query(value = "SELECT cl FROM Comment c LEFT JOIN c.likes cl WHERE c = :comment")
    Set<User> getAllLikes(@Param("comment") Comment comment);

}
