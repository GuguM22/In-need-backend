package com.In_need.inNeedApp.services;

import com.In_need.inNeedApp.model.Users;
import com.In_need.inNeedApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;



@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
  public  CustomUserDetailsService(UserRepository userRepository){
      this.userRepository = userRepository;
  }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            Users user = userRepository. findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

//            if (!user.isVerified()) {
//                throw new RuntimeException("Please verify your email before logging in.");
//            }

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        }

    public Users findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
