package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.dto.UserInfoDTO;
import com.In_need.inNeedApp.model.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser(@AuthenticationPrincipal Users user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserInfoDTO dto = new UserInfoDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getVerified() != null && user.getVerified()        );

        return ResponseEntity.ok(dto);
    }
}
