package com.findit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// Email service is optional - requires mail dependency
// To enable email notifications, uncomment the following and add spring-boot-starter-mail dependency
/*
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
*/

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    // Uncomment for email functionality
    /*
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Async
    public void sendClaimStatusNotification(String toEmail, String claimantName, String itemTitle, String status, String reason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("FindIt - Claim Status Update for " + itemTitle);
            
            String content = String.format(
                "Dear %s,\n\n" +
                "Your claim for item '%s' has been %s.\n\n" +
                (reason != null ? "Reason: " + reason + "\n\n" : "") +
                "Thank you for using FindIt - Campus Lost & Found System.\n\n" +
                "Best regards,\n" +
                "FindIt Team",
                claimantName, itemTitle, status.toLowerCase()
            );
            
            message.setText(content);
            mailSender.send(message);
            logger.info("Claim status email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
    */
    
    // Placeholder method for email notifications
    public void sendNotification(String toEmail, String subject, String content) {
        logger.info("Email notification would be sent to: {} - Subject: {}", toEmail, subject);
        // Implement actual email sending when mail dependency is added
    }
}