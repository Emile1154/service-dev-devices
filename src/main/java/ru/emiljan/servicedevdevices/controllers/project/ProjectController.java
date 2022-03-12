package ru.emiljan.servicedevdevices.controllers.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.dto.CommentDTO;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.models.portfolio.Project;
import ru.emiljan.servicedevdevices.services.UserService;
import ru.emiljan.servicedevdevices.services.project.ProjectService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/portfolio")
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;


    @Autowired
    public ProjectController(ProjectService projectService,
                             UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/new")
    public String addProject(@ModelAttribute("project") Project project,Model model,
                             @AuthenticationPrincipal UserDetails user){
        model.addAttribute("currentUser", userService.findUserByNickname(user.getUsername()));
        return "project/create_project";
    }

    @PostMapping("/upload")
    public String uploadProject(@ModelAttribute("project") @Valid Project project,BindingResult bindingResult,
                                Model model, @AuthenticationPrincipal UserDetails user,
                                @RequestParam("files") MultipartFile[] files) throws IOException {
        model.addAttribute("currentUser", userService.findUserByNickname(user.getUsername()));
        if(bindingResult.hasErrors()){
            return "project/create_project";
        }
        List<FileInfo> infoList = this.projectService.upload(Arrays.asList(files));
        project.setFileList(infoList);
        project.setPreviewId(infoList.get(0).getId());
        projectService.save(project);
        infoList.forEach(fileInfo -> fileInfo.setProject(project));
        projectService.saveAllFileInfo(infoList);
        return "redirect:/portfolio";
    }

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal UserDetails user){
        if(user!=null){
            model.addAttribute("currentUser", userService.findUserByNickname(user.getUsername()));
        }
        model.addAttribute("projects", projectService.getAllProjects());
        return "project/index_projects";
    }

    @GetMapping("/{id}")
    @Transactional
    public String show(@ModelAttribute("comment") Comment comment,
                       @PathVariable("id") Long id, Model model,
                       @AuthenticationPrincipal UserDetails user){
        List<CommentDTO> comments;
        User currentUser;
        if(user!=null){
            currentUser = this.userService.findUserByNickname(user.getUsername());
            model.addAttribute("currentUser", currentUser);
            comments = this.projectService.getAllCommentsByProjectId(id, currentUser);
            model.addAttribute("vip",currentUser.getRoles().stream().anyMatch(role->role.getId()>=2));
        }else{
            comments = this.projectService.getAllCommentsByProjectId(id, null);
            model.addAttribute("vip",false);
        }
        Project project = this.projectService.getProjectById(id);
        List<FileInfo> filesInfo = this.projectService.getAllFilesByProjectId(id);

        model.addAttribute("project",project);
        model.addAttribute("files", filesInfo);
        model.addAttribute("comments", comments);
        return "project/show";
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<?> viewImage(@PathVariable("id") Long id) throws IOException {
        return this.projectService.drawImage(id);
    }

    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id){
        projectService.deleteById(id);
        return "redirect:/portfolio";
    }
}
