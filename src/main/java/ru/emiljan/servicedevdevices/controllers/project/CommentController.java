package ru.emiljan.servicedevdevices.controllers.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.dto.ProjectDTO;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.services.UserService;
import ru.emiljan.servicedevdevices.services.project.CommentService;
import ru.emiljan.servicedevdevices.services.project.ProjectService;

import javax.validation.Valid;
import java.util.Set;

/**
 * Controller class for {@link ru.emiljan.servicedevdevices.models.portfolio.Comment}
 *
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final ProjectService projectService;

    @Autowired
    public CommentController(CommentService commentService,
                             UserService userService,
                             ProjectService projectService) {
        this.commentService = commentService;
        this.userService = userService;
        this.projectService = projectService;
    }

    @PostMapping("/create/project-id/{id}")
    public String addComment(@ModelAttribute("comment") @Valid Comment comment, BindingResult bindingResult,
                             @PathVariable("id") Long id, @AuthenticationPrincipal UserDetails user, Model model){
        final User commentator = this.userService.findUserByNickname(user.getUsername());
        final ProjectDTO project = this.projectService.getDTOById(id);
        model.addAttribute("currentUser", commentator);
        model.addAttribute("project", project);
        model.addAttribute("comments", this.projectService.getAllCommentsByProjectId(id, commentator));
        model.addAttribute("files",this.projectService.getAllFilesByProjectId(id));
        model.addAttribute("vip",false);
        if(bindingResult.hasErrors()){
            return "project/show";
        }
        comment.setUser(commentator);
        comment.setProject(this.projectService.getProjectById(id));
        commentService.save(comment);
        return "redirect:/portfolio/"+id;
    }


    @DeleteMapping("/delete/{id}")
    public String removeComment(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails user){  //admin and creator comment
        User currentUser = this.userService.findUserByNickname(user.getUsername());
        Comment comment = this.commentService.getById(id);
        User commentator = comment.getUser();
        Long _id = comment.getProject().getId();
        if(currentUser.getId() == commentator.getId() || currentUser.getRoles().stream().anyMatch(role ->
                role.getId()>=2)){
            commentService.removeById(id);
        }
        return "redirect:/portfolio/"+_id;
    }

    @GetMapping("/like/{id}")
    @Transactional
    public String likeComment(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails user){
        if(user == null){
           return  "redirect:/users/login";
        }
        User currentUser = this.userService.findUserByNickname(user.getUsername());
        Long _id = this.commentService.getProjectByCommentId(id).getId();
        Comment comment = this.commentService.getById(id);
        Set<User> likes = this.commentService.getAllCommentLikes(comment);

        if(likes.stream().anyMatch(user1 -> user1.getId() == currentUser.getId())){
            likes.remove(currentUser);
        }
        else{
            likes.add(currentUser);
        }
        comment.setLikes(likes);
        this.commentService.save(comment);
        return "redirect:/portfolio/"+_id;
    }
}
