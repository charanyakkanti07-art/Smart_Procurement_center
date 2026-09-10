package com.smartprocurement.dto;

public class TravelTimeResponseDTO {
    private boolean success;
    private TravelTimeDataDTO data;
    private String message;

    public TravelTimeResponseDTO() {
    }

    public TravelTimeResponseDTO(boolean success, TravelTimeDataDTO data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public TravelTimeDataDTO getData() { return data; }
    public void setData(TravelTimeDataDTO data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static TravelTimeResponseDTO success(TravelTimeDataDTO data) {
        return new TravelTimeResponseDTO(true, data, "Travel-time calculation successful");
    }

    public static TravelTimeResponseDTO error(String message) {
        return new TravelTimeResponseDTO(false, null, message);
    }
}
