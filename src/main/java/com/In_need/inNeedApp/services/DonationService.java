package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.constant.DonationStatus;
import com.In_need.inNeedApp.dto.DonationRequest;
import com.In_need.inNeedApp.dto.DonationUpdate;
import com.In_need.inNeedApp.model.Donation;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.sponsor_request;
import com.In_need.inNeedApp.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public Donation createDonation(DonationRequest request, Users user, sponsor_request sponsorRequest) {
        Donation donation = new Donation();

        donation.setDescription(request.getDescription());
        donation.setQuantity(request.getQuantity());
        donation.setAvailability(request.getAvailability());
        donation.setAdditionalNotes(request.getAdditionalNotes());
        donation.setPreference(request.getPreference());
        donation.setType(request.getType());
        donation.setFrequency(request.getFrequency());
        donation.setDonorEmail(request.getDonorEmail());
        donation.setDonorName(user.getUsername());
        donation.setDonorRole(user.getRole());
        donation.setCreatedAt(LocalDateTime.now());
        donation.setStatus(DonationStatus.PENDING);

        // 🔥 Critical line to associate the donation with the sponsor request
        if (sponsorRequest != null) {
            donation.setSponsorRequest(sponsorRequest);
        }

        return donationRepository.save(donation);
    }



   /* public List<Donation> getDonationsByEmail(String donorEmail) {

    public Donation updateDonation(DonationUpdate donationUpdate) {
        Donation donation = donationRepository.findById(donationUpdate.getId())
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        donation.setIsAccepted(donationUpdate.getIsAccepted());

        return donationRepository.save(donation);
    }


    public List<Donation> getDonationsByEmail(String donorEmail) {

        return donationRepository.findByDonorEmailIgnoreCase(donorEmail);
    }*/

    public List<Donation> getDonations() {
        return donationRepository.findAll();
    }

    public Donation updateDonationStatus(Donation donation, boolean isAccepted) {
        donation.setStatus(isAccepted ? DonationStatus.ACCEPTED : DonationStatus.DECLINED);
        return donationRepository.save(donation);
    }

    public List<Donation> getDonationsByStatus(DonationStatus status) {
        return donationRepository.findByStatus(status);
    }
    public Donation updateDonationStatus(Long id, DonationStatus status) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        donation.setStatus(status);
        return donationRepository.save(donation);
    }

    public List<DonationRequest> getDonationsByEmail(String donorEmail) {
        List<Donation> donations = donationRepository.findByDonorEmailIgnoreCase(donorEmail);

        return donations.stream().map(d -> {
            DonationRequest dto = new DonationRequest();
            dto.setDescription(d.getDescription());
            dto.setQuantity(d.getQuantity());
            dto.setAvailability(d.getAvailability());
            dto.setAdditionalNotes(d.getAdditionalNotes());
            dto.setPreference(d.getPreference());
            dto.setType(d.getType());
            dto.setFrequency(d.getFrequency());
            dto.setDonorEmail(d.getDonorEmail());
            dto.setDonorName(d.getDonorName());
            dto.setDonorRole(d.getDonorRole());
            dto.setProfileImageUrl(d.getProfileImageUrl());
            dto.setCreatedAt(d.getCreatedAt());

            dto.setSponsorRequestId(
                    d.getSponsorRequest() != null ? d.getSponsorRequest().getId() : null
            );

            return dto;
        }).collect(Collectors.toList());
    }


    // in DonationService
    public List<Donation> getPendingDonations() {
        return donationRepository.findByStatus(DonationStatus.PENDING);
    }

    public Optional<Donation> findById(Long id) {
        return donationRepository.findById(id);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Donation confirmDonationReceived(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        if (donation.getStatus() == DonationStatus.ACCEPTED) {  // use status instead of isAccepted
            donation.setIsReceived(true);
            return donationRepository.save(donation);
        } else {
            throw new RuntimeException("Donation must be accepted before confirming receipt");
        }
    }



}
