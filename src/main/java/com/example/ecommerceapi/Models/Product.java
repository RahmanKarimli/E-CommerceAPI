package com.example.ecommerceapi.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    private String description;
    private double price;
    private int inventoryCount;
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
