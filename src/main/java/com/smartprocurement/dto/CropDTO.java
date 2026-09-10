package com.smartprocurement.dto;

public class CropDTO {
    private Long cropId;
    private String cropType;
    private Double quantity;
    private Long farmerId;

    public CropDTO() {
    }

    public CropDTO(Long cropId, String cropType, Double quantity, Long farmerId) {
        this.cropId = cropId;
        this.cropType = cropType;
        this.quantity = quantity;
        this.farmerId = farmerId;
    }

    public Long getCropId() { return cropId; }
    public void setCropId(Long cropId) { this.cropId = cropId; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public static CropDTOBuilder builder() {
        return new CropDTOBuilder();
    }

    public static class CropDTOBuilder {
        private Long cropId;
        private String cropType;
        private Double quantity;
        private Long farmerId;

        public CropDTOBuilder cropId(Long cropId) { this.cropId = cropId; return this; }
        public CropDTOBuilder cropType(String cropType) { this.cropType = cropType; return this; }
        public CropDTOBuilder quantity(Double quantity) { this.quantity = quantity; return this; }
        public CropDTOBuilder farmerId(Long farmerId) { this.farmerId = farmerId; return this; }

        public CropDTO build() {
            return new CropDTO(cropId, cropType, quantity, farmerId);
        }
    }
}
