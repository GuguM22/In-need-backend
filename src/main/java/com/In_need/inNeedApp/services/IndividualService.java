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
    public void IndividualRequestService(IndividualRepository repository) {
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
                .orElseThrow(() -> new RuntimeException("Individual request not found"));
    }
}
