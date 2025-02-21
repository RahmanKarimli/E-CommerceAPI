package com.example.ecommerceapi.Controllers;

import com.example.ecommerceapi.Services.OderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/webhook")
@Slf4j
public class StripeWebhookController {

    private final OderService oderService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping
    public ResponseEntity<String> handleWebhook(HttpServletRequest request,
                                                @RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) throws BadRequestException {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.error("Webhook signature verification failed.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } catch (Exception e) {
            log.error("Error parsing webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow(() -> (new BadRequestException("PaymentIntent not exists")));
                log.info("PaymentIntent succeeded: {}", paymentIntent.getId());
                try {
                    oderService.handleSuccessfulPayment(paymentIntent.getId());
                } catch (Exception e) {
                    log.error("Error handling successful payment for PaymentIntent {}: {}",
                            paymentIntent.getId(), e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
                }
                break;
            case "payment_intent.payment_failed":
                PaymentIntent failedPaymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow(() -> (new BadRequestException("PaymentIntent not exists")));
                log.info("PaymentIntent failed: {}", failedPaymentIntent.getId());
                try {
                    oderService.handleFailedPayment(failedPaymentIntent.getId());
                } catch (Exception e) {
                    log.error("Error handling failed payment for PaymentIntent {}: {}",
                            failedPaymentIntent.getId(), e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
                }
                break;
            default:
                log.warn("Unhandled event type: {}", event.getType());
        }
        return ResponseEntity.ok("");
    }
}
