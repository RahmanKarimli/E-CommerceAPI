package com.example.ecommerceapi.Controllers;


import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Services.AppUserService;
import com.example.ecommerceapi.Services.OderService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RequiredArgsConstructor
@RequestMapping("/api/payment")
@RestController
@Slf4j
public class OrderController {
    private final AppUserService appUserService;
    private final OderService oderService;

    @PostMapping("/create-payment-intent")
    private ResponseEntity<?> createPaymentIntent(Authentication authentication) {
        try {
            AppUser currentUser = appUserService.getCurrentUser(authentication);
            PaymentIntent paymentIntent = oderService.createPaymentIntent(currentUser);

            return ResponseEntity.ok(Map.of(
                    "clientSecret", paymentIntent.getClientSecret(),
                    "amount", paymentIntent.getAmount()
            ));
        } catch (StripeException e) {
            log.error("Payment failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed: " + e.getMessage());
        }
    }
}
