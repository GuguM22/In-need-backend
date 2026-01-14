package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.constant.Status;
import com.In_need.inNeedApp.dto.VerificationRequest;
import com.In_need.inNeedApp.dto.VerificationResponse;
import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.Verification;
import com.In_need.inNeedApp.repository.DocumentRepository;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.repository.VerificationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository  verificationRepository;
    private final DocumentService documentService;
    private final UserRepository usersRepository;
     private final DocumentRepository documentRepository;

    public Verification saveVerification(Verification verification ) {
        verification.setSubmittedDate(LocalDateTime.now());
        return verificationRepository.save(verification);
    }

    public Optional<Verification> getVerificationByPhone(String phone) {
        return verificationRepository.findByPhoneNumber(phone);
    }

    public boolean phoneExists(String phone) {
        return verificationRepository.existsByPhoneNumber(phone);
    }

    //public List<Verification> searchByWebsite(String keyword) {
       // return verificationRepository.findByWebsiteContainingIgnoreCase(keyword);
    //}

    public VerificationResponse mapToDto(Verification verification) {
        List<String> documentFileNames = verification.getDocuments()
                .stream()
                .map(Documents::getFileName)
                .collect(Collectors.toList());

        String username = verification.getUser() != null ? verification.getUser().getUsername() : null;

        return VerificationResponse.builder()
                .id(verification.getId())
                .phone(verification.getPhoneNumber())
                .website(verification.getWebsite())
                .status(verification.getStatus())
                .documents(documentFileNames)
                .email(verification.getEmail())
                .userId(verification.getUser() != null ? verification.getUser().getId() : null)
                .username(username)
                .submittedDate(verification.getSubmittedDate())
                .build();
    }


    @Transactional(readOnly = true)
    public List<VerificationResponse> getAllByStatus(Status status) {
        return verificationRepository.findAllByStatus(status)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    // Get all pending verifications
    @Transactional(readOnly = true)
    public List<VerificationResponse> getAllPending() {
        return getAllByStatus(Status.PENDING);
    }

    @Transactional(readOnly = true)
    public List<VerificationResponse> getAll() {
        return verificationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

//    @Transactional
//    public void updateVerificationStatus(Long verificationId, Status newStatus) {
//        Optional<Verification> optionalVerification = verificationRepository.findById(verificationId);
//
//        if (optionalVerification.isEmpty()) {
//            throw new IllegalArgumentException("Verification not found with ID: " + verificationId);
//        }
//
//        Verification verification = optionalVerification.get();
//        verification.setStatus(newStatus);
//        verificationRepository.save(verification);
//
//        // ✅ If approved, mark user as verified
//        if (newStatus == Status.APPROVED && verification.getUser() != null) {
//            Users user = verification.getUser();
//            user.setVerified(true);
//            usersRepository.save(user);
//        }
//    }

    @Transactional
    public void updateVerificationStatus(Long verificationId, Status newStatus) {
        Verification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("Verification not found"));

        verification.setStatus(newStatus);
        verificationRepository.save(verification);

        Users user = verification.getUser();
        if (user != null) {
            if (newStatus == Status.APPROVED) {
                user.setVerified(true);
            } else if (newStatus == Status.REJECTED) {
                user.setVerified(false);
            }
            usersRepository.save(user);
        }
    }

     @Transactional
    public Verification createVerification(VerificationRequest request, String userEmail) {
        // Find the user by email
        Users user = usersRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Create and populate verification entity
        Verification verification = new Verification();
        verification.setId(request.getUserId());
        verification.setEmail(userEmail);
        verification.setPhoneNumber(request.getPhone());
        verification.setWebsite(request.getWebsite());
        verification.setUser(user);
        verification.setStatus(Status.PENDING);

        // Handle documents if provided
        if (request.getDocuments() != null && !request.getDocuments().isEmpty()) {
            List<Documents> documents = request.getDocuments().stream()
                    .map(docRequest -> {
                        Documents document = new Documents();
                        document.setId(document.getId());
                        document.setFileName(document.getFileName());
                        document.setData(document.getData());
                        document.setVerification(document.getVerification());
                        document.setVerification(verification);
                        return document;
                    })
                    .collect(Collectors.toList());
            verification.setDocuments(documents);
        }

        return verificationRepository.save(verification);
    }
     public Optional<Documents> findDocumentById(Long id) {
        return documentService.findDocumentById(id);
    }

 

}
