package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.dto.sponsorRequest;
import com.In_need.inNeedApp.model.sponsor_request;
import com.In_need.inNeedApp.services.SponsorRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/sponsor-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class sponsor_requestController {

    private final SponsorRequestService sponsorRequestService;
    private final Path uploadDir = Paths.get("uploads");

    public sponsor_requestController(SponsorRequestService sponsorRequestService) {
        this.sponsorRequestService = sponsorRequestService;

        try {
            Files.createDirectories(uploadDir);
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @PostMapping( consumes = {"multipart/form-data"})
    public ResponseEntity<sponsor_request> createRequest(
            @RequestPart("title") String title,
            @RequestPart("priority") String priority,
            @RequestPart("quantity") int quantity,
            @RequestPart("requiredDate") String requiredDate,
            @RequestPart("description") String description,
            @RequestPart(value = "media", required = false) List<MultipartFile> mediaFiles) {

        List<String> mediaUrls = new ArrayList<>();

        if (mediaFiles != null) {
            for (MultipartFile file : mediaFiles) {
                try {
                    Path filePath = uploadDir.resolve(file.getOriginalFilename());
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    mediaUrls.add(filePath.toString());
                } catch (Exception e) {
                    throw new RuntimeException("File upload failed: " + file.getOriginalFilename(), e);
                }
            }
        }

        sponsor_request request = new sponsor_request(
                title,
                priority,
                quantity,
                LocalDate.parse(requiredDate),
                description,
                mediaUrls
        );

        return ResponseEntity.ok(sponsorRequestService.save(request));
    }

    @GetMapping
    public List<sponsor_request> getAll() {
        return sponsorRequestService.getAll();
    }
}
