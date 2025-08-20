package com.In_need.inNeedApp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "individual_requests")
public class individual_request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String urgency; // low, medium, high

    private int quantity;

    private LocalDate neededByDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "individual_media", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls;

    // Constructors
    public individual_request() { }

    public individual_request(String title, String urgency, int quantity, LocalDate neededByDate, String description, List<String> mediaUrls) {
        this.title = title;
        this.urgency = urgency;
        this.quantity = quantity;
        this.neededByDate    = neededByDate;
        this.description = description;
        this.mediaUrls = mediaUrls;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getNeededByDate() { return neededByDate; }
    public void setNeededByDate(LocalDate neededByDate) { this.neededByDate = neededByDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }
}
