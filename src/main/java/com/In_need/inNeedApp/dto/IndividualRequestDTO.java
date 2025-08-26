package com.In_need.inNeedApp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class IndividualRequestDTO {
    private Long id;
    private String title;
    private String urgency;
    private int quantity;
    private LocalDate neededByDate;
    private String description;
    private List<String> mediaUrls;
    private String username;
    private LocalDateTime createdAt;
}
