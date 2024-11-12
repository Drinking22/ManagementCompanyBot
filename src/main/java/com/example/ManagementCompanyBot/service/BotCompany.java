package com.example.ManagementCompanyBot.service;

import com.example.ManagementCompanyBot.config.properties.BotProperties;
import com.example.ManagementCompanyBot.service.bot.BotServiceImpl;
import com.example.ManagementCompanyBot.utils.CreateKeyboardClass;
import com.example.ManagementCompanyBot.utils.UpdateChecker;
import com.example.ManagementCompanyBot.utils.states.BotStates;
import com.example.ManagementCompanyBot.utils.states.UserStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotCompany extends TelegramLongPollingBot {

    private static final int MIN_TEXT_LENGTH = 10;

    private final BotProperties botProperties;
    private final CreateKeyboardClass replyKeyboard;

    private final UpdateChecker updateChecker;
    private final UserStateManager stateManager;
    private final BotServiceImpl botService;

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
            handleTextMessage(update);
        } else if (updateChecker.isCallbackQuery(update)) {
            handleCallbackQuery(update);
        } else if (updateChecker.isPhotoFile(update)) {
            handlePhotoFile(update);
        } else if (updateChecker.isVideoFile(update)) {
            handleVideoFile(update);
        }
    }

    public void handleTextMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String userName = update.getMessage().getFrom().getFirstName();

        BotStates currentState = stateManager.getUserState(chatId);

        if (currentState == BotStates.WAITING_FOR_NEWS) {
            processNewsInput(chatId, text);
        } else if (currentState == BotStates.WAITING_FOR_READINGS) {
            processReadingsInput(chatId, text);
        } else {
            processCommand(chatId, text, userName);
        }
    }

    public void processCommand(long chatId, String text, String userName) {
        switch (text) {
            case "/start":
                log.info("Client push start");
                sendMessageWithButtons(chatId, "Здравствуйте " + userName + "!" + "\n\n" +
                        "Добро пожаловать в наш телеграм бот!\n\nВыберете услугу:\n");
                break;
            case "/admin"://возможна работа админа
                log.info("Admin worked");
                sendMessageWithButtons(chatId, "Выберете услугу:\n");
                break;
            default:
                log.info("Client wrong action");
                sendMessageWithButtons(chatId, "Неизвестная команда. Пожалуйста, попробуйте снова.");
                break;
        }
    }

    public void processReadingsInput(long chatId, String text) {
        if (isTextIsShort(text)) {
            log.warn("Received empty readings input from the client");
            sendMessage(chatId, "Пожалуйста, введите показания. Текст вашего сообщения слишком короткий." +
                    "Убедитесь в правильности ввода данных");
            return;
        }

        log.info("Receiving readings from the client");
        botService.processingReadings(chatId, text);
        stateManager.removeUserState(chatId);
        sendMessageWithButtons(chatId, "Показания переданны!\n\nВыберете услугу:\n");
    }

    public void processNewsInput(long chatId, String text) {
        if (isTextIsShort(text)) {
            log.warn("Received empty news input from the client");
            sendMessage(chatId, "Пожалуйста, введите сообщение. " +
                    "Текст вашего сообщения слишком короткий. Убедитесь в правильности ввода данных");
            return;
        }

        log.info("Receiving news from the client");
        botService.processingNews(chatId, text);
        stateManager.removeUserState(chatId);
        sendMessageWithButtons(chatId, "Сообщение отправлено!\n\nВыберете услугу:\n");
    }

    public void handleCallbackQuery(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String command = update.getCallbackQuery().getData();

        BotStates currentState = stateManager.getUserState(chatId);

        switch (command) {
            case "submit_news":
                if (currentState == BotStates.IDLE) {
                    log.info("Client push the button submit_news");
                    sendMessage(chatId, "Введите текст сообщения:");
                    stateManager.setUserState(chatId, BotStates.WAITING_FOR_NEWS);
                } else {
                    sendMessage(chatId, "Пожалуйста, завершите текущую операцию перед началом новой.");
                }
                break;

            case "submit_readings":
                if (currentState == BotStates.IDLE) {
                    log.info("Client push the button submit_readings");
                    sendMessage(chatId, "Введите адрес и показания:\n" +
                            "Напоминаем, что показания принимаются с 15 по 20 число!");
                    stateManager.setUserState(chatId, BotStates.WAITING_FOR_READINGS);
                } else {
                    sendMessage(chatId, "Пожалуйста, завершите текущую операцию перед началом новой.");
                }
                break;

            case "submit_photo":
                if (currentState == BotStates.IDLE) {
                    log.info("Client push the button submit_photo");
                    sendMessage(chatId, "Отправьте фотографию");
                    stateManager.setUserState(chatId, BotStates.WAITING_FOR_PHOTO);
                } else {
                    sendMessage(chatId, "Пожалуйста, завершите текущую операцию перед началом новой.");
                }
                break;

            case "submit_video":
                if (currentState == BotStates.IDLE) {
                    log.info("Client push the button submit_video");
                    sendMessage(chatId, "Отправьте видео файл");
                    stateManager.setUserState(chatId, BotStates.WAITING_FOR_VIDEO);
                } else {
                    sendMessage(chatId, "Пожалуйста, завершите текущую операцию перед началом новой.");
                }
                break;

            default:
                log.warn("Unknown command from callback query: {}", command);
                break;
        }
    }

    public void handlePhotoFile(Update update) {
        long chatId = update.getMessage().getChatId();
        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize photo = photos.get(photos.size() - 1);
        String fileId = photo.getFileId();
        log.info("Received photo with file ID: {}", fileId);

        try {
            File downloadedFile = downloadVideoAndPhotoFile(fileId);
            ByteArrayOutputStream outputStream = getFileAsByteArrayOutputStream(downloadedFile);
            String fileName = "photo_" + System.currentTimeMillis() + fileId;
            botService.sendEmailWithAttachment(outputStream, fileName);
            sendMessageWithButtons(chatId, "Фотография отправлена на электронную почту!\n\n" +
                    "Выберите услугу:\n");
        } catch (Exception ex) {
            log.error("Error while processing photo: {}", ex.getMessage());
            sendMessage(chatId, "Произошла ошибка при обработке фотографии.");
        }

        stateManager.removeUserState(chatId);
    }

    public void handleVideoFile(Update update) {
        long chatId = update.getMessage().getChatId();
        Video video = update.getMessage().getVideo();
        String fileId = video.getFileId();
        log.info("Received video with file ID: {}", fileId);

        try {
            File downloadedFile = downloadVideoAndPhotoFile(fileId);
            ByteArrayOutputStream outputStream = getFileAsByteArrayOutputStream(downloadedFile);
            String fileName = "Video_" + System.currentTimeMillis()+ fileId;
            botService.sendEmailWithAttachment(outputStream, fileName);
            sendMessageWithButtons(chatId, "Видео отправлено!\n\nВыберете услугу:\n");
        } catch (TelegramApiException ex) {
            log.error("Error while processing video: {}", ex.getMessage());
            sendMessage(chatId, "Произошла ошибка при обработке видео.");
        }

        stateManager.removeUserState(chatId);
    }

    public File downloadVideoAndPhotoFile(String fileId) throws TelegramApiException {
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(fileId);
        return execute(getFileMethod);
    }

    public ByteArrayOutputStream getFileAsByteArrayOutputStream(File downloadedFile) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (InputStream inputStream = new URL("https://api.telegram.org/file/bot" +
                botProperties.getToken() + "/" + downloadedFile.getFilePath()).openStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("Error downloading file: {}", e.getMessage());
        }
        return outputStream;
    }

    public boolean isTextIsShort(String text) {
        return text == null || text.trim().length() < MIN_TEXT_LENGTH;
    }

    public void sendMessage(long chatId, String textMessage, InlineKeyboardMarkup keyboardMarkup) {
        log.info("Send message to client");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);//к строке?
        sendMessage.setText(textMessage);

        if (keyboardMarkup != null) {
            sendMessage.setReplyMarkup(keyboardMarkup);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessageWithButtons(long chatId, String textMessage) {
        sendMessage(chatId, textMessage, replyKeyboard.createKeyboard());
    }

    public void sendMessage(long chatId, String textMessage) {
        sendMessage(chatId, textMessage, null);
    }

    public void handleEditMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        log.warn("Ignoring edited message from chatId: {}", chatId);
        sendMessage(chatId, "Редактирование сообщений запрещено. Пожалуйста, отправьте новое сообщение.");
    }
}
