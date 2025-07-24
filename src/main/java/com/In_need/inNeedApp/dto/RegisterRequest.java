package com.In_need.inNeedApp.dto;

import com.In_need.inNeedApp.constant.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
    private String username;

    @NotNull(message = "Role is required")
    private Role role;

    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 15, message = "Password must be 8-15 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain 1 uppercase, 1 lowercase, 1 number, 1 special character"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }
}