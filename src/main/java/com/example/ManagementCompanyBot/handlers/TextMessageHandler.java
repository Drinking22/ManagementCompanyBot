package com.example.ManagementCompanyBot.handlers;

import com.example.ManagementCompanyBot.service.bot.BotServiceImpl;
import com.example.ManagementCompanyBot.utils.BotMessages;
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

        if (text.equals("/cansel")) {
            return handleCancel(chatId);
        }

        if (currentState == BotStates.WAITING_FOR_NEWS) {
            return processNewsInput(chatId, text);
        } else if (currentState == BotStates.WAITING_FOR_READINGS) {
            return processReadingsInput(chatId, text);
        } else {
            return processCommand(chatId, text, userName);
        }
    }

    public SendMessage handleCancel(long chatId) {
        log.info("Client cancels action");
        stateManager.setUserState(chatId, BotStates.IDLE);
        SendMessage responseCancelMessage = new SendMessage(String.valueOf(chatId),
                BotMessages.CANSEL_ACTION.getMessage());
        responseCancelMessage.setReplyMarkup(createKeyboardClass.createKeyboard());
        return responseCancelMessage;
    }

    public SendMessage processCommand(long chatId, String text, String userName) {
        switch (text) {
            case "/start":
                log.info("Client push start");
                return handleCommand(chatId, BotMessages.START_MESSAGE.getFormattedMessage(userName));

            case "/admin"://возможна работа админа
                log.info("Admin worked");
                return new SendMessage(String.valueOf(chatId), BotMessages.ADMIN_ACTION.getMessage());

            case "/cansel":
                log.info("Client cansel action");
                stateManager.setUserState(chatId, BotStates.IDLE);
                return handleCommand(chatId, BotMessages.CANSEL_ACTION.getMessage());

            default:
                log.info("Client wrong action");
                return new SendMessage(String.valueOf(chatId),
                        BotMessages.UNKNOWN_COMMAND.getMessage());

        }
    }

    public SendMessage processReadingsInput(long chatId, String text) {
        if (isTextIsShort(text)) {
            return sendMessageWhenTextIsShort(chatId);
        }

        log.info("Receiving readings from the client");
        botService.processingReadings(chatId, text);
        stateManager.removeUserState(chatId);
        return handleCommand(chatId, BotMessages.READINGS_RECEIVED.getMessage());
    }

    public SendMessage processNewsInput(long chatId, String text) {
        if (isTextIsShort(text)) {
            return sendMessageWhenTextIsShort(chatId);
        }

        log.info("Receiving news from the client");
        botService.processingNews(chatId, text);
        stateManager.removeUserState(chatId);
        return handleCommand(chatId, BotMessages.NEWS_RECEIVED.getMessage());
    }

    public SendMessage handleCommand(long chatId, String message) {
        SendMessage responseMessage = new SendMessage(String.valueOf(chatId), message);
        responseMessage.setReplyMarkup(createKeyboardClass.createKeyboard());
        return responseMessage;
    }

    public boolean isTextIsShort(String text) {
        return text == null || text.trim().length() < MIN_TEXT_LENGTH;
    }

    private static SendMessage sendMessageWhenTextIsShort(long chatId) {
        log.warn("Received empty readings input from the client");
        return new SendMessage(String.valueOf(chatId),
                BotMessages.SHORT_TEXT_ERROR.getMessage());
    }
}
