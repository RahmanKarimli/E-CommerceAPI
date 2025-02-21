package com.example.ecommerceapi.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long productId;
    private int quantity;
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private AppUser user;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
