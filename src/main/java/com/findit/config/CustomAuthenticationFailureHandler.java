package com.findit.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String email = request.getParameter("email");
        
        // Log failed attempt (implement logging service here)
        System.err.println("Login failed for email: " + email + " - Reason: " + exception.getMessage());
        
        // Add delay to prevent brute force (500ms)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Track failed attempts (implement in database for account lockout)
        
        response.sendRedirect("/login?error=true");
    }
}