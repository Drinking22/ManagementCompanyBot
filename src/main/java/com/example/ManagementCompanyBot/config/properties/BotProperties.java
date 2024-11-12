package com.example.ManagementCompanyBot.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class BotProperties {

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;
}
