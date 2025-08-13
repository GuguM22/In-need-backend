package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.model.Verification;
import com.In_need.inNeedApp.repository.VerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository  verificationRepository;

    public Verification saveVerification(Verification verification) {
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

}
