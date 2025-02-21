package com.example.ecommerceapi.Controllers;

import com.example.ecommerceapi.Models.AuthenticationRequest;
import com.example.ecommerceapi.Models.AuthenticationResponse;
import com.example.ecommerceapi.Models.RegisterRequest;
import com.example.ecommerceapi.Services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}
