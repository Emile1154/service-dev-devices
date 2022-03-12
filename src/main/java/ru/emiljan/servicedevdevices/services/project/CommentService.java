package ru.emiljan.servicedevdevices.services.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.models.portfolio.Project;
import ru.emiljan.servicedevdevices.repositories.projectRepo.CommentRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProjectService projectService ;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          ProjectService projectService) {
        this.commentRepository = commentRepository;
        this.projectService = projectService;
    }

    @Transactional
    public void save(Comment comment, Long projectId){
        comment.setProject(this.projectService.getProjectById(projectId));
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
}
