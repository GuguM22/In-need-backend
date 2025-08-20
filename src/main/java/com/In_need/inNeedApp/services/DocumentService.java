package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Transactional(readOnly = true)
    public List<Documents> getDocumentsByVerificationId(Long verificationId) {
        // Make sure this method runs in a transaction to avoid "auto-commit mode" error on LOBs
        return documentRepository.findByVerificationId(verificationId);
    }

    @Transactional(readOnly = true)
    public Optional<Documents> findDocumentById(Long id) {
        return documentRepository.findById(id);
    }
}
