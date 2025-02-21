package com.example.ecommerceapi.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(unique = true)
    private String stripePaymentId;
    private double totalAmount;
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
