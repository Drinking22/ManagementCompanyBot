package com.example.ManagementCompanyBot.handlers;

import com.example.ManagementCompanyBot.service.bot.BotServiceImpl;
import com.example.ManagementCompanyBot.utils.BotMessages;
import com.example.ManagementCompanyBot.utils.CreateKeyboardClass;
import com.example.ManagementCompanyBot.utils.states.UserStateManager;
import com.example.ManagementCompanyBot.utils.workWithFiles.DownloadAndWriteFiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;

import java.io.ByteArrayOutputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class VideoFileHandler implements FileHandlerInterface {

    private final UserStateManager stateManager;
    private final BotServiceImpl botService;
    private final DownloadAndWriteFiles downloadAndWriteFiles;
    private final CreateKeyboardClass createKeyboardClass;

    @Override
    public SendMessage handleFile(Update update, File downloadedFile) {
        long chatId = update.getMessage().getChatId();
        Video video = update.getMessage().getVideo();
        String fileId = video.getFileId();
        log.info("Received video with file ID: {}", fileId);

        try {
            ByteArrayOutputStream outputStream = downloadAndWriteFiles.getFileAsByteArrayOutputStream(downloadedFile);
            String fileName = "Video_" + System.currentTimeMillis()+ fileId;

            botService.sendEmailWithAttachment(outputStream, fileName);
            stateManager.removeUserState(chatId);
            SendMessage responseMessage = new SendMessage(String.valueOf(chatId),
                    BotMessages.VIDEO_SEND_ON_EMAIL.getMessage());
            responseMessage.setReplyMarkup(createKeyboardClass.createKeyboard());
            return responseMessage;

        } catch (Exception ex) {
            log.error("Error while processing video: {}", ex.getMessage());
            return new SendMessage (String.valueOf(chatId), BotMessages.PROCESSING_FILE_ERROR.getMessage());
        }
    }
}
