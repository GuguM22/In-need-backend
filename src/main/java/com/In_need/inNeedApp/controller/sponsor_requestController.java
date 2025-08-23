package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.dto.sponsorRequest;
import com.In_need.inNeedApp.model.sponsor_request;
import com.In_need.inNeedApp.services.SponsorRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @PostMapping(consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<sponsor_request> createRequest(@RequestBody sponsor_request request, List<MultipartFile> mediaFiles) {

        List<String> mediaUrls = new ArrayList<>();

        try {
            if (mediaFiles != null) {
                for (MultipartFile file : mediaFiles) {
                    String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = uploadDir.resolve(filename);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    mediaUrls.add(filePath.toString());
                }
            }



            request.setMediaUrls(mediaUrls); // assuming your model has this field

            // Save using your service
            sponsor_request savedRequest = sponsorRequestService.saveSponsorRequest(request);

            return ResponseEntity.ok(savedRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<sponsor_request> getAll() {
        return sponsorRequestService.getAll();
    }
}