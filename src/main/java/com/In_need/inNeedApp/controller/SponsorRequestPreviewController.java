package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.model.sponsor_request;  // <-- use the actual class name

import com.In_need.inNeedApp.services.SponsorRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sponsor-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class SponsorRequestPreviewController {

    @Autowired
    private SponsorRequestService SponsorRequestService;

    // GET method to fetch sponsor request by ID for preview
    @GetMapping("/{id}")
    public ResponseEntity<sponsor_request> getSponsorRequest(@PathVariable Long id) {
        
        sponsor_request request = SponsorRequestService.getSponsorRequestById(id);
        if (request != null) {
            return ResponseEntity.ok(request);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
