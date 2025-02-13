package com.example.ecommerceapi.Repositories;

import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByRole(Role role);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
}
