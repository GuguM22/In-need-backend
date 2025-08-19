package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.constant.Status;
import com.In_need.inNeedApp.dto.VerificationRequest;
import com.In_need.inNeedApp.dto.VerificationResponse;
import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.Verification;
import com.In_need.inNeedApp.repository.DocumentRepository;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.repository.VerificationRepository;
import com.In_need.inNeedApp.services.VerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/verify")
@CrossOrigin(origins = "http://localhost:4200")
public class VerificationController {
    private final VerificationRepository verificationRepository;
    private final VerificationService verificationService;
    private final UserRepository user;
    private final DocumentRepository documentRepository;

    @Autowired
    public VerificationController(VerificationRepository verificationRepository, VerificationService verificationService, UserRepository user, DocumentRepository documentRepository) {
        this.verificationRepository = verificationRepository;
        this.verificationService = verificationService;
        this.user = user;
        this.documentRepository = documentRepository;
    }

    @PostMapping("/verification")
    public ResponseEntity<Map<String, Object>> createVerification(
            @Valid @RequestBody VerificationRequest verificationRequest,
            Principal principal) {

        String userEmail = principal.getName(); // email from Spring Security context
        Users foundUser = user.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Verification verification = Verification.builder()
                .phoneNumber(verificationRequest.getPhone())
                .website(verificationRequest.getWebsite())
                .status(Status.PENDING)
                .email(userEmail)
                .user(foundUser)
                .build();

        verificationService.saveVerification(verification);

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

//    @PutMapping("/{id}/status")
//    public ResponseEntity<?> updateVerificationStatus(
//            @PathVariable Long id,
//            @RequestParam Status status) {
//
//        Optional<Verification> optionalVerification = verificationRepository.findById(id);
//        if (optionalVerification.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Verification verification = optionalVerification.get();
//        verification.setStatus(status);
//        verificationRepository.save(verification);
//
//        return ResponseEntity.ok(Map.of(
//                "id", verification.getId(),
//                "status", verification.getStatus(),
//                "message", "Verification status updated successfully"
//        ));
//    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateVerificationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        try {
            String statusStr = body.get("status");
            Status status = Status.valueOf(statusStr);

            verificationService.updateVerificationStatus(id, status);

            return ResponseEntity.ok(Map.of(
                    "id", id,
                    "status", status,
                    "message", "Verification status and user verification updated successfully"
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Something went wrong"));
        }
    }


    //    @PutMapping("/{id}/status")
//    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
//        Verification verification = verificationRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Verification not found"));
//
//        String statusStr = body.get("status");
//        Status status = Status.valueOf(statusStr);
//
//        verification.setStatus(status);
//        verificationRepository.save(verification);
//
//        return ResponseEntity.ok(Map.of(
//                "id", verification.getId(),
//                "status", verification.getStatus(),
//                "message", "Verification status updated successfully"
//        ));
//    }
    @GetMapping("/verifications/rejected")
    public ResponseEntity<List<VerificationResponse>> getRejectedVerifications() {
        List<VerificationResponse> rejected = verificationService.getAllByStatus(Status.REJECTED);
        return ResponseEntity.ok(rejected);
    }

    @GetMapping("/verifications/all")
    public ResponseEntity<List<VerificationResponse>> getAllVerifications() {
        List<Verification> allVerifications = verificationRepository.findAll();
        List<VerificationResponse> responseList = allVerifications.stream()
                .map(verificationService::mapToDto) // make mapToDto public if needed
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }


    @Transactional(readOnly = true)
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable("fileName") String fileName) {
        List<Documents> docOptional = documentRepository.findByFileName(fileName);

        if (docOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

//        Documents document = docOptional.get();
        Documents document = docOptional.get(0);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(document.getData());
    }

}