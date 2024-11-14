package com.example.ManagementCompanyBot.handlers;

import com.example.ManagementCompanyBot.service.bot.BotServiceImpl;
import com.example.ManagementCompanyBot.utils.CreateKeyboardClass;
import com.example.ManagementCompanyBot.utils.states.BotStates;
import com.example.ManagementCompanyBot.utils.states.UserStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class TextMessageHandler {

    private static final int MIN_TEXT_LENGTH = 10;

    private final UserStateManager stateManager;
    private final BotServiceImpl botService;
    private final CreateKeyboardClass createKeyboardClass;

    public SendMessage handleTextMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String userName = update.getMessage().getFrom().getFirstName();

        BotStates currentState = stateManager.getUserState(chatId);

        if (currentState == BotStates.WAITING_FOR_NEWS) {
            return processNewsInput(chatId, text);
        } else if (currentState == BotStates.WAITING_FOR_READINGS) {
            return processReadingsInput(chatId, text);
        } else {
            return processCommand(chatId, text, userName);
        }
    }

    public SendMessage processCommand(long chatId, String text, String userName) {
        switch (text) {
            case "/start":
                log.info("Client push start");
                return new SendMessage(String.valueOf(chatId), "Здравствуйте " + userName + "!" + "\n\n" +
                        "Добро пожаловать в наш телеграм бот!\n\nВыберете услугу:\n");

            case "/admin"://возможна работа админа
                log.info("Admin worked");
                return new SendMessage(String.valueOf(chatId), "Выберете услугу:\n");

            default:
                log.info("Client wrong action");
                return new SendMessage(String.valueOf(chatId),
                        "Неизвестная команда. Пожалуйста, попробуйте снова.");

        }
    }

    public SendMessage processReadingsInput(long chatId, String text) {
        if (isTextIsShort(text)) {
            return sendMessageWhenTextIsShort(chatId);
        }

        log.info("Receiving readings from the client");
        botService.processingReadings(chatId, text);
        stateManager.removeUserState(chatId);

        SendMessage responseMessage =
                new SendMessage(String.valueOf(chatId), "Показания переданы!\n\nВыберете услугу:\n");
        responseMessage.setReplyMarkup(createKeyboardClass.createKeyboard());
        return responseMessage;
    }

    public SendMessage processNewsInput(long chatId, String text) {
        if (isTextIsShort(text)) {
            return sendMessageWhenTextIsShort(chatId);
        }

        log.info("Receiving news from the client");
        botService.processingNews(chatId, text);
        stateManager.removeUserState(chatId);

        SendMessage responseMessage =
                new SendMessage(String.valueOf(chatId), "Сообщение отправлено!\n\nВыберете услугу:\n");
        responseMessage.setReplyMarkup(createKeyboardClass.createKeyboard());
        return responseMessage;
    }

    public boolean isTextIsShort(String text) {
        return text == null || text.trim().length() < MIN_TEXT_LENGTH;
    }

    private static SendMessage sendMessageWhenTextIsShort(long chatId) {
        log.warn("Received empty readings input from the client");
        return new SendMessage(String.valueOf(chatId),
                "Текст вашего сообщения слишком короткий. Убедитесь в правильности ввода данных");
    }
}
