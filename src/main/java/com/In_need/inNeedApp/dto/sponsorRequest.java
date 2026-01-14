package com.In_need.inNeedApp.dto;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDate;
@Service
public class sponsorRequest {private String title;
    private String priority;
    private int quantity;
    private LocalDate requiredDate;
    private String description;
    private List<MultipartFile> media;

    private List<String> mediaUrls;
    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getRequiredDate() { return requiredDate; }
    public void setRequiredDate(LocalDate requiredDate) { this.requiredDate = requiredDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<MultipartFile> getMedia() { return media; }
    public void setMedia(List<MultipartFile> media) { this.media = media; }
}


