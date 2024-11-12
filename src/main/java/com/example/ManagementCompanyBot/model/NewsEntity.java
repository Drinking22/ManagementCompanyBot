package com.example.ManagementCompanyBot.model;

import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "news")
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "news_text", nullable = false)
    private String newsText;

    @Column(name = "date_message_sending", nullable = false)
    private LocalDateTime dateMessageSending;
}
