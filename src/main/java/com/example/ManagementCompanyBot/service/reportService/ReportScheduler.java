package com.example.ManagementCompanyBot.service.reportService;

import com.example.ManagementCompanyBot.config.properties.EmailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportScheduler {

    private final EmailProperties emailProperties;
    private final ReportServiceImpl reportService;

    @Scheduled(cron = "0 0 9 25 * ?")
    public void scheduledMonthlyReadingsReport() {
        String email = emailProperties.getUsername();
        log.info("Scheduled sending of the reading report to email: {}", email);
        reportService.sendBotReportReadings(email);
    }

    @Scheduled(cron = "0 0 9 1 * ?")
    public void scheduleMonthlyNewsAndReadingsCount() {
        String email = emailProperties.getUsername();
        log.info("Scheduled sending count visits of readings and news report to email: {}", email);
        reportService.sendBotReportUsersInMonth(email);
    }
}
