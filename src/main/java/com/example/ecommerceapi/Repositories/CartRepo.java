package com.example.ecommerceapi.Repositories;

import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {
    List<Cart> findCartsByUser(AppUser user);

    Optional<Cart> findByProductIdAndUser(long id, AppUser user);

    Optional<Cart> findByIdAndUser(long id, AppUser user);

    void deleteCartsByUser(AppUser user);
}
