package com.smartprocurement.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CentreRecommendationResponseDTO {
    private RecommendationDTO recommendedCentre;
    private List<RecommendationDTO> centres;
    private LocalDateTime calculationTimestamp;
    private String recommendationSummary;

    public CentreRecommendationResponseDTO() {
    }

    public CentreRecommendationResponseDTO(RecommendationDTO recommendedCentre, List<RecommendationDTO> centres, LocalDateTime calculationTimestamp, String recommendationSummary) {
        this.recommendedCentre = recommendedCentre;
        this.centres = centres;
        this.calculationTimestamp = calculationTimestamp;
        this.recommendationSummary = recommendationSummary;
    }

    public RecommendationDTO getRecommendedCentre() { return recommendedCentre; }
    public void setRecommendedCentre(RecommendationDTO recommendedCentre) { this.recommendedCentre = recommendedCentre; }

    public List<RecommendationDTO> getCentres() { return centres; }
    public void setCentres(List<RecommendationDTO> centres) { this.centres = centres; }

    public LocalDateTime getCalculationTimestamp() { return calculationTimestamp; }
    public void setCalculationTimestamp(LocalDateTime calculationTimestamp) { this.calculationTimestamp = calculationTimestamp; }

    public String getRecommendationSummary() { return recommendationSummary; }
    public void setRecommendationSummary(String recommendationSummary) { this.recommendationSummary = recommendationSummary; }

    public static CentreRecommendationResponseDTOBuilder builder() {
        return new CentreRecommendationResponseDTOBuilder();
    }

    public static class CentreRecommendationResponseDTOBuilder {
        private RecommendationDTO recommendedCentre;
        private List<RecommendationDTO> centres;
        private LocalDateTime calculationTimestamp;
        private String recommendationSummary;

        public CentreRecommendationResponseDTOBuilder recommendedCentre(RecommendationDTO recommendedCentre) { this.recommendedCentre = recommendedCentre; return this; }
        public CentreRecommendationResponseDTOBuilder centres(List<RecommendationDTO> centres) { this.centres = centres; return this; }
        public CentreRecommendationResponseDTOBuilder calculationTimestamp(LocalDateTime calculationTimestamp) { this.calculationTimestamp = calculationTimestamp; return this; }
        public CentreRecommendationResponseDTOBuilder recommendationSummary(String recommendationSummary) { this.recommendationSummary = recommendationSummary; return this; }

        public CentreRecommendationResponseDTO build() {
            return new CentreRecommendationResponseDTO(recommendedCentre, centres, calculationTimestamp, recommendationSummary);
        }
    }
}
