package com.example.ManagementCompanyBot.service;

import com.example.ManagementCompanyBot.config.properties.EmailProperties;
import com.example.ManagementCompanyBot.model.NewsEntity;
import com.example.ManagementCompanyBot.model.ReadingsEntity;
import com.example.ManagementCompanyBot.repository.NewsRepository;
import com.example.ManagementCompanyBot.repository.ReadingsRepository;
import com.example.ManagementCompanyBot.service.bot.BotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BotServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private ReadingsRepository readingsRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailProperties emailProperties;

    @InjectMocks
    private BotServiceImpl botService;

    private Long chatId;
    private String newsText;
    private String readingsText;

    @BeforeEach
    void setUp() {
        chatId = 12345L;
        newsText = "newsTextExample";
        readingsText = "readingsTextExample";
    }

    @Test
    @DisplayName("Проверка сохранения сообщения в репозиторий")
    void testProcessingNews() {
        botService.processingNews(chatId, newsText);

        Mockito.verify(newsRepository).save(Mockito.any(NewsEntity.class));
    }

    @Test
    @DisplayName("Проверка сохранения показаний в репозиторий")
    void testProcessingReadings() {
        botService.processingReadings(chatId, readingsText);

        Mockito.verify(readingsRepository).save(Mockito.any(ReadingsEntity.class));
    }

    @Test
    @DisplayName("Проверка отправки e-mail сообщения")
    void testSendEmail() {
        Mockito.when(emailProperties.getUsername()).thenReturn("test@example.com");

        botService.sendEmail(newsText);

        Mockito.verify(emailService, Mockito.times(1))
                .sendEmail(Mockito.eq("test@example.com"),
                        Mockito.eq("Новость от Telegram бота."),
                         Mockito.eq("Получена новость: " + newsText));
    }

    @Test
    @DisplayName("Проверка создания сущности сообщения")
    void testCreateNews() {
        NewsEntity newsEntity = botService.createNews(chatId, newsText);

        assertEquals(chatId, newsEntity.getChatId());
        assertEquals(newsText, newsEntity.getNewsText());
    }

    @Test
    @DisplayName("Проверка создания сущности показаний")
    void testCreateReadings() {
        ReadingsEntity readingsEntity = botService.createReadings(chatId, readingsText);

        assertEquals(chatId, readingsEntity.getChatId());
        assertEquals(readingsText, readingsEntity.getReadingsText());
    }
}
