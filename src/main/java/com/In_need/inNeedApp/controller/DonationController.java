package com.In_need.inNeedApp.controller;
import com.In_need.inNeedApp.dto.DonationRequest;
import com.In_need.inNeedApp.dto.DonationUpdate;
import com.In_need.inNeedApp.model.Donation;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.services.DonationService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth/donations")
public class DonationController {
    private final DonationService donationService;
    private final UserRepository userRepository;

    public DonationController(DonationService donationService, UserRepository userRepository) {
        this.donationService = donationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/post")
    public ResponseEntity<?> createDonation(@RequestBody @Valid DonationRequest request) {
        Optional<Users> userOpt = userRepository.findByEmailIgnoreCase(request.getDonorEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with email " + request.getDonorEmail() + " not found.");
        }

        Users user = userOpt.get();
        Donation savedDonation = donationService.createDonation(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDonation);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateDonation(@RequestBody DonationUpdate donationUpdate) {
        Donation updatedDonation = donationService.updateDonation(donationUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDonation);
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getDonationsByEmail(@PathVariable("email") String email) {
        List<Donation> donations = donationService.getDonationsByEmail(email);
        if (donations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No donations found for email: " + email);
        }
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/details")
    public ResponseEntity<List<DonationRequest>> getAllDonations() {
        List<Donation> donations = donationService.getDonations();
        List<DonationRequest> dtoList = donations.stream().map(d -> {
            DonationRequest dto = new DonationRequest();
            BeanUtils.copyProperties(d, dto);

            Optional<Users> userOpt = userRepository.findByEmailIgnoreCase(d.getDonorEmail());
            userOpt.ifPresent(user -> dto.setProfileImageUrl(user.getProfileImagePath()));

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
    @GetMapping("/donations/details")
    public List<DonationRequest> getDonations() {
        return donationService.getDonations()
                .stream()
                .map(donation -> {
                    DonationRequest dto = new DonationRequest();
                   // dto.set(donation.getId());
                    dto.setDescription(donation.getDescription());
                    dto.setDonorEmail(donation.getDonorEmail());
                    dto.setDonorName(donation.getDonorName());
                    dto.setProfileImageUrl(donation.getProfileImageUrl());
                    return dto;
                })
                .toList();
    }


}
