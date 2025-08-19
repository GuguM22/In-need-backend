package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;

    public void updateUserProfileImage(String username, String filename) {
        Users user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old image if exists
        if (user.getProfileImagePath() != null) {
            try {
                Files.deleteIfExists(Paths.get("uploads").resolve(user.getProfileImagePath()));
            } catch (IOException e) {
                System.err.println("Failed to delete old profile image: " + e.getMessage());
            }
        }

        user.setProfileImagePath(filename); // store only filename
        userRepository.save(user);
    }
}
