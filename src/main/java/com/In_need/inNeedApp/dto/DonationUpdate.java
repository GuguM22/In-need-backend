package com.In_need.inNeedApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DonationUpdate {
    @NotBlank
    private Long Id;
    @NotBlank
    private Boolean isAccepted;
}
