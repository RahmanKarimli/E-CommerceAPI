package com.example.ecommerceapi.Repositories;

import com.example.ecommerceapi.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Optional<Order> findByStripePaymentId(String stripePaymentId);
}
