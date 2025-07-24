package com.In_need.inNeedApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your email");
        message.setText(
                "Hi,\n\n" +
                        "Thank you for registering with us!\n\n" +
                        "Please click the link below to verify your email address:\n" +
                        link + "\n\n" +
                        "If you didn't request this, please ignore this email.\n\n" +
                        "Best regards,\nIn-Need App"
        );
        mailSender.send(message);
    }
}
