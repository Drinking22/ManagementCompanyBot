package com.example.ManagementCompanyBot.repository;

import com.example.ManagementCompanyBot.model.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, Long> {

    @Query("SELECT COUNT(n.chatId) FROM NewsEntity n " +
            "WHERE n.dateMessageSending >= :startDate " +
            "AND n.dateMessageSending < CURRENT_DATE")
    Long countVisitsNewsBot(@Param("startDate") LocalDate startDate);
}
