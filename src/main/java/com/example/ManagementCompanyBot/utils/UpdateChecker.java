package com.example.ManagementCompanyBot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class UpdateChecker {

    public boolean isTextMessage(Update update) {
        log.info("Checking update text in message");
        return update.hasMessage() && update.getMessage().hasText();
    }

    public boolean isCallbackQuery(Update update) {
        log.info("Checking callBackQuery");
        return update.hasCallbackQuery();
    }

    public boolean isPhotoFile(Update update) {
        log.info("Checking photo file");
        return update.getMessage().hasPhoto();
    }

    public boolean isVideoFile(Update update) {
        log.info("Checking video file");
        return update.getMessage().hasVideo();
    }

    public boolean isEditMessage(Update update) {
        return update.hasEditedMessage();
    }
}
