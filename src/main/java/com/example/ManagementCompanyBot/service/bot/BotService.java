package com.example.ManagementCompanyBot.service.bot;

import java.io.ByteArrayOutputStream;

public interface BotService {
    void processingNews(Long chatId, String newsText);
    void processingReadings(Long chatId, String readingsText);
    void sendEmail(String newsText);
    void sendEmailWithAttachment(ByteArrayOutputStream outputStream, String fileName);
}
