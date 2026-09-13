package com.blooddonation.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApprovalEmail(String toEmail, String name, String type) {

        SimpleMailMessage message = new SimpleMailMessage();

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
    }

    public void sendRejectionEmail(String toEmail, String name, String type) {

        SimpleMailMessage message = new SimpleMailMessage();

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
    }
}