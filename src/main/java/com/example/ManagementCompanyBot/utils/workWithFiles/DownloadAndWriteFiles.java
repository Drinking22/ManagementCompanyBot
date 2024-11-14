package com.example.ManagementCompanyBot.utils.workWithFiles;

import com.example.ManagementCompanyBot.config.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.File;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
@Slf4j
@RequiredArgsConstructor
public class DownloadAndWriteFiles {

    private final BotProperties botProperties;

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
}
