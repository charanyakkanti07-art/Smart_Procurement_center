package com.smartprocurement.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProcurementRateService {

    private final Map<String, Double> baseRatesPerKg = new HashMap<>();

    public ProcurementRateService() {
        baseRatesPerKg.put("Paddy", 25.0);
        baseRatesPerKg.put("Maize", 21.0);
        baseRatesPerKg.put("Wheat", 22.5);
        baseRatesPerKg.put("Cotton", 60.0);
        baseRatesPerKg.put("Red Gram", 70.0);
    }

    public double resolveRate(String cropType, String qualityGrade) {
        String crop = cropType != null ? cropType.trim() : "Paddy";
        double baseRate = baseRatesPerKg.getOrDefault(crop, 25.0);

        if (qualityGrade != null) {
            String grade = qualityGrade.toUpperCase().trim();
            if (grade.contains("A") || grade.contains("PREMIUM")) {
                return baseRate;
            } else if (grade.contains("B") || grade.contains("STANDARD")) {
                return Math.max(1.0, baseRate - 2.0);
            } else if (grade.contains("C") || grade.contains("FAIR")) {
                return Math.max(1.0, baseRate - 4.0);
            }
        }
        return baseRate;
    }
}
