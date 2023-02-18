package com.bartek.supportportal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.bartek.supportportal.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

@Service
@Slf4j
public class EmailService {

    @Value("${email.username}")
    private String emailSMTP;
    @Value("${email.password}")
    private String passwordSMTP;

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, SMTP_MAIL_OUTLOOK_COM);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }

    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName + "\n \n Your new account password is: " + password + " \n \n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    public void sendNewPasswordEmail(String firstName, String password, String email) {
        try {
            log.info("email: " + email + "password: " + password);
            Message message = createEmail(firstName, password, email);
            Transport transport = getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
            transport.connect(SMTP_MAIL_OUTLOOK_COM, emailSMTP, passwordSMTP);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}