package com.In_need.inNeedApp.repository;

import com.In_need.inNeedApp.model.Documents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Documents, Long> {
    List<Documents> findByVerificationId(Long verificationId);
    List<Documents> findByFileName(String fileName);

}
