package com.example.ManagementCompanyBot.model;

import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "readings")
public class ReadingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "readings_text", nullable = false)
    private String readingsText;

    @Column(name = "date_filling_readings", nullable = false)
    private LocalDateTime dateFillingReadings;
}
