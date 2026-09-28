package com.example.wastewater.repository;

import com.example.wastewater.entity.IndustrialFactory;
import com.example.wastewater.enums.FactoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IndustrialFactoryRepository extends JpaRepository<IndustrialFactory, Long> {
    Optional<IndustrialFactory> findByRegistrationNumber(String registrationNumber);

    List<IndustrialFactory> findByStatus(FactoryStatus status);

    @Query("SELECT f FROM IndustrialFactory f WHERE " +
           "LOWER(f.factoryName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.registrationNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.industryType) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<IndustrialFactory> searchFactories(@Param("query") String query);
}
