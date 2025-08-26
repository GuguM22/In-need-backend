package com.In_need.inNeedApp.dto;

import java.time.LocalDate;
import java.util.List;

public class SponsorRequestResponseDTO {
    private String title;
    private String priority;
    private int quantity;
    private LocalDate requiredDate;
    private String description;
    private List<String> mediaUrls;
    private String username;
}
