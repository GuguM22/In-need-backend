package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.constant.Status;
import com.In_need.inNeedApp.dto.VerificationRequest;
import com.In_need.inNeedApp.dto.VerificationResponse;
import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.model.Verification;
import com.In_need.inNeedApp.repository.VerificationRepository;
import com.In_need.inNeedApp.services.VerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/verify")
@CrossOrigin(origins = "http://localhost:4200")
public class VerificationController {
    private final VerificationRepository verificationRepository;
    private final VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationRepository verificationRepository, VerificationService verificationService) {
        this.verificationRepository = verificationRepository;
        this.verificationService = verificationService;
    }

    @PostMapping("/verification")
    public ResponseEntity<Map<String, Object>> createVerification(
            @Valid @RequestBody VerificationRequest verificationRequest,
            Principal principal) {

        String userEmail = principal.getName(); // email from Spring Security context

        Verification verification = Verification.builder()
                .phoneNumber(verificationRequest.getPhone())
                .website(verificationRequest.getWebsite())
                .status(Status.PENDING)
                .email(userEmail) // set email directly
                .build();

        verificationRepository.save(verification);

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
        boolean exists = verificationRepository.existsByPhoneNumber(phone);
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

    @GetMapping("/verifications/pending")
    public ResponseEntity<List<VerificationResponse>> getPendingVerifications() {
        List<VerificationResponse> pending = verificationService.getAllByStatus(Status.PENDING);
        return ResponseEntity.ok(pending);
    }



}