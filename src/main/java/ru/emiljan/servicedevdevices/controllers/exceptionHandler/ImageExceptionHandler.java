package ru.emiljan.servicedevdevices.controllers.exceptionHandler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.emiljan.servicedevdevices.controllers.ImageController;
import ru.emiljan.servicedevdevices.controllers.userControllers.UserController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@ControllerAdvice(assignableTypes = {ImageController.class})
public class ImageExceptionHandler {
    @ExceptionHandler(IOException.class)
    public ModelAndView loadIconException(IOException ex, HttpServletRequest request){
        Map<String, Object> map = (Map<String, Object>) request.getAttribute("map");
        ModelAndView model = new ModelAndView("users/show");
        model.addAllObjects(map);
        model.addObject("error",ex.getMessage());
        return model;
    }

}
