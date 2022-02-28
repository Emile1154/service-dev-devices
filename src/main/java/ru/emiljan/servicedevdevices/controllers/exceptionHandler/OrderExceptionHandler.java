package ru.emiljan.servicedevdevices.controllers.exceptionHandler;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.emiljan.servicedevdevices.controllers.OrderController;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author EM1LJAN
 */
@ControllerAdvice(assignableTypes = {OrderController.class})
public class OrderExceptionHandler {

    @ExceptionHandler(IOException.class)
    public String createOrderException(IOException ex, RedirectAttributes attributes,
                                    HttpServletRequest request){
        Map<String, Object> params = (Map<String, Object>) request.getAttribute("map");
        if(params != null){
            for(Map.Entry<String,Object> pair : params.entrySet()){
                attributes.addFlashAttribute(pair.getKey(),pair.getValue());
            }
        }
        if(ex instanceof FileSizeLimitExceededException){
            attributes.addFlashAttribute("error",
                    "максимальный размер файла превышен");
            return "redirect:/orders/new";
        }
        attributes.addFlashAttribute("error",ex.getMessage());
        return "redirect:/orders/new";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ModelAndView downloadOrderException(HttpServletRequest request){
        ModelAndView model2 = new ModelAndView("orders/show_order");
        Map<String, Object> attr = (Map<String, Object>) request.getAttribute("map");
        model2.addAllObjects(attr);
        model2.addObject("error", "Файл на найден");
        return model2;
    }
}
