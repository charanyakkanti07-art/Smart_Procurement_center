package com.smartprocurement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cropId;

    private String cropType;

    private Double quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    public Crop() {
    }

    public Crop(Long cropId, String cropType, Double quantity, Farmer farmer) {
        this.cropId = cropId;
        this.cropType = cropType;
        this.quantity = quantity;
        this.farmer = farmer;
    }

    public Long getCropId() { return cropId; }
    public void setCropId(Long cropId) { this.cropId = cropId; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public static CropBuilder builder() {
        return new CropBuilder();
    }

    public static class CropBuilder {
        private Long cropId;
        private String cropType;
        private Double quantity;
        private Farmer farmer;

        public CropBuilder cropId(Long cropId) { this.cropId = cropId; return this; }
        public CropBuilder cropType(String cropType) { this.cropType = cropType; return this; }
        public CropBuilder quantity(Double quantity) { this.quantity = quantity; return this; }
        public CropBuilder farmer(Farmer farmer) { this.farmer = farmer; return this; }

        public Crop build() {
            return new Crop(cropId, cropType, quantity, farmer);
        }
    }
}
