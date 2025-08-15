package com.In_need.inNeedApp.repository;

import com.In_need.inNeedApp.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findByPhoneNumber(String phoneNumber);

    Optional<Verification> findByWebsite(String website);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT v FROM Verification v LEFT JOIN FETCH v.documents WHERE v.id = :id")
    Optional<Verification> findByIdWithDocuments(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
            "FROM Verification v " +
            "WHERE SUBSTRING(v.phoneNumber, LENGTH(v.phoneNumber) - 9, 10) = :lastTenDigits")
    boolean existsByLastTenDigits(@Param("lastTenDigits") String lastTenDigits);


    Optional<Verification> findByUserId(Long userId);
}

