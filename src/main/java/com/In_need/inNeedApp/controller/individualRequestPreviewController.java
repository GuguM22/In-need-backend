package com.In_need.inNeedApp.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.In_need.inNeedApp.model.individual_request;
import com.In_need.inNeedApp.services.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/individual-requests/preview")
@CrossOrigin(origins = "http://localhost:4200")

public class individualRequestPreviewController {
    @Autowired
    private IndividualService individualService;

    @GetMapping("/{id}")
    public ResponseEntity<individual_request> getIndividualRequest(@PathVariable Long id) {
        individual_request request = individualService.getById(id);
        if (request != null) {
            return ResponseEntity.ok(request);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
