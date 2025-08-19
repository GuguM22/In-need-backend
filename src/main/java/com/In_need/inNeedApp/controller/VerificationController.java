package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.constant.Role;
import com.In_need.inNeedApp.dto.VerificationRequest;
import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.Verification;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.repository.VerificationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/verify")
@CrossOrigin(origins = "http://localhost:4200")
public class VerificationController {

    private final VerificationRepository verificationRepository;
    private  final UserRepository userRepository;

    @Autowired
    public VerificationController(VerificationRepository verificationRepository, UserRepository userRepository) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/verification")
    public ResponseEntity<Map<String, Object>> createVerification(
            @Valid @RequestBody VerificationRequest verificationRequest,
            Authentication authentication) {

        // Get logged-in user
        String email = authentication.getName();
        Users user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only allow ORGANIZATION
        if (user.getRole() != Role.ORGANIZATION /*user.getRole() != Role.INDIVIDUAL*/) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Verification not required for your role"));
        }

        Verification verification = Verification.builder()
                .phoneNumber(verificationRequest.getPhone())
                .website(verificationRequest.getWebsite())
                .user(user)
                .build();

        verificationRepository.save(verification);

        // Mark user as verified
        user.setVerified(true);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", verification.getId());
        response.put("message", "Verification saved successfully");

        return ResponseEntity.ok(response);
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
        String normalizedPhone = phone.replaceAll("\\D", ""); // remove non-numeric chars
        if (normalizedPhone.length() < 10) {
            return ResponseEntity.badRequest().body(false);
        }
        String lastTenDigits = normalizedPhone.substring(normalizedPhone.length() - 10);
        boolean exists = verificationRepository.existsByLastTenDigits(lastTenDigits);
        return ResponseEntity.ok(exists);
    }


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

    @GetMapping("/verified")
    public ResponseEntity<Boolean> isUserVerified(Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user.isVerified());
    }
 
}