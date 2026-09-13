package com.blooddonation.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // Reads MAIL_USERNAME from application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendApprovalEmail(String toEmail, String name, String type) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            log.warn("Skipping email: No recipient email provided for {}", name);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Blood Donation System - Registration Approved");
            message.setText(
                    "Dear " + name + ",\n\n" +
                    "Your " + type + " has been approved by the administrator.\n\n" +
                    "You can now use the Blood Donation Management System.\n\n" +
                    "Thank you for being part of our blood donation community.\n\n" +
                    "Regards,\n" +
                    "Blood Donation Management System"
            );

            mailSender.send(message);
            log.info("Approval email sent successfully to {} ({})", name, toEmail);
        } catch (Exception e) {
            log.error("Failed to send approval email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendRejectionEmail(String toEmail, String name, String type) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            log.warn("Skipping email: No recipient email provided for {}", name);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Blood Donation System - Registration Rejected");
            message.setText(
                    "Dear " + name + ",\n\n" +
                    "We are sorry to inform you that your " + type +
                    " has been rejected by the administrator.\n\n" +
                    "Please contact the administrator if you need more information.\n\n" +
                    "Regards,\n" +
                    "Blood Donation Management System"
            );

            mailSender.send(message);
            log.info("Rejection email sent successfully to {} ({})", name, toEmail);
        } catch (Exception e) {
            log.error("Failed to send rejection email to {}: {}", toEmail, e.getMessage());
        }
    }
}