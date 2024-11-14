package com.example.ManagementCompanyBot.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Getter
public enum BotMessages {
    CANSEL_ACTION("Выберете услугу:\n"),
    START_MESSAGE("Здравствуйте %s!\n\nДобро пожаловать в наш телеграм бот!\n\nВыберете услугу:\n"),
    ADMIN_ACTION("Выберете услугу:\n"),
    UNKNOWN_COMMAND("Неизвестная команда. Пожалуйста, попробуйте снова."),
    READINGS_RECEIVED("Показания переданы!\n\nВыберете услугу:\n"),
    NEWS_RECEIVED("Сообщение отправлено!\n\nВыберете услугу:\n"),
    SHORT_TEXT_ERROR("Текст вашего сообщения слишком короткий. Убедитесь в правильности ввода данных"),
    SUBMIT_NEWS("Введите текст сообщения:\nДля возврата в меню нажмите /cansel"),
    SUBMIT_READINGS("Введите адрес и показания:\n\nНапоминаем, что показания принимаются с 15 по 20 число!" +
            "\n\nДля возврата в меню нажмите /cansel"),
    SUBMIT_PHOTO("Отправьте фотографию\nДля возврата в меню нажмите /cansel"),
    SUBMIT_VIDEO("Отправьте видеофайл\nДля возврата в меню нажмите /cansel"),
    CURRENT_OPERATION_ERROR("Пожалуйста, завершите текущую операцию перед началом новой."),
    PHOTO_SEND_ON_EMAIL("Фотография отправлена на электронную почту!\n\nВыберите услугу:\n"),
    PROCESSING_FILE_ERROR("Произошла ошибка при обработке файла."),
    VIDEO_SEND_ON_EMAIL("Видеофайл отправлен на электронную почту!\n\nВыберите услугу:\n"),
    EDIT_MESSAGE_ERROR("Редактирование сообщений запрещено. Пожалуйста, отправьте новое сообщение.");

    private final String message;

    public String getFormattedMessage(String userName) {
        return String.format(message, userName);
    }
}
