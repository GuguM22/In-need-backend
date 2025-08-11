package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.model.sponsor_request;
import com.In_need.inNeedApp.repository.sponsor_requestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SponsorRequestService {private final sponsor_requestRepository repository;

    public SponsorRequestService(sponsor_requestRepository repository) {
        this.repository = repository;
    }

    public sponsor_request save(sponsor_request request) {
        return repository.save(request);
    }

    public List<sponsor_request> getAll() {
        return repository.findAll();
    }

    public sponsor_request getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
    }
}


