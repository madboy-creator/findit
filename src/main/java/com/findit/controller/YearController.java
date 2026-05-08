package com.findit.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.time.Year;

@ControllerAdvice
public class YearController {
    
    @ModelAttribute("currentYear")
    public int getCurrentYear() {
        return Year.now().getValue();
    }
}