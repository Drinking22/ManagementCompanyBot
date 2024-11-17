package com.example.ManagementCompanyBot.service.reportService;

import com.example.ManagementCompanyBot.config.properties.EmailProperties;
import com.example.ManagementCompanyBot.model.ReadingsEntity;
import com.example.ManagementCompanyBot.repository.NewsRepository;
import com.example.ManagementCompanyBot.repository.ReadingsRepository;
import com.example.ManagementCompanyBot.service.EmailService;
import com.example.ManagementCompanyBot.utils.workWithFiles.GenerateExcelFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final EmailProperties emailProperties;
    private final ReadingsRepository readingsRepository;
    private final NewsRepository newsRepository;
    private final EmailService emailService;
    private final GenerateExcelFile generateExcelFile;



    @Override
    public void sendBotReportReadings(String email) {
        log.info("Starting to send the report on readings to email: {}", email);
        List<ReadingsEntity> readingsEntityList = getReadingsEntityList();

        if (isReadingsListEmpty(readingsEntityList)) {
            log.warn("The list of readings is empty. The report will not be sent.");
            return;
        }

        ByteArrayOutputStream attachment;
        try {
            attachment = getAttachment(readingsEntityList);
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

    @Override
    public void sendBotReportUsersInMonth(String email) {
        log.info("Starting to send the report of count visits to email: {}", email);
        LocalDate startDate = LocalDate.now().minusMonths(1);
        Long countNewsVisits = getCountNewsVisits(startDate);
        Long countReadingsVisits = getCountReadingsVisits(startDate);
        long totalQuantity = countNewsVisits + countReadingsVisits;

        if (totalQuantity == 0) {
            log.warn("Total quantity of visits is empty. The report will not be sent.");
            return;
        }

        String subject = "Отчет по количеству посещений Telegram бота.";
        String body = "Количетсво взаимодейтвий с Telegram ботом за прошлый месяц - " + totalQuantity + "\n" +
                "Количество отправленных сообщений - " + countNewsVisits + "\n" +
                "Количество отправленных показаний" + countReadingsVisits;
        emailService.sendEmail(email, subject, body);
    }

    public List<ReadingsEntity> getReadingsEntityList() {
        return readingsRepository.monthlyReadingsReport();
    }

    public ByteArrayOutputStream getAttachment(List<ReadingsEntity> readingsEntityList) throws IOException {
        return generateExcelFile.generateExcelReport(readingsEntityList);
    }

    public Long getCountReadingsVisits(LocalDate startDate) {
        return readingsRepository.countVisitsReadingsBot(startDate);
    }

    public Long getCountNewsVisits(LocalDate startDate) {
        return newsRepository.countVisitsNewsBot(startDate);
    }

    public boolean isReadingsListEmpty(List<ReadingsEntity> readingsEntityList) {
        return readingsEntityList.isEmpty();
    }
}
