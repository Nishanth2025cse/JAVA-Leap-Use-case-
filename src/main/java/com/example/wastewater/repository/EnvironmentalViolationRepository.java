package com.example.wastewater.repository;

import com.example.wastewater.entity.EnvironmentalViolation;
import com.example.wastewater.enums.ViolationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnvironmentalViolationRepository extends JpaRepository<EnvironmentalViolation, Long> {
    List<EnvironmentalViolation> findByFactoryId(Long factoryId);
    List<EnvironmentalViolation> findByFactoryIdAndStatus(Long factoryId, ViolationStatus status);
    List<EnvironmentalViolation> findByStatus(ViolationStatus status);
    
    @Query("SELECT SUM(v.fineAmount) FROM EnvironmentalViolation v")
    Double getTotalFineAmount();

    @Query("SELECT COUNT(v) FROM EnvironmentalViolation v WHERE v.status = :status")
    long countByStatus(@Param("status") ViolationStatus status);

    List<EnvironmentalViolation> findTop10ByOrderByViolationDateDesc();

    @Query("SELECT v.factory.id, v.factory.factoryName, COUNT(v), SUM(v.fineAmount) " +
           "FROM EnvironmentalViolation v GROUP BY v.factory.id, v.factory.factoryName " +
           "ORDER BY COUNT(v) DESC")
    List<Object[]> findFactoryWiseViolationSummary();
}
