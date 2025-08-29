package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.dto.sponsorRequest;
import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.sponsor_request;
import com.In_need.inNeedApp.repository.sponsor_requestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SponsorRequestService {
    @Autowired
    private sponsor_requestRepository sponsor_requestRepository;  // instance, not class

    public sponsor_request getSponsorRequestById(Long id) {
        return sponsor_requestRepository.findById(id).orElse(null); // ✅ correct
    }
    public sponsor_request getById(Long id) {
        return sponsor_requestRepository.findById(id).orElse(null);
    }
    public sponsor_request saveSponsorRequest(sponsor_request request) {
        return sponsor_requestRepository.save(request);
    }

    public List<sponsor_request> getAll() {
        return sponsor_requestRepository.findAll();
    }



    public sponsor_request updateSponsorRequest(Long id, sponsorRequest dto) {
        Optional<sponsor_request> optional = sponsor_requestRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Sponsor request not found");
        }

        sponsor_request existing = optional.get();

        // update fields from DTO
        existing.setTitle(dto.getTitle());
        existing.setPriority(dto.getPriority());
        existing.setQuantity(dto.getQuantity());
        existing.setRequiredDate(dto.getRequiredDate());
        existing.setDescription(dto.getDescription());
        existing.setLocation(dto.getLocation());
        existing.setMediaUrls(dto.getMediaUrls()); // make sure DTO has mediaUrls

        return sponsor_requestRepository.save(existing);
    }

    public List<sponsor_request> getByUser(Users user) {
        return sponsor_requestRepository.findByUser(user);
    }


}
