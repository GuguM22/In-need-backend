package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.constant.Status;
import com.In_need.inNeedApp.dto.VerificationRequest;
import com.In_need.inNeedApp.dto.VerificationResponse;
import com.In_need.inNeedApp.services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

// Controller
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private VerificationService verificationService;

    @GetMapping("/verifications")
    public ResponseEntity<List<VerificationResponse>> getAll(@RequestParam Optional<String> status) {
        List<VerificationResponse> list = status
                .map(s -> verificationService.getAllByStatus(Status.valueOf(s.toUpperCase())))
                .orElse(verificationService.getAllPending());
        return ResponseEntity.ok(list);
    }


}

