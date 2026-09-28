package com.example.wastewater.repository;

import com.example.wastewater.entity.TelemetryReading;
import com.example.wastewater.enums.ComplianceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TelemetryReadingRepository extends JpaRepository<TelemetryReading, Long> {
    List<TelemetryReading> findByFactoryId(Long factoryId);
    List<TelemetryReading> findByStatus(ComplianceStatus status);
    
    @Query("SELECT COUNT(t) FROM TelemetryReading t WHERE t.measurementDateTime >= :startOfDay")
    long countTodayReadings(@Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT COUNT(t) FROM TelemetryReading t WHERE t.measurementDateTime >= :startOfDay AND t.status = :status")
    long countTodayReadingsByStatus(@Param("startOfDay") LocalDateTime startOfDay, @Param("status") ComplianceStatus status);

    List<TelemetryReading> findTop10ByOrderByMeasurementDateTimeDesc();
}
