package com.example.ManagementCompanyBot.service.reportService;

import com.example.ManagementCompanyBot.config.properties.EmailProperties;
import com.example.ManagementCompanyBot.model.ReadingsEntity;
import com.example.ManagementCompanyBot.repository.ReadingsRepository;
import com.example.ManagementCompanyBot.service.EmailService;
import com.example.ManagementCompanyBot.utils.workWithFiles.GenerateExcelFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final EmailProperties emailProperties;
    private final ReadingsRepository readingsRepository;
    private final EmailService emailService;
    private final GenerateExcelFile generateExcelFile;

    @Override
    public void sendBotReportReadings(String email) {
        log.info("Starting to send the report on readings to email: {}", email);
        List<ReadingsEntity> readingsEntityList = readingsRepository.monthlyReadingsReport();

        if (checkingReadingsListIsEmpty(readingsEntityList)) {
            log.warn("The list of readings is empty. The report will not be sent.");
            return;
        }

        ByteArrayOutputStream attachment;
        try {
            attachment = generateExcelFile.generateExcelReport(readingsEntityList);
            log.info("The report was generated successfully.");
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return;
        }

        if (attachment != null) {
            String subject = "Отчет по показаниям от Telegram бота.";
            String body = "Получены показания";
            String fileName = "Отчет с показаниями";
            emailService.sendEmailWithAttachment(email, subject, body, attachment, fileName);
        } else {
            log.error("Failed to generate attachment for report.");
        }
    }

    @Scheduled(cron = "0 0 9 25 * ?")
    public void scheduledMonthlyReadingsReport() {
        String email = emailProperties.getUsername();
        log.info("Scheduled sending of the reading report to email: {}", email);
        sendBotReportReadings(email);
    }

    public boolean checkingReadingsListIsEmpty(List<ReadingsEntity> readingsEntityList) {
        return readingsEntityList.isEmpty();
    }
}
