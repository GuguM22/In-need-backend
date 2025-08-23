package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.constant.DonationStatus;
import com.In_need.inNeedApp.dto.DonationRequest;
import com.In_need.inNeedApp.model.Donation;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public Donation createDonation(DonationRequest request, Users user) {
        Donation donation = new Donation();
        donation.setType(request.getType());
        donation.setDescription(request.getDescription());
        donation.setQuantity(request.getQuantity());
        donation.setAvailability(request.getAvailability());
        donation.setAdditionalNotes(request.getAdditionalNotes());
        donation.setPreference(request.getPreference());
        donation.setFrequency(request.getFrequency());

        donation.setDonorEmail(user.getEmail());
        donation.setDonorName(user.getUsername());
        donation.setProfileImageUrl(user.getProfileImageUrl());
        donation.setCreatedAt(LocalDateTime.now());

        return donationRepository.save(donation);
    }

   /* public List<Donation> getDonationsByEmail(String donorEmail) {
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
            dto.setProfileImageUrl(d.getProfileImageUrl());
            dto.setCreatedAt(d.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    // in DonationService
    public List<Donation> getPendingDonations() {
        return donationRepository.findByStatus(DonationStatus.PENDING);
    }

}
