package com.In_need.inNeedApp.model;

import com.In_need.inNeedApp.constant.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "donations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String availability;

    @Column(name = "additional_notes", nullable = false)
    private String additionalNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "logistic_preference", nullable = false)
    private LogisticPreference preference;

    @Enumerated(EnumType.STRING)
    @Column(name = "donation_type", nullable = false)
    private DonationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "donation_frequency", nullable = false)
    private DonationFrequency frequency;

    @Column(name = "profile_image")
    private String profileImageUrl;

    @Column(name = "donor_email", nullable = false)
    private String donorEmail;

    @Column(name = "donor_name",  nullable = false)
    private String donorName;

    @Column(name = "donor_role")
    private Role donorRole;
    @Column(name = "createdAt")
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime createdAt;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status = DonationStatus.PENDING;
 
    private Boolean isAccepted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_request_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private sponsor_request sponsorRequest;

    @Column(name = "is_received")
    private Boolean isReceived = false;

}
