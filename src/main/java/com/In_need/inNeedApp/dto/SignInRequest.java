package com.In_need.inNeedApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class SignInRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")  // Default validation
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 15, message = "Password must be 8-15 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain at least 1 uppercase, 1 lowercase, 1 number, 1 special character, and be at least 8 characters long"
    )

    private String password;

}
