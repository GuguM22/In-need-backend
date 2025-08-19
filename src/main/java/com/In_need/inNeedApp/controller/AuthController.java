package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.dto.*;
import com.In_need.inNeedApp.model.Location;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.repository.VerificationRepository;
import com.In_need.inNeedApp.services.EmailService;
import com.In_need.inNeedApp.services.TokenBlacklistService;
import com.In_need.inNeedApp.services.UserProfileService;
import com.In_need.inNeedApp.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;
    private final TokenBlacklistService blacklistService;
    private  final EmailService emailService;
    private  final VerificationRepository verificationRepository;
    private final UserProfileService  userProfileService;

    private static final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/gif");

    @Autowired
    public  AuthController(AuthenticationManager authenticationManager,
                           JwtUtils jwtUtil,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           EmailService emailService,
                           TokenBlacklistService blacklistService,
                           VerificationRepository verificationRepository,
                           UserProfileService  userProfileService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.blacklistService = blacklistService;
        this.verificationRepository = verificationRepository;
        this.userProfileService = userProfileService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Validate required fields
        if (request.getRole() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Role must not be empty"));
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must not be empty"));
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }

        if (userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username must not be empty"));
        }

        // Map DTO to Entity
        Users user = new Users();
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
//        user.setVerified(false);
//        user.setVerificationToken(UUID.randomUUID().toString());

        Users savedUser = userRepository.save(user);

//        String encodedToken = URLEncoder.encode(savedUser.getVerificationToken(), StandardCharsets.UTF_8);
       // String link = "http://10.100.3.53:5050/auth/verify?token=" + encodedToken;
       // emailService.sendVerificationEmail(savedUser.getEmail(), link);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInRequest loginRequest) {
        try {
            // Normalize email to lowercase
            String email = loginRequest.getEmail().toLowerCase();

            // Check if user exists and is verified
            Optional<Users> optionalUser = userRepository.findByEmailIgnoreCase(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password"));
            }

            Users user = optionalUser.get();
//            if (!user.isVerified()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(Map.of("error", "Please verify your email before logging in"));
//            }

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(authentication.getName(), authentication.getAuthorities());


            return ResponseEntity.ok(new UserLoginResponse(token, user.getRole().name(), user.getEmail(), Boolean.TRUE.equals(user.getVerified()), user.getId()));



        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

   /* @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        String decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8);
        System.out.println("Received token: " + token);
        System.out.println("Decoded token: " + decodedToken);

        Optional<Users> optionalUser = userRepository.findByVerificationToken(decodedToken);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();

            if (user.isVerified()) {
                return ResponseEntity.ok(Map.of("message", "Email is already verified"));
            }

            user.setVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or expired token"));
        }

    }*/

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Optional<Users> userOpt = userRepository.findByEmailIgnoreCase(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
        }

        Users user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String resetLink = "http://localhost:4200/auth/reset-password?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), resetLink); // Reuse email sender

        return ResponseEntity.ok(Map.of("message", "Reset link sent to your email"));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<Users> userOpt = userRepository.findByResetToken(request.getToken());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired reset token"));
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }

        Users user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            Date exp = jwtUtil.extractExpiration(token); // new method
            blacklistService.blacklist(token, exp.getTime());
        }
        return ResponseEntity.ok().body(Map.of("message", "Logout successful"));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        // 1. Authentication check
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // 2. Fetch user from database
        Users user = userRepository.findByEmailIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Build profile response
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", user.getUsername());   // or user.getName() depending on your entity
        profile.put("email", user.getEmail());
        profile.put("bio", user.getBio());
        profile.put("location", user.getLocation());

        // Include profileImagePath
        if (user.getProfileImagePath() != null) {
            profile.put("profileImagePath", user.getProfileImagePath());
        }

        // 4. Add phone number if verified
        verificationRepository.findByUserId(user.getId())
                .ifPresent(verification -> profile.put("phone", verification.getPhoneNumber()));

        return ResponseEntity.ok(profile);
    }


    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateRequest request, Principal principal) {
        Users user = userRepository.findByEmailIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getLocation() != null) {
            user.setLocation(new Location(
                    request.getLocation().getCity(),
                    request.getLocation().getProvince()
            ));
        }

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("profileImage") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);

            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File cannot be empty"));
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File size exceeds 2MB limit"));
            }

            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Only JPG, PNG, GIF, or WEBP images are allowed"));
            }

            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String fileExtension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // store only filename in DB
            userProfileService.updateUserProfileImage(username, uniqueFilename);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "success", true,
                            "message", "File uploaded successfully",
                            "filePath", "/auth/images/" + uniqueFilename, // frontend sees this
                            "contentType", contentType,
                            "size", file.getSize()
                    ));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "File processing error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }


    @GetMapping("/auth/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads").resolve(filename).normalize();
        System.out.println(filePath.toAbsolutePath());
        UrlResource resource = new UrlResource(filePath.toAbsolutePath().toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body((Resource) resource);
    }



}






