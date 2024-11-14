package com.example.ManagementCompanyBot.service;

import com.example.ManagementCompanyBot.config.properties.BotProperties;
import com.example.ManagementCompanyBot.handlers.*;
import com.example.ManagementCompanyBot.utils.UpdateChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotCompany extends TelegramLongPollingBot {

    private final BotProperties botProperties;

    private final TextMessageHandler textMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final PhotoFileHandler photoFileHandler;
    private final VideoFileHandler videoFileHandler;

    private final UpdateChecker updateChecker;

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (updateChecker.isEditMessage(update)) {
            handleEditMessage(update);
            return;
        }
        if (updateChecker.isTextMessage(update)) {
            sendMessage(textMessageHandler.handleTextMessage(update));
        } else if (updateChecker.isCallbackQuery(update)) {
            sendMessage(callbackQueryHandler.handleCallbackQuery(update));
        } else if (updateChecker.isPhotoFile(update)) {
            handleFileUpdate(update, photoFileHandler);
        } else if (updateChecker.isVideoFile(update)) {
            handleFileUpdate(update, videoFileHandler);
        }
    }

    public void handleFileUpdate(Update update, FileHandlerInterface handler) {
        String fileId = getFileId(update);
        File downloadedFile = downloadVideoAndPhotoFile(fileId);
        if (downloadedFile != null) {
            sendMessage(handler.handleFile(update, downloadedFile));
        }
    }

    public String getFileId(Update update) {
        if (updateChecker.isPhotoFile(update)) {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            return photos.get(photos.size() - 1).getFileId();
        } else if (updateChecker.isVideoFile(update)) {
            return update.getMessage().getVideo().getFileId();
        }
        return null;
    }

    public File downloadVideoAndPhotoFile(String fileId) {
        try {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);
            return execute(getFileMethod);
        } catch (TelegramApiException e) {
            log.error("Error downloading file with ID {}: {}", fileId, e.getMessage());
            return null;
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        log.info("Send message to client");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public SendMessage handleEditMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        log.warn("Ignoring edited message from chatId: {}", chatId);
        return new SendMessage(String.valueOf(chatId),
                "Редактирование сообщений запрещено. Пожалуйста, отправьте новое сообщение.");
    }
}