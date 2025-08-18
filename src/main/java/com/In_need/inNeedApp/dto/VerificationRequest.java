package com.In_need.inNeedApp.dto;

import com.In_need.inNeedApp.model.Documents;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 characters")
    @Pattern(regexp = "^(0|27|\\+27)[0-9]{9}$", message = "Phone number format is invalid")
    private String phone;

    @NotBlank(message = "Website is required")
    @Pattern(
            regexp = "^https://(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,}(/\\S*)?$",
            message = "Website must be a valid HTTPS link"
    )
    private String website;
    @NotEmpty(message = "At least one document URL must be provided")
    private List<String> documents;
    private Long userId;
}
