package com.example.ManagementCompanyBot.handlers;

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
public class CallbackQueryHandler {

    private final UserStateManager stateManager;

    public SendMessage handleCallbackQuery(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String command = update.getCallbackQuery().getData();
        BotStates currentState = stateManager.getUserState(chatId);

        switch (command) {
            case "submit_news":
                return handleCommand(chatId, currentState, BotStates.WAITING_FOR_NEWS,
                        "Введите текст сообщения:");

            case "submit_readings":
                return handleCommand(chatId, currentState, BotStates.WAITING_FOR_READINGS,
                        "Введите адрес и показания:\nНапоминаем, что показания принимаются с 15 по 20 число!");

            case "submit_photo":
                return handleCommand(chatId, currentState, BotStates.WAITING_FOR_PHOTO,
                        "Отправьте фотографию");

            case "submit_video":
                return handleCommand(chatId, currentState, BotStates.WAITING_FOR_VIDEO,
                        "Отправьте видеофайл");
        }
        log.warn("Unknown command from callback query: {}", command);
        return new SendMessage(String.valueOf(chatId), "Неизвестная команда из запроса обратного вызова");
    }

    private SendMessage handleCommand(long chatId, BotStates currentState, BotStates waitingState, String message) {
        if (currentState == BotStates.IDLE) {
            log.info("Client pushed the button {}", waitingState);
            stateManager.setUserState(chatId, waitingState);
            return new SendMessage(String.valueOf(chatId), message);
        } else {
            log.warn("User  is currently busy with another operation.");
            return new SendMessage(String.valueOf(chatId),
                    "Пожалуйста, завершите текущую операцию перед началом новой.");
        }
    }
}
