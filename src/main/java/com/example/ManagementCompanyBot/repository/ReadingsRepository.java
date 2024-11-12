package com.example.ManagementCompanyBot.repository;

import com.example.ManagementCompanyBot.model.ReadingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingsRepository extends JpaRepository<ReadingsEntity, Long> {

    @Query("SELECT r FROM ReadingsEntity r " +
            "WHERE FUNCTION('MONTH', r.dateFillingReadings)=FUNCTION('MONTH', CURRENT_DATE)" +
            "AND FUNCTION('YEAR', r.dateFillingReadings)=FUNCTION('YEAR', CURRENT_DATE)")
    List<ReadingsEntity> monthlyReadingsReport();
}
