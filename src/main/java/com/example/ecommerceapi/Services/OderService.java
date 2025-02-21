package com.example.ecommerceapi.Services;


import com.example.ecommerceapi.Models.*;
import com.example.ecommerceapi.Repositories.OrderRepo;
import com.example.ecommerceapi.Repositories.ProductRepo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final CartService cartService;
    private final AppUserService appUserService;
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public PaymentIntent createPaymentIntent(AppUser user) throws StripeException {
        double cartTotal = cartService.getTotalPrice(user);


        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (cartTotal * 100))
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Order order = Order.builder()
                .user(user)
                .status(Status.PENDING)
                .totalAmount(cartTotal)
                .stripePaymentId(paymentIntent.getId())
                .build();

        orderRepo.save(order);

        return paymentIntent;
    }

    @Transactional
    public void handleSuccessfulPayment(String id) throws BadRequestException {
        Order order = orderRepo.findByStripePaymentId(id).orElseThrow(() -> new BadRequestException("Payment id not found"));

        AppUser user = appUserService.loadAppUserById(order.getUser().getId());
        for (Cart cart : cartService.findAllByUser(user)) {
            Product product = productRepo.findById(cart.getProductId()).orElseThrow(() -> new BadRequestException("Product not found"));
            product.setInventoryCount(product.getInventoryCount() - cart.getQuantity());
            productRepo.save(product);
        }

        order.setStatus(Status.PAID);
        orderRepo.save(order);

        cartService.clearCart(user);
    }

    public void handleFailedPayment(String id) throws BadRequestException {
        Order order = orderRepo.findByStripePaymentId(id).orElseThrow(() -> new BadRequestException("Payment id not found"));
        order.setStatus(Status.CANCELLED);
        orderRepo.save(order);
    }
}
