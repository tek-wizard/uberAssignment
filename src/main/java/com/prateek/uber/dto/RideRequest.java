package com.prateek.uber.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RideRequest {
    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotBlank(message = "Drop location is required")
    private String dropLocation;
}