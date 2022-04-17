package ru.emiljan.servicedevdevices.services.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.models.portfolio.Project;
import ru.emiljan.servicedevdevices.repositories.projectRepo.CommentRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void save(Comment comment){
        commentRepository.save(comment);
    }

    @Transactional
    public void removeById(Long id){
        commentRepository.deleteById(id);
    }


    public Project getProjectByCommentId(Long id){
        Comment comment = this.commentRepository.findById(id).orElse(null);
        if(comment == null){
            return null;
        }
        return comment.getProject();
    }

    public Comment getById(Long id){
        return this.commentRepository.findById(id).orElse(null);
    }

    public Set<User> getAllCommentLikes(Comment comment){
        Set<User> likes = this.commentRepository.getAllLikes(comment);
        if(likes.iterator().next() == null){
            likes = new HashSet<>();
        }
        return likes;
    }
}
