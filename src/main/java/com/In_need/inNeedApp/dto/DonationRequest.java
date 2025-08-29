package com.In_need.inNeedApp.dto;

import com.In_need.inNeedApp.constant.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class DonationRequest {
    @NotNull(message = "Donation ID must not be null")
    private Long id;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 1, message = "Quantity must be greater than zero")
    private int quantity;

    @NotBlank(message = "Availability is required")
    private String availability;

    @NotBlank(message = "Additional notes are required")
    private String additionalNotes;

    @NotNull(message = "Logistic preference is required")
    private LogisticPreference preference;

    @NotNull(message = "Donation type is required")
    private DonationType type;

    @NotNull(message = "Donation frequency is required")
    private DonationFrequency frequency;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Donor email is required")
    private String donorEmail;

    private LocalDateTime createdAt;

    private String profileImageUrl;

    private String donorName;

    private Role donorRole;

    private DonationStatus status;

    private Long sponsorRequestId;

    public String getTimeAgo() {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else {
            return days + (days == 1 ? " day ago" : " days ago");
        }
    }

}

