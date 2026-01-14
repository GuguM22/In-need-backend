package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.dto.UserInfoDTO;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyUser(@PathVariable Long id) {
        Optional<Users> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
