package com.In_need.inNeedApp.controller;
import com.In_need.inNeedApp.dto.DonationRequest;
import com.In_need.inNeedApp.model.Donation;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.services.DonationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/donations")
public class DonationController {

    private final DonationService donationService;
    private final UserRepository userRepository;

    public DonationController(DonationService donationService, UserRepository userRepository) {
        this.donationService = donationService;
        this.userRepository = userRepository;
    }

    @PostMapping
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

    @GetMapping("/{email}")
    public ResponseEntity<?> getDonationsByEmail(@PathVariable("email") String email) {
        List<Donation> donations = donationService.getDonationsByEmail(email);
        if (donations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No donations found for email: " + email);
        }
        return ResponseEntity.ok(donations);
    }
}
