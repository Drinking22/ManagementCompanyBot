package com.example.ManagementCompanyBot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface FileHandlerInterface {
    SendMessage handleFile(Update update, File downloadedFile);
}
