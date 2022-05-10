package ru.emiljan.servicedevdevices.controllers.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.dto.CommentDTO;
import ru.emiljan.servicedevdevices.models.dto.ProjectDTO;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.portfolio.Comment;
import ru.emiljan.servicedevdevices.models.portfolio.Project;
import ru.emiljan.servicedevdevices.services.UserService;
import ru.emiljan.servicedevdevices.services.project.ProjectService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for {@link ru.emiljan.servicedevdevices.models.portfolio.Project}
 *
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

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String addProject(@ModelAttribute("project") Project project,Model model,
                             @AuthenticationPrincipal UserDetails user){
        if(user==null){
            return "redirect:/portfolio";
        }
        final User currentUser = this.userService.findUserByNickname(user.getUsername());
        model.addAttribute("alarm",this.userService.checkNewNotifies(currentUser));
        model.addAttribute("currentUser",currentUser);
        return "project/create_project";
    }

    @PostMapping("/upload")
    public String uploadProject(@ModelAttribute("project") @Valid Project project, BindingResult bindingResult,
                                Model model, @AuthenticationPrincipal UserDetails user,
                                @RequestParam("files") MultipartFile[] files, HttpServletRequest request) throws IOException {
        Map<String, Object> attr =
                getAttributes(user.getUsername(), "/portfolio/new/", files, project);
        model.addAttribute("currentUser", attr.get("currentUser"));
        model.addAttribute("alarm", attr.get("alarm"));
        model.addAttribute("files", files);
        request.setAttribute("map", attr);

        if(bindingResult.hasErrors()){
            return "project/create_project";
        }
        List<FileInfo> infoList = this.projectService.upload(Arrays.asList(files));
        project.setFileList(infoList);
        project.setPreviewId(infoList.get(0).getId());
        projectService.save(project);
        return "redirect:/portfolio";
    }

    @Secured({"ROLE_ADMIN", "ROLE_DEVELOP", "ROLE_MANAGER"})
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails user){
        final ProjectDTO project = this.projectService.getDTOById(id);
        model.addAttribute("project",project);
        model.addAttribute("currentUser", this.userService.findUserByNickname(user.getUsername()));
        return "project/edit";
    }

    @PatchMapping("/update/{id}")
    public String update(@ModelAttribute("project") @Valid Project project, BindingResult bindingResult,
                         Model model, @AuthenticationPrincipal UserDetails user, @PathVariable("id") Long id,
                         @RequestParam("files") MultipartFile[] files, HttpServletRequest request) throws IOException{
        Map<String, Object> attr =
                getAttributes(user.getUsername(),"/portfolio/edit/"+project.getId(), files, project);
        attr.put("title", project.getTitle());
        attr.put("description",project.getDescription());
        model.addAttribute("currentUser", attr.get("currentUser"));
        model.addAttribute("alarm", attr.get("alarm"));
        model.addAttribute("files", files);
        request.setAttribute("map", attr);

        if(bindingResult.hasErrors()){
            return "project/edit";
        }
        List<FileInfo> infoList = this.projectService.upload(Arrays.asList(files));
        project.setFileList(infoList);
        project.setPreviewId(infoList.get(0).getId());
        this.projectService.update(project);
        return "redirect:/portfolio";
    }

    private Map<String,Object> getAttributes(String username, String returnLink, MultipartFile[] files, Project project){
        Map<String, Object> attr = new HashMap<>();
        final User user = this.userService.findUserByNickname(username);
        attr.put("currentUser", user);
        attr.put("alarm", this.userService.checkNewNotifies(user));
        attr.put("files", files);
        attr.put("link", returnLink);
        attr.put("title", project.getTitle());
        attr.put("description",project.getDescription());
        return attr;
    }

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal UserDetails user){
        if(user!=null){
            final User currentUser = this.userService.findUserByNickname(user.getUsername());
            model.addAttribute("alarm",this.userService.checkNewNotifies(currentUser));
            model.addAttribute("currentUser",currentUser);
            model.addAttribute("vip",currentUser.getRoles().stream().anyMatch(role->role.getId()>=2));
        }
        model.addAttribute("projects", projectService.getAllProjects());
        return "project/index_projects";
    }

    @GetMapping("/{id}")
    public String show(@ModelAttribute("comment") Comment comment,
                       @PathVariable("id") Long id, Model model,
                       @AuthenticationPrincipal UserDetails user){
        List<CommentDTO> comments;
        User currentUser;
        if(user!=null){
            currentUser = this.userService.findUserByNickname(user.getUsername());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("alarm",this.userService.checkNewNotifies(currentUser));
            comments = this.projectService.getAllCommentsByProjectId(id, currentUser);
            model.addAttribute("vip",currentUser.getRoles().stream().anyMatch(role->role.getId()>=2));
        }else{
            comments = this.projectService.getAllCommentsByProjectId(id, null);
            model.addAttribute("vip",false);
        }
        final ProjectDTO project = this.projectService.getDTOById(id);
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

    @Secured({"ROLE_ADMIN", "ROLE_DEVELOPER", "ROLE_MANAGER"})
    @DeleteMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id){
        projectService.deleteById(id);
        return "redirect:/portfolio";
    }
}
