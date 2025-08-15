package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.model.individual_request;
import com.In_need.inNeedApp.services.IndividualService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/individual-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class IndividualreqController {

        private final IndividualService individualRequestService;
    private final Path uploadDir = Paths.get("uploads/individual");

    public IndividualreqController(IndividualService individualRequestService) {
        this.individualRequestService = individualRequestService;

        try {
            Files.createDirectories(uploadDir);
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @PostMapping(consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<individual_request> createRequest(@RequestBody individual_request request, List<MultipartFile> mediaFiles) {

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

            // You might want to set these URLs in the request object
            // request.setMediaUrls(mediaUrls);

            individual_request saved = individualRequestService.save(request);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<individual_request> getAll() {
        return individualRequestService.getAll();
    }
}
