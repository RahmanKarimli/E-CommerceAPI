package com.example.ecommerceapi.Services;


import com.example.ecommerceapi.Exceptions.AuthenticationFailedException;
import com.example.ecommerceapi.Models.*;
import com.example.ecommerceapi.Repositories.AppUserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepo appUserRepository;
    private final JwtUtil jwtUtil;


    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (appUserRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new AuthenticationFailedException("User already exists");
        }
        if (appUserRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new AuthenticationFailedException("Email already in use");
        }

        AppUser newUser = AppUser.builder()
                .firstname(registerRequest.getFirstname())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .address(registerRequest.getAddress())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.CLIENT)
                .createdAt(new Date())
                .build();

        appUserRepository.save(newUser);
        String jwtToken = jwtUtil.generateToken(newUser);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        String email = authenticationRequest.getEmail();

        if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())) {
            throw new AuthenticationFailedException("Username or email must be provided");
        }

        AppUser user;
        if (!(username == null || username.isEmpty())) {
            user = appUserRepository.findByUsername(username).orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));
        } else {
            user = appUserRepository.findByEmail(email).orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));
        }

        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationFailedException("Invalid username or password");
        }

        String jwtToken = jwtUtil.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}