package com.In_need.inNeedApp.repository;

import com.In_need.inNeedApp.model.individual_request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualRepository extends JpaRepository<individual_request, Long> {
}
