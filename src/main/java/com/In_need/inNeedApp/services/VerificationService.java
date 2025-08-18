package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.constant.Status;
import com.In_need.inNeedApp.dto.VerificationResponse;
import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.model.Verification;
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


}
