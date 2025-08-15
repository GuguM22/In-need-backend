package com.In_need.inNeedApp.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public class individualRequest {

    private String title;
    private String urgency;
    private int quantity;
    private LocalDate neededByDate;
    private String description;
    private List<MultipartFile> media;

    // Getters and setters
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrgency() {
        return urgency;
    }
    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getNeededByDate() {
        return neededByDate;
    }
    public void setNeededByDate(LocalDate neededByDate) {
        this.neededByDate = neededByDate;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<MultipartFile> getMedia() {
        return media;
    }
    public void setMedia(List<MultipartFile> media) {
        this.media = media;
    }
}
