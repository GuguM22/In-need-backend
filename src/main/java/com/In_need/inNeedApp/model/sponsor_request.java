package com.In_need.inNeedApp.model;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sponsor_requests")
public class sponsor_request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String priority; // low, medium, high

    private int quantity;

    private LocalDate requiredDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "sponsorship_media", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @OneToMany(mappedBy = "sponsorRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<Donation> donations;

    // Getter & Setter
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    // Constructors
    public sponsor_request() {}

    public sponsor_request(String title, String priority, int quantity, LocalDate requiredDate, String description, List<String> mediaUrls, Users user) {
        this.title = title;
        this.priority = priority;
        this.quantity = quantity;
        this.requiredDate = requiredDate;
        this.description = description;
        this.mediaUrls = mediaUrls;
        this.location = location;
        this.user = user;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    public List<Donation> getDonations() {
        return donations;
    }
}

