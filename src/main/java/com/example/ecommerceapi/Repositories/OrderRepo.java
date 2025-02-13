package com.example.ecommerceapi.Repositories;

import com.example.ecommerceapi.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
