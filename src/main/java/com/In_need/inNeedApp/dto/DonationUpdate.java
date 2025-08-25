package com.In_need.inNeedApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DonationUpdate {
    @NotNull(message = "Donation ID must not be null")
    private Long id;

    @NotNull(message = "Donation status is required")
    private String status;
}
