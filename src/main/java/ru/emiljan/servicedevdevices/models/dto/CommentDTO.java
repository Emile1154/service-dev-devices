package ru.emiljan.servicedevdevices.models.dto;

import lombok.Getter;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.models.portfolio.Project;

import java.time.LocalDateTime;

@Getter
public class CommentDTO {
    private Long id;
    private String message;
    private String mark;
    private User user;
    private LocalDateTime created;
    private Project project;
    private Long likes;
    private boolean meLiked;

    public CommentDTO(Comment comment, Long likes, boolean meLiked) {
        this.id = comment.getId();
        this.message = comment.getMessage();
        this.mark = comment.getMark();
        this.user = comment.getUser();
        this.created = comment.getCreated();
        this.project = comment.getProject();
        this.likes = likes;
        this.meLiked = meLiked;
    }
}
