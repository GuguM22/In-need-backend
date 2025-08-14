package com.In_need.inNeedApp.repository;

import com.In_need.inNeedApp.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users>  findByEmailIgnoreCase(String email);
//    Optional<Users> findByVerificationToken(String token);
    Optional<Users> findByResetToken(String resetToken);
}
