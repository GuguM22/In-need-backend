package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.constant.Status;
import com.In_need.inNeedApp.dto.VerificationRequest;
import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.Verification;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.repository.VerificationRepository;
import com.In_need.inNeedApp.services.VerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/verify")
@CrossOrigin(origins = "http://localhost:4200")
public class VerificationController {

    private final VerificationRepository verificationRepository;
    private  final VerificationService verificationService;
    private final UserRepository userRepository;

    @Autowired
    public VerificationController(VerificationRepository verificationRepository,
                                  VerificationService verificationService,
                                  UserRepository userRepository) {
        this.verificationRepository = verificationRepository;
        this.verificationService = verificationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/verification")
    public ResponseEntity<?> createVerification(
            @Valid @RequestBody VerificationRequest request,
            Authentication authentication) {

        try {
            // Get email from authenticated user
            String userEmail = authentication.getName();

            // Find user by email
            Users user =userRepository.findByEmailIgnoreCase(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create verification entity
            Verification verification = new Verification();
            verification.setEmail(userEmail);
            verification.setPhoneNumber(request.getPhone());
            verification.setWebsite(request.getWebsite());
            verification.setUser(user);
            verification.setStatus(Status.PENDING);

            // Use existing save method
            Verification savedVerification = verificationService.saveVerification(verification);
            return ResponseEntity.ok(savedVerification);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating verification: " + e.getMessage());
        }
    }
    @GetMapping("/phone/{phone}")
    public ResponseEntity<Verification> getByPhone(@PathVariable String phone) {
        Optional<Verification> verification = verificationRepository.findByPhoneNumber(phone);
        return verification
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/website")
    public ResponseEntity<Verification> getByWebsite(@RequestParam String url) {
        Optional<Verification> verification = verificationRepository.findByWebsite(url);
        return verification
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/phone/{phone}")
    public ResponseEntity<Boolean> phoneExists(@PathVariable String phone) {
        boolean exists = verificationRepository.existsByPhoneNumber(phone);
        return ResponseEntity.ok(exists);
    }

   /* @GetMapping("/verification/status/{userId}")
    public ResponseEntity<?> getVerificationStatus(@PathVariable Long userId) {
        try {
            // Check if user has any approved verification
            Optional<Verification> approvedVerification = verificationRepository.findByUserIdAndStatus(userId, Status.APPROVED);

            if (approvedVerification.isPresent()) {
                return ResponseEntity.ok("APPROVED");
            }

            // Check if user has any pending verification
            Optional<Verification> pendingVerification = verificationRepository.findByUserIdAndStatus(userId, Status.PENDING);

            if (pendingVerification.isPresent()) {
                return ResponseEntity.ok("PENDING");
            }

            return ResponseEntity.ok("NONE");

        } catch (Exception e) {
            // Log the actual error for debugging
            System.err.println("Error in getVerificationStatus: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking status: " + e.getMessage());
        }
    }*/



//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, Object>> uploadFiles(
//            @RequestParam("files") MultipartFile[] files,
//            @RequestParam("verificationId") Long verificationId) throws IOException {
//
//        Verification verification = verificationRepository.findById(verificationId)
//                .orElseThrow(() -> new RuntimeException("Verification not found"));
//
//        List<String> urls = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//            String url = "/files/" + file.getOriginalFilename();
//            urls.add(url);
//        }
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("urls", urls);
//
//        return ResponseEntity.ok(response);
//    }
@PostMapping("/upload")
public ResponseEntity<Map<String, Object>> uploadFiles(
        @RequestParam("files") MultipartFile[] files,
        @RequestParam("verificationId") Long verificationId) throws IOException {

    Verification verification = verificationRepository.findById(verificationId)
            .orElseThrow(() -> new RuntimeException("Verification not found"));

    List<String> urls = new ArrayList<>();

    for (MultipartFile file : files) {
        Documents document = Documents.builder()
                .fileName(file.getOriginalFilename())
                .data(file.getBytes())
                .verification(verification) // maintain the owning side
                .build();

        verification.getDocuments().add(document); // attach to parent list
        urls.add("/files/" + file.getOriginalFilename());
    }

    verificationRepository.save(verification); // cascade will save docs

    Map<String, Object> response = new HashMap<>();
    response.put("urls", urls);

    return ResponseEntity.ok(response);
}

}