package com.In_need.inNeedApp.repository;

import com.In_need.inNeedApp.constant.Status;
import com.In_need.inNeedApp.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    // Find by phone number
    Optional<Verification> findByPhoneNumber(String phoneNumber);

    // Find by website
    Optional<Verification> findByWebsite(String website);

    // Check if phone exists
    boolean existsByPhoneNumber(String phoneNumber);

    List<Verification> findByStatus(Status status);
    List<Verification> findAllByStatus(Status status);

    // Search all by partial website match
    //List<Verification> findByWebsiteContainingIgnoreCase(String keyword);

    @Query("SELECT v FROM Verification v LEFT JOIN FETCH v.documents WHERE v.id = :id")
    Optional<Verification> findByIdWithDocuments(@Param("id") Long id);

    Optional<Verification> findByUserId(Long id);
}
