package com.example.ManagementCompanyBot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CreateKeyboardClass {

    public InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(createRow("Отправить сообщение", "submit_news"));
        buttons.add(createRow("Передать показания", "submit_readings"));
        buttons.add(createRow("Отправить фотографию", "submit_photo"));
        buttons.add(createRow("Отправить фидео файл", "submit_video"));

        keyboard.setKeyboard(buttons);

        return keyboard;
    }

    public List<InlineKeyboardButton> createRow(String text, String callbackData) {
        InlineKeyboardButton button = createButton(text, callbackData);
        return List.of(button);
    }

    public InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}
