package com.smartprocurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class TravelTimeRequestDTO {

    @NotNull(message = "Origin location is required")
    @Valid
    private LocationDTO origin;

    @NotNull(message = "Destination location is required")
    @Valid
    private LocationDTO destination;

    public TravelTimeRequestDTO() {
    }

    public TravelTimeRequestDTO(LocationDTO origin, LocationDTO destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public LocationDTO getOrigin() { return origin; }
    public void setOrigin(LocationDTO origin) { this.origin = origin; }

    public LocationDTO getDestination() { return destination; }
    public void setDestination(LocationDTO destination) { this.destination = destination; }
}
