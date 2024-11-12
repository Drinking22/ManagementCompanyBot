package com.example.ManagementCompanyBot.service.bot;

import com.example.ManagementCompanyBot.config.properties.EmailProperties;
import com.example.ManagementCompanyBot.model.NewsEntity;
import com.example.ManagementCompanyBot.model.ReadingsEntity;
import com.example.ManagementCompanyBot.repository.NewsRepository;
import com.example.ManagementCompanyBot.repository.ReadingsRepository;
import com.example.ManagementCompanyBot.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class BotServiceImpl implements BotService {

    private final NewsRepository newsRepository;
    private final ReadingsRepository readingsRepository;
    private final EmailService emailService;
    private final EmailProperties emailProperties;

    public void processingNews(Long chatId, String newsText) {
        log.info("Processing of news from the user");
        NewsEntity news = createNews(chatId, newsText);
        newsRepository.save(news);
        sendEmail(newsText);
    }

    public void processingReadings(Long chatId, String readingsText) {
        log.info("Processing of readings from the user");
        ReadingsEntity readings = createReadings(chatId, readingsText);
        readingsRepository.save(readings);
    }

    @Override
    public void sendEmail(String newsText) {
        String subject = "Новость от Telegram бота.";
        String body = "Получена новость: " + newsText;

        try {
            emailService.sendEmail(emailProperties.getUsername(), subject, body);
            log.info("Email sent successfully to {}", emailProperties.getUsername());
        } catch (Exception ex) {
            log.error("Failed to send email: {}", ex.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachment(ByteArrayOutputStream attachment, String fileName) {
        String subject = "Медиафайл от Telegram бота";
        String body = "Получен медиафайл.";

        try {
            emailService.sendEmailWithAttachment(emailProperties.getUsername(), subject, body, attachment, fileName);
            log.info("Email with attachment sent successfully");
        } catch (Exception ex) {
            log.error("Error sending email with attachment: {}", ex.getMessage());
        }
    }

    public NewsEntity createNews(Long chatId, String newsText) {
        NewsEntity news = new NewsEntity();
        news.setChatId(chatId);
        news.setNewsText(newsText);
        news.setDateMessageSending(LocalDateTime.now());
        return news;
    }

    public ReadingsEntity createReadings(Long chatId, String readingsText) {
        ReadingsEntity readings = new ReadingsEntity();
        readings.setChatId(chatId);
        readings.setReadingsText(readingsText);
        readings.setDateFillingReadings(LocalDateTime.now());
        return readings;
    }
}
