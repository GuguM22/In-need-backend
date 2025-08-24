package com.In_need.inNeedApp.repository;

import com.In_need.inNeedApp.constant.DonationStatus;
import com.In_need.inNeedApp.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorEmailIgnoreCase(String donorEmail);
    List<Donation> findByStatus(DonationStatus status);
}