package com.findit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException e, Model model) {
        log.error("Resource not found: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        model.addAttribute("status", 404);
        return "error";
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicate(DuplicateResourceException e, RedirectAttributes redirectAttributes) {
        log.error("Duplicate resource: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/register";
    }
    
    @ExceptionHandler(UnauthorizedAccessException.class)
    public String handleUnauthorized(UnauthorizedAccessException e, RedirectAttributes redirectAttributes) {
        log.error("Unauthorized access: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/dashboard";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        log.error("Unexpected error: ", e);
        model.addAttribute("error", "An unexpected error occurred. Please try again later.");
        model.addAttribute("status", 500);
        return "error";
    }
}