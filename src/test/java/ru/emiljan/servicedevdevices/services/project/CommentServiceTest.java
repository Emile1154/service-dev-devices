package ru.emiljan.servicedevdevices.services.project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.models.portfolio.Project;
import ru.emiljan.servicedevdevices.repositories.projectRepo.CommentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    private Comment comment;
    private Project project;

    @BeforeEach
    public void setUp(){
        project = Project.builder()
                    .id(99L)
                .build();
        comment = Comment.builder()
                    .id(99L)
                    .message("some message")
                    .user(User.builder()
                                .id(99L)
                            .build())
                    .project(project)
                .build();
    }

    @Test
    void getProjectByCommentId() {
        Long id = 99L;
        Mockito.when(commentRepository.findById(id)).thenReturn(java.util.Optional.ofNullable(comment));
        Project result = commentService.getProjectByCommentId(id);

        Assertions.assertEquals(project, result);
    }

    @Test
    void getAllCommentLikes() {
        final User user = comment.getUser();
        Mockito.when(commentRepository.getAllLikes(comment)).thenReturn(new HashSet<>(List.of(user)));
        Set<User> likes = commentService.getAllCommentLikes(comment);

        Assertions.assertTrue(likes.contains(user));
    }
}