package com.In_need.inNeedApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {

    @NotBlank
    private String city;

    @NotBlank
    private String province;

   // private String addressLine;
}
