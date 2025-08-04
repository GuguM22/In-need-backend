package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.dto.DonationRequest;
import com.In_need.inNeedApp.model.Donation;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        donation.setAdditionalNotes(request.getAdditionalNotes()); // Fixed typo
        donation.setPreference(request.getPreference());
        donation.setFrequency(request.getFrequency());

        donation.setDonorEmail(user.getEmail()); // from verified user
        donation.setCreatedAt(LocalDateTime.now());

        return donationRepository.save(donation);
    }

    public List<Donation> getDonationsByEmail(String donorEmail) {
        return donationRepository.findByDonorEmailIgnoreCase(donorEmail);
    }
}
