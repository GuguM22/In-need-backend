package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.dto.ForgotPasswordRequest;
import com.In_need.inNeedApp.dto.RegisterRequest;
import com.In_need.inNeedApp.dto.ResetPasswordRequest;
import com.In_need.inNeedApp.dto.SignInRequest;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.services.EmailService;
import com.In_need.inNeedApp.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost/4200")
@RestController
@RequestMapping("/auth") // All auth-related endpoints
public class AuthController {


    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;
    private  final EmailService emailService;

    @Autowired
    public  AuthController(AuthenticationManager authenticationManager,
                           JwtUtils jwtUtil,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
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

        // Map DTO to Entity
        Users user = new Users();
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getUsername() );
        user.setRole(request.getRole());
        user.setVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        Users savedUser = userRepository.save(user);

        String link = "http://localhost:7474/auth/verify?token=" + savedUser.getVerificationToken();
        emailService.sendVerificationEmail(savedUser.getEmail(), link);

        return ResponseEntity.ok(Map.of("message", "User registered successfully. Please verify your email."));
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
            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Please verify your email before logging in"));
            }

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(authentication.getName(), authentication.getAuthorities());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", authentication.getName()
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        Optional<Users> optionalUser = userRepository.findByVerificationToken(token);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

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

        String resetLink = "http://localhost:7474/auth/reset-password?token=" + token;
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

}






