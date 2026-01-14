package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.model.individual_request;
import com.In_need.inNeedApp.repository.IndividualRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndividualService {

    private IndividualRepository repository;

    @Autowired
    public void IndividualService(IndividualRepository repository) {
        this.repository = repository;
    }

    public individual_request save(individual_request request) {
        return repository.save(request);
    }

    public List<individual_request> getAll() {
        return repository.findAll();
    }

    public individual_request getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Individual request not found" + id));
    }

    public individual_request update(Long id, individual_request updatedRequest) {
        individual_request existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Individual request not found: " + id));

        // Update the fields
        existing.setTitle(updatedRequest.getTitle());
        existing.setUrgency(updatedRequest.getUrgency());
        existing.setQuantity(updatedRequest.getQuantity());
        existing.setNeededByDate(updatedRequest.getNeededByDate());
        existing.setDescription(updatedRequest.getDescription());
        existing.setMediaUrls(updatedRequest.getMediaUrls());

        // Save the updated entity
        return repository.save(existing); // <-- return statement added
    }

}