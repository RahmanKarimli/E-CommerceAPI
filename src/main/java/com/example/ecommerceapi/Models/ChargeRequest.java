package com.example.ecommerceapi.Models;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChargeRequest {
    @NotNull
    private String paymentMethodId;
    @NotNull
    private String currency;
}
