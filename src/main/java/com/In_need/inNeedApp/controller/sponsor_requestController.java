package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.dto.sponsorRequest;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.sponsor_request;
import com.In_need.inNeedApp.services.CustomUserDetailsService;
import com.In_need.inNeedApp.services.SponsorRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final CustomUserDetailsService userService;  // Add this

    public sponsor_requestController(SponsorRequestService sponsorRequestService, CustomUserDetailsService userService) {
        this.sponsorRequestService = sponsorRequestService;
        this.userService = userService;

        try {
            Files.createDirectories(uploadDir);
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

//    @PostMapping(consumes = {"multipart/form-data", "application/json"})
//    public ResponseEntity<sponsor_request> createRequest(@RequestBody sponsor_request request, List<MultipartFile> mediaFiles) {
//
//        List<String> mediaUrls = new ArrayList<>();
//
//        try {
//            if (mediaFiles != null) {
//                for (MultipartFile file : mediaFiles) {
//                    String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//                    Path filePath = uploadDir.resolve(filename);
//                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//                    mediaUrls.add(filePath.toString());
//                }
//            }
//
//
//            request.setMediaUrls(mediaUrls); // assuming your model has this field
//
//            // Save using your service
//            sponsor_request savedRequest = sponsorRequestService.saveSponsorRequest(request);
//
//            return ResponseEntity.ok(savedRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//    }

//    @PostMapping(consumes = {"multipart/form-data", "application/json"})
//    public ResponseEntity<sponsor_request> createRequest(
//            @RequestBody sponsor_request request,
//            List<MultipartFile> mediaFiles,
//            @AuthenticationPrincipal UserDetails userDetails // get logged-in user details
//    ) {
//        List<String> mediaUrls = new ArrayList<>();
//
//        try {
//            if (mediaFiles != null) {
//                for (MultipartFile file : mediaFiles) {
//                    String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//                    Path filePath = uploadDir.resolve(filename);
//                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//                    mediaUrls.add(filePath.toString());
//                }
//            }
//
//            request.setMediaUrls(mediaUrls);
//
//            // Fetch Users entity by username or email from userDetails
//            Users user = userService.findByEmail(userDetails.getUsername()); // you need this method
//
//            request.setUser(user);
//
//            sponsor_request savedRequest = sponsorRequestService.saveSponsorRequest(request);
//
//            return ResponseEntity.ok(savedRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PostMapping(consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<sponsor_request> createRequest(
            @RequestBody sponsor_request request,
            List<MultipartFile> mediaFiles
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // DEBUG: Check what's going on
        System.out.println("Authentication = " + authentication);

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        String email = authentication.getName(); // Usually the email/username
        Users user = userService.findByEmail(email); // Make sure this works

        // ... process mediaFiles etc.
        request.setUser(user);
        sponsor_request savedRequest = sponsorRequestService.saveSponsorRequest(request);
        return ResponseEntity.ok(savedRequest);
    }
    @GetMapping
    public List<sponsor_request> getAll() {
        return sponsorRequestService.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<sponsor_request> updateRequest(
            @PathVariable Long id,
            @ModelAttribute sponsorRequest request,
            @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) {

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

            request.setMediaUrls(mediaUrls);

            sponsor_request updatedRequest = sponsorRequestService.updateSponsorRequest(id, request);

            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/my-posts")
    public ResponseEntity<List<sponsor_request>> getMyRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        Users user = userService.findByEmail(email); // Fetch logged-in user

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<sponsor_request> requests = sponsorRequestService.getByUser(user);

        return ResponseEntity.ok(requests);
    }

   /* @PutMapping("/{id}/fulfill")
    public ResponseEntity<sponsor_request> markFulfilled(@PathVariable Long id) {
        sponsor_request req = sponsorRequestService.getById(id);
        if (req == null) {
            return ResponseEntity.notFound().build();
        }

        req.setFulfilled(true);
        sponsor_request updated = sponsorRequestService.saveSponsorRequest(req);

        return ResponseEntity.ok(updated);
    }*/

    @PutMapping("/{id}/fulfill")
    public ResponseEntity<sponsor_request> markFulfilled(@PathVariable Long id) {
        sponsor_request req = sponsorRequestService.getById(id);
        if (req == null) {
            return ResponseEntity.notFound().build();
        }

        req.setFulfilled(true);
        sponsor_request updated = sponsorRequestService.saveSponsorRequest(req);

        return ResponseEntity.ok(updated);
    }

}