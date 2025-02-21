package com.example.ecommerceapi.Repositories;

import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {
    Optional<Product> findProductByIdAndUser(long id, AppUser user);
}
