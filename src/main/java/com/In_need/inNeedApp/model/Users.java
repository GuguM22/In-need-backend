package com.In_need.inNeedApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.In_need.inNeedApp.constant.Role;

import java.util.Objects;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "bio")
    private String bio;
    @Column(name = "profile")
    private String profileImageUrl;

    // Optional: If you have a location object
    @Embedded
    private Location location;

//    @Column(name = "verification_token")
//    private String verificationToken;

   /* @Column(name = "verified")
    private boolean verified = false; */

    @Column(name = "reset_token")
    private String resetToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private  Role role;

    @Column(name = "verified")
    private Boolean verified = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users user = (Users) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", email='" + email + " " +
        ", name='" + username + " " +
        ", verified=" + //verified* +
                ", role=" + role +
                '}';
    }
}