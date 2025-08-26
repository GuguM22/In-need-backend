package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.model.individual_request;
import com.In_need.inNeedApp.repository.IndividualRepository;
import com.In_need.inNeedApp.services.IndividualService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/individual-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class IndividualreqController {

    private final IndividualService individualService;
    private final IndividualRepository individualRepository;
    private final Path uploadDir = Paths.get("uploads/individual");

    public IndividualreqController(IndividualService individualService, IndividualRepository individualRepository) {
        this.individualService = individualService;
        this.individualRepository = individualRepository;

        try {
            Files.createDirectories(uploadDir);
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<individual_request> createRequest(
            @RequestParam("title") String title,
            @RequestParam(value = "urgency", required = false) String urgency,
            @RequestParam("quantity") int quantity,
            @RequestParam("neededByDate") String neededByDate,
            @RequestParam("description") String description,
            @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles
    ) {
        individual_request request = new individual_request();
        request.setTitle(title);
        request.setUrgency(urgency);
        request.setQuantity(quantity);
        request.setNeededByDate(LocalDate.parse(neededByDate));
        request.setDescription(description);

        List<String> mediaUrls = new ArrayList<>();
        if (mediaFiles != null) {
            for (MultipartFile file : mediaFiles) {
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(filename);
                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    mediaUrls.add(filePath.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        request.setMediaUrls(mediaUrls);

        individual_request saved = individualService.save(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<individual_request> getAll() {
        return individualRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<individual_request> getById(@PathVariable Long id) {
        individual_request req = individualService.getById(id);
        return ResponseEntity.ok(req);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<individual_request> updateRequest(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "urgency", required = false) String urgency,
            @RequestParam("quantity") int quantity,
            @RequestParam("neededByDate") String neededByDate,
            @RequestParam("description") String description,
            @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles
    ) {
        individual_request existing = individualService.getById(id);

        existing.setTitle(title);
        existing.setUrgency(urgency);
        existing.setQuantity(quantity);
        existing.setNeededByDate(LocalDate.parse(neededByDate));
        existing.setDescription(description);

        List<String> mediaUrls = existing.getMediaUrls() != null ? existing.getMediaUrls() : new ArrayList<>();
        if (mediaFiles != null) {
            for (MultipartFile file : mediaFiles) {
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(filename);
                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    mediaUrls.add("individual/" + filename);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        existing.setMediaUrls(mediaUrls);

        individual_request updated = individualService.save(existing);
        return ResponseEntity.ok(updated);
    }

}
