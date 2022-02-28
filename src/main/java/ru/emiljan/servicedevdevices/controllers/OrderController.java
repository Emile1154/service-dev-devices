package ru.emiljan.servicedevdevices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.models.order.CustomOrder;
import ru.emiljan.servicedevdevices.models.order.DesignType;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.services.OrderService;
import ru.emiljan.servicedevdevices.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author EM1LJAN
 */
@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final TransferInfo transferInfo;

    @Autowired
    public OrderController(OrderService orderService,
                           UserService userService,
                           TransferInfo transferInfo) {
        this.orderService = orderService;
        this.userService = userService;
        this.transferInfo = transferInfo;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model,
                       @AuthenticationPrincipal UserDetails currentUser){
        User user = userService.findUserByNickname(currentUser.getUsername());
        CustomOrder order = orderService.findById(id);
        User customer = order.getUser();

        if(user.getId() == customer.getId() || user.getRoles().stream().anyMatch(role ->
                role.getId()>=2)){
            model.addAttribute("customer", customer);
            model.addAttribute("order", order);
            model.addAttribute("user", user);
            return "orders/show_order";
        }
        return "error403";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadResource(@PathVariable("id") Long orderId,
                                                                HttpServletRequest request,
                                                                @AuthenticationPrincipal UserDetails currentUser) throws FileNotFoundException {
        final CustomOrder order = this.orderService.findById(orderId);
        final String filename = order.getFileInfo().getFilename();
        final MediaType mediaType = MediaType.valueOf(order.getFileInfo().getContentType());
        File file = new File(transferInfo.getPath() + filename);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("customer",order.getUser());
        attributes.put("order",order);
        attributes.put("user",userService.findUserByNickname(currentUser.getUsername()));
        request.setAttribute("map", attributes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+file.getName())
                .contentType(mediaType)
                .body(new InputStreamResource(new FileInputStream(file)));
    }

    @GetMapping("/new")
    public String newOrder(@ModelAttribute("order") CustomOrder order,
                           Model model, Principal user){
        model.addAttribute("user",
                userService.findUserByNickname(user.getName()));
        return "orders/create_order";
    }

    @PostMapping
    public String createOrder(Model model, Principal user,
                              @ModelAttribute("order") @Valid CustomOrder order,
                              BindingResult bindingResult,HttpServletRequest request,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam("design") String design) throws IOException {
        User customer = userService.findUserByNickname(user.getName());
        model.addAttribute("user", customer);
        model.addAttribute("upload", file);
        model.addAttribute("select", design);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user",customer);
        attributes.put("order",order);
        attributes.put("upload",file);
        attributes.put("select",design);
        request.setAttribute("map", attributes);
        if(orderService.checkTitle(order)){
                bindingResult.rejectValue("title", "error.order",
                        "Данное наименнование проекта уже использовалось");
        }
        if(bindingResult.hasErrors()){
            return "orders/create_order";
        }
        order.setFileInfo(this.orderService.uploadFile(file));
        order.setDesignType(DesignType.valueOf(design));
        order.setUser(customer);
        orderService.saveOrder(order);
        return "success";
    }

    @GetMapping("/payments")
    public String customerIndex(@AuthenticationPrincipal UserDetails user, Model model){
        User currentUser = this.userService.findUserByNickname(user.getUsername());
        model.addAttribute("currentUser",currentUser);
        model.addAttribute("history",currentUser.getPayments());
        return "payment/payment_list";
    }
}
