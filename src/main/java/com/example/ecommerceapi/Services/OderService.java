package com.example.ecommerceapi.Services;


import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Order;
import com.example.ecommerceapi.Models.Status;
import com.example.ecommerceapi.Repositories.OrderRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OderService {
    @Value("${stripe.api.key}")
//    api ket is not used why?
    private String stripeApiKey;

    private final OrderRepo orderRepo;
    private final CartService cartService;

    @Transactional
    public PaymentIntent createPaymentIntent(AppUser user) throws StripeException {
        double cartTotal = cartService.getTotalPrice(user);

        Order order = Order.builder()
                .user(user)
                .status(Status.PENDING)
                .totalAmount(cartTotal)
                .build();
        orderRepo.save(order);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (cartTotal * 100))
                .setCurrency("usd")
//                I didn't understand anything from this part
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
//                What is metadata
                .putMetadata("orderId", String.valueOf(order.getId()))
                .build();
//      Doesn't this have to return paymentintent id? I mean how we will know
//      paymentintentid so we can pass to confirmPayment function
        return PaymentIntent.create(params);
    }


    @Transactional
    public Order confirmPayment(String paymentIntentId, AppUser user) throws StripeException, BadRequestException {
//        How does this get intentid from a class but now an object?
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        String orderId = paymentIntent.getMetadata().get("orderId");

        Order order = orderRepo.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new BadRequestException("Order not Found"));

        if ("succeeded".equals(paymentIntent.getStatus())) {
            order.setStatus(Status.PAID);
            order.setStripePaymentId(paymentIntentId);
            orderRepo.save(order);

            // Clear cart after successful payment
            cartService.clearCart(user);
        } else {
            order.setStatus(Status.FAILED);
            orderRepo.save(order);
        }

        return order;
    }
}


















