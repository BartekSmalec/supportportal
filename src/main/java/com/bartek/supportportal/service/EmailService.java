package com.bartek.supportportal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

import static com.bartek.supportportal.constant.EmailConstant.*;

@Service
@Slf4j
public class EmailService {

    @Value("${email.username}")
    private String emailSMTP;
    @Value("${email.password}")
    private String passwordSMTP;

    private JavaMailSender emailSender;


    public SimpleMailMessage sendSimpleMessage(String firstName, String password, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName + "\n \n Your new account password is: " + password + " \n \n The Support Team");
        return message;
    }

    public void sendNewPasswordEmail(String firstName, String password, String email) {
        log.info("email: " + email + "password: " + password);
        emailSender = getJavaMailSender();
        SimpleMailMessage message = sendSimpleMessage(firstName, password, email);
        emailSender.send(message);
    }

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(SMTP_MAIL_OUTLOOK_COM);
        mailSender.setPort(DEFAULT_PORT);

        mailSender.setUsername(emailSMTP);
        mailSender.setPassword(passwordSMTP);

        Properties props = mailSender.getJavaMailProperties();
        props.put(MAIL_TRANSPORT_PROTOCOL, SIMPLE_MAIL_TRANSFER_PROTOCOL);
        props.put(SMTP_AUTH, true);
        props.put(SMTP_STARTTLS_ENABLE, true);
        props.put(SMTP_STARTTLS_REQUIRED, true);

        return mailSender;
    }
}
