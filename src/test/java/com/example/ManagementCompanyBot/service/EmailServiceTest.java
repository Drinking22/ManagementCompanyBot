package com.example.ManagementCompanyBot.service;

import com.example.ManagementCompanyBot.config.properties.EmailProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private EmailProperties emailProperties;

    @Mock
    private Session session;

    @Mock
    private Message message;

    @Mock
    private Transport transport;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() throws MessagingException {
        Mockito.when(emailProperties.getUsername()).thenReturn("test@example.com");
        Mockito.when(emailProperties.getPassword()).thenReturn("passwordExample");

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.yandex.com");
        properties.put("mail.smtp.port", "465");

        Mockito.when(Session.getInstance(Mockito.eq(properties), Mockito.any())).thenReturn(session);

        Mockito.when(new MimeMessage(session)).thenReturn(mimeMessage);

        Mockito.doNothing().when(mimeMessage).setFrom(new InternetAddress("test@example.com"));
        Mockito.doNothing().when(mimeMessage).setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("recipient@example.com"));
        Mockito.doNothing().when(mimeMessage).setSubject(Mockito.anyString());
        Mockito.doNothing().when(mimeMessage).setText(Mockito.anyString());

        Mockito.when(session.getTransport()).thenReturn(transport);
    }
}
