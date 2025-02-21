package com.example.ecommerceapi.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String username;
    private String email;
    private String password;
}
