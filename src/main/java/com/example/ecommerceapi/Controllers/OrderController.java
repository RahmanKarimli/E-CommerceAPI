package com.example.ecommerceapi.Controllers;


import com.example.ecommerceapi.Exceptions.AuthenticationFailedException;
import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Order;
import com.example.ecommerceapi.Repositories.AppUserRepo;
import com.example.ecommerceapi.Services.OderService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RequiredArgsConstructor
@RequestMapping("/api/payment")
@RestController
@Slf4j
public class OrderController {
    private final AppUserRepo appUserRepo;
    private final OderService oderService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(Authentication authentication) {
        try {
            AppUser currentUser = getCurrentUser(authentication);
            PaymentIntent paymentIntent = oderService.createPaymentIntent(currentUser);

//            what is clientsecret?
            return ResponseEntity.ok(Map.of(
                    "clientSecret", paymentIntent.getClientSecret(),
                    "amount", paymentIntent.getAmount()
            ));
        } catch (StripeException e) {
            log.error("Payment failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed: " + e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
//    what does payload contain
    private ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> payload, Authentication authentication) {
        try {
            AppUser currentUser = getCurrentUser(authentication);
            String paymentIntentId = payload.get("paymentIntentId");

            Order order = oderService.confirmPayment(paymentIntentId, currentUser);

            return ResponseEntity.ok(Map.of(
                    "status", order.getStatus(),
                    "orderId", order.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed: " + e.getMessage());
        }
    }
//       Front endi nece yazmaliyam html nece olmalidi ve returnurl e ne yazmaliyam ve s.


    private AppUser getCurrentUser(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();

        return appUserRepo.findByUsername(user.getUsername()).orElseThrow(() -> new AuthenticationFailedException("User not found"));
    }
}
