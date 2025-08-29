package com.In_need.inNeedApp.controller;
import com.In_need.inNeedApp.constant.DonationStatus;
import com.In_need.inNeedApp.dto.DonationRequest;
 
import com.In_need.inNeedApp.dto.DonationUpdateRequest;
 
import com.In_need.inNeedApp.dto.DonationUpdate;
 
import com.In_need.inNeedApp.model.Donation;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.sponsor_request;
import com.In_need.inNeedApp.repository.DonationRepository;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.repository.sponsor_requestRepository;
import com.In_need.inNeedApp.services.DonationService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth/donations")
public class DonationController {
    private final DonationService donationService;
    private final UserRepository userRepository;
    private final DonationRepository donationRepository;
    private final sponsor_requestRepository sponsorRequestRepository;

    @Autowired
    public DonationController(DonationService donationService,
                              UserRepository userRepository,
                              DonationRepository donationRepository,
                              sponsor_requestRepository sponsorRequestRepository) {
        this.donationService = donationService;
        this.userRepository = userRepository;
        this.donationRepository = donationRepository;
        this.sponsorRequestRepository = sponsorRequestRepository;
    }

    @PostMapping("/post")
    public ResponseEntity<?> createDonation(@RequestBody @Valid DonationRequest request) {
        Optional<Users> userOpt = userRepository.findByEmailIgnoreCase(request.getDonorEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with email " + request.getDonorEmail() + " not found.");
        }

        Users user = userOpt.get();

        // Optional: link to sponsor request if provided
        sponsor_request sponsorRequest = null;
        if (request.getSponsorRequestId() != null) {
            sponsorRequest = sponsorRequestRepository.findById(request.getSponsorRequestId())
                    .orElseThrow(() -> new RuntimeException("Sponsor request not found"));
        }

        Donation savedDonation = donationService.createDonation(request, user, sponsorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDonation);
    }


 
   /* @GetMapping("/{email}")
 
    @PutMapping("/update")
    public ResponseEntity<?> updateDonation(@RequestBody DonationUpdate donationUpdate) {
        Donation updatedDonation = donationService.updateDonation(donationUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDonation);
    }

    @GetMapping("/{email}")
 
    public ResponseEntity<?> getDonationsByEmail(@PathVariable("email") String email) {
        List<DonationRequest> donations = donationService.getDonationsByEmail(email);
        if (donations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No donations found for email: " + email);
        }
        return ResponseEntity.ok(donations);
    }*/

    @GetMapping("/details")
    public ResponseEntity<List<DonationRequest>> getAllDonations() {
        List<Donation> donations = donationService.getAllDonations(); // only pending
        List<DonationRequest> dtoList = donations.stream().map(d -> {
            DonationRequest dto = new DonationRequest();
            BeanUtils.copyProperties(d, dto);
            dto.setStatus(d.getStatus());
            userRepository.findByEmailIgnoreCase(d.getDonorEmail())
                    .ifPresent(user -> {
                        dto.setDonorName(capitalizeWords(user.getUsername()));
                        dto.setDonorRole(user.getRole());
                        dto.setProfileImageUrl(user.getProfileImageUrl());
                    });

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
                    dto.setDonorEmail(capitalizeWords(donation.getDonorEmail()));
                    dto.setDonorName(capitalizeWords(donation.getDonorName()));
                    dto.setProfileImageUrl(donation.getProfileImageUrl());
                    return dto;
                })
                .toList();
    }

   /* @GetMapping("/pending")
    public List<Donation> getPendingDonations() {
        return donationRepository.findByStatus(DonationStatus.PENDING);
    }*/
   @GetMapping("/pending")
   public ResponseEntity<List<DonationRequest>> getPendingDonations() {
       List<Donation> donations = donationService.getDonationsByStatus(DonationStatus.PENDING);

       List<DonationRequest> dtoList = donations.stream().map(d -> {
           DonationRequest dto = new DonationRequest();
           BeanUtils.copyProperties(d, dto);

           // Explicitly set ID
           dto.setId(d.getId());
           dto.setStatus(d.getStatus());
           // Map user details
           userRepository.findByEmailIgnoreCase(d.getDonorEmail())
                   .ifPresent(user -> {
                       dto.setDonorName(capitalizeWords(user.getUsername()));
                       dto.setDonorRole(user.getRole());
                       dto.setProfileImageUrl(user.getProfileImageUrl());
                   });

           return dto;
       }).collect(Collectors.toList());

       return ResponseEntity.ok(dtoList);
   }


    @GetMapping("/accepted")
    public ResponseEntity<List<DonationRequest>> getAcceptedDonations() {
        List<Donation> donations = donationService.getDonationsByStatus(DonationStatus.ACCEPTED);

        List<DonationRequest> dtoList = donations.stream().map(d -> {
            DonationRequest dto = new DonationRequest();
            BeanUtils.copyProperties(d, dto);

            dto.setId(d.getId());
            dto.setStatus(d.getStatus());

            userRepository.findByEmailIgnoreCase(d.getDonorEmail())
                    .ifPresent(user -> {
                        dto.setDonorName(capitalizeWords(user.getUsername()));
                        dto.setDonorRole(user.getRole());
                        dto.setProfileImageUrl(user.getProfileImageUrl());
                    });

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/declined")
    public ResponseEntity<List<DonationRequest>> getDeclinedDonations() {
        List<Donation> donations = donationService.getDonationsByStatus(DonationStatus.DECLINED);

        List<DonationRequest> dtoList = donations.stream().map(d -> {
            DonationRequest dto = new DonationRequest();
            BeanUtils.copyProperties(d, dto);

            dto.setId(d.getId());
            dto.setStatus(d.getStatus());

            userRepository.findByEmailIgnoreCase(d.getDonorEmail())
                    .ifPresent(user -> {
                        dto.setDonorName(capitalizeWords(user.getUsername()));
                        dto.setDonorRole(user.getRole());
                        dto.setProfileImageUrl(user.getProfileImageUrl());
                    });

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateDonation(@RequestBody DonationUpdateRequest request) {
        if (request.getId() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Donation ID must not be null");
        }

        Donation updated = donationService.updateDonationStatus(request.getId(), request.getStatus());
        return ResponseEntity.ok(updated);
    }


    private String capitalizeWords(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        return Arrays.stream(input.trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donation> getDonationById(@PathVariable Long id) {
        return donationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
