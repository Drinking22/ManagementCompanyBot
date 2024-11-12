package com.example.ManagementCompanyBot.repository;

import com.example.ManagementCompanyBot.model.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
}
