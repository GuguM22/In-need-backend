package com.In_need.inNeedApp.repository;



import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.model.sponsor_request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface sponsor_requestRepository extends JpaRepository<sponsor_request, Long> {
    List<sponsor_request> findByUser(Users user);

}

