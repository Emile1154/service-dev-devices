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
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.services.UserService;
import ru.emiljan.servicedevdevices.services.project.CommentService;
import ru.emiljan.servicedevdevices.services.project.ProjectService;

import javax.validation.Valid;
import java.util.Set;

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
    @Transactional
    public String addComment(@ModelAttribute("comment") @Valid Comment comment, BindingResult bindingResult,
                             @PathVariable("id") Long id, @AuthenticationPrincipal UserDetails user, Model model){
        User commentator = this.userService.findUserByNickname(user.getUsername());
        model.addAttribute("currentUser", commentator);
        model.addAttribute("project", this.projectService.getProjectById(id));
        model.addAttribute("comments", this.projectService.getAllCommentsByProjectId(id, commentator));
        model.addAttribute("files",this.projectService.getAllFilesByProjectId(id));
        model.addAttribute("vip",false);
        if(bindingResult.hasErrors()){
            return "project/show";
        }
        comment.setUser(commentator);
        commentService.save(comment,id);
        return "redirect:/portfolio/"+id;
    }

    @PostMapping("/delete/{id}")
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
        Set<User> likes = this.commentService.getById(id).getLikes();
        if(likes.contains(currentUser)){
            likes.remove(currentUser);
            return "redirect:/portfolio/"+_id;
        }
        likes.add(currentUser);
        return "redirect:/portfolio/"+_id;
    }
}
