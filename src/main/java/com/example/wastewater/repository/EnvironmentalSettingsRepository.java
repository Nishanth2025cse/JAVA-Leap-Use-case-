package com.example.wastewater.repository;

import com.example.wastewater.entity.EnvironmentalSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentalSettingsRepository extends JpaRepository<EnvironmentalSettings, Long> {
}
