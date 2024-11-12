package com.example.ManagementCompanyBot.service;

import com.example.ManagementCompanyBot.config.properties.EmailProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

@Data
@Slf4j
@Service
public class EmailService {

    private EmailProperties emailProperties;

    public void sendEmail(String to, String subject, String body) {
        log.info("Processing send e-mail: {}", to);

        Properties properties = createEmailProp();
        Session session = createSession(properties);

        try {
            Message message = createMessage(session, to, subject, body);
            sendMessage(message);
        } catch (MessagingException ex) {
            log.error(ex.getMessage());
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String body,
                                        ByteArrayOutputStream attachment, String fileName) {
        log.info("Processing sending e-mail with attachment to: {}", to);

        Properties properties = createEmailProp();
        Session session = createSession(properties);

        try {
            Message message = createMessageWithAttachment(session, to, subject, body, attachment, fileName);
            sendMessage(message);
        } catch (MessagingException ex) {
            log.error(ex.getMessage());
        }
    }

    public Properties createEmailProp() {
        log.info("Create properties");
        Properties properties = new Properties();//проверить!!
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.yandex.com"); // SMTP сервер
        properties.put("mail.smtp.port", "465");
        return properties;
    }

    public Session createSession(Properties properties) {
        log.info("Create session");
        return Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailProperties.getUsername(), emailProperties.getPassword());
                    }
                });
    }

    public Message createMessage(Session session, String to, String subject, String body) throws MessagingException {
        log.info("Create message");
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailProperties.getUsername()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

    private Message createMessageWithAttachment(Session session, String to,
                                                String subject, String body, ByteArrayOutputStream attachment,
                                                String fileName) throws MessagingException {

        log.info("Create message with attachments");
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailProperties.getUsername()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart messageBodyPart = createMimeBodyPart(body);
        MimeBodyPart attachmentBodyPart = createMimeBodyPart(attachment, fileName);
        createMultipart(messageBodyPart, attachmentBodyPart, message);
        return message;
    }

    public void createMultipart(MimeBodyPart messageBodyPart, MimeBodyPart attachmentBodyPart, Message message)
            throws MessagingException {
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentBodyPart);
        message.setContent(multipart);
    }

    public MimeBodyPart createMimeBodyPart(ByteArrayOutputStream attachment, String fileName)
            throws MessagingException {
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(attachment.toByteArray(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
        attachmentBodyPart.setFileName(fileName);
        return attachmentBodyPart;
    }

    public MimeBodyPart createMimeBodyPart(String body) throws MessagingException {
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);
        return messageBodyPart;
    }

    public void sendMessage(Message message) throws MessagingException {
        Transport.send(message);
        log.info("E-mail successfully sent to: {}", message.getAllRecipients()[0]);
    }
}
