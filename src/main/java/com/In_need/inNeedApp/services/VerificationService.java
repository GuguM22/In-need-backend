package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.constant.Status;
import com.In_need.inNeedApp.dto.VerificationResponse;
import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.Verification;
import com.In_need.inNeedApp.repository.UserRepository;
import com.In_need.inNeedApp.repository.VerificationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository  verificationRepository;
    private final DocumentService documentService;
    private final UserRepository usersRepository;


    public Verification saveVerification(Verification verification ) {
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

        return VerificationResponse.builder()
                .id(verification.getId())
                .phone(verification.getPhoneNumber())
                .website(verification.getWebsite())
                .status(verification.getStatus())
                .documents(documentFileNames)
                .email(verification.getEmail())
                .userId(verification.getUser() != null ? verification.getUser().getId() : null)
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


}
