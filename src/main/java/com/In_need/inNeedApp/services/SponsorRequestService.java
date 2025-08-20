package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.model.sponsor_request;
import com.In_need.inNeedApp.repository.sponsor_requestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SponsorRequestService {
    @Autowired
private sponsor_requestRepository sponsor_requestRepository;  // instance, not class

    public sponsor_request getSponsorRequestById(Long id) {
        return sponsor_requestRepository.findById(id).orElse(null); // ✅ correct
    }
    public sponsor_request saveSponsorRequest(sponsor_request request) {
        return sponsor_requestRepository.save(request);
    }

    public List<sponsor_request> getAll() {
        return sponsor_requestRepository.findAll();
    }



}


