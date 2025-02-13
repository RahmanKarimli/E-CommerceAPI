package com.example.ecommerceapi.Controllers;


import com.example.ecommerceapi.Exceptions.AuthenticationFailedException;
import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Cart;
import com.example.ecommerceapi.Models.Order;
import com.example.ecommerceapi.Models.Status;
import com.example.ecommerceapi.Repositories.AppUserRepo;
import com.example.ecommerceapi.Repositories.CartRepo;
import com.example.ecommerceapi.Repositories.OrderRepo;
import com.example.ecommerceapi.Services.CartService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final AppUserRepo appUserRepo;
    private final CartService cartService;
    private final CartRepo cartRepo;
    private final OrderRepo orderRepo;

    @GetMapping
    public ResponseEntity<List<Cart>> getAllProducts(Authentication authentication) {
        AppUser currentUser = getCurrentUser(authentication);

        return new ResponseEntity<>(cartService.findAllByUser(currentUser), HttpStatus.OK);
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalPrice(Authentication authentication) {
        AppUser currentUser = getCurrentUser(authentication);

        return new ResponseEntity<>(cartService.getTotalPrice(currentUser), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cart> addToCart(@RequestBody Cart newCart, Authentication authentication) {
        AppUser currentUser = getCurrentUser(authentication);

        newCart.setUser(currentUser);

        return new ResponseEntity<>(cartService.addProductToCart(newCart, currentUser), HttpStatus.CREATED);
    }

    @PostMapping("/checkout")
    public String checkout(Authentication authentication) {
        AppUser currentUser = getCurrentUser(authentication);
        double totalAmount = cartService.getTotalPrice(currentUser);
        List<Cart> userCarts = cartRepo.findCartsByUser(currentUser);

        Order newOrder = orderRepo.save(Order.builder()
                .status(Status.PENDING)
                .totalAmount(totalAmount)
                .createdAt(new Date())
                .user(currentUser)
                .build());


        currentUser.getOrders().add(newOrder);

        return "payment.html";
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<Cart> changeCart(@PathVariable long cartId, @RequestBody Cart newCart, Authentication authentication) throws BadRequestException {
        AppUser user = getCurrentUser(authentication);

        Optional<Cart> cart = cartRepo.findCartByIdAndUser(cartId, user);
        if (cart.isEmpty()) {
            throw new BadRequestException("Cart not found or unauthorized");
        }

        return new ResponseEntity<>(cartService.changeCart(cart, newCart, user), HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<HttpStatus> deleteCart(@PathVariable long cartId, Authentication authentication) throws BadRequestException {
        AppUser user = getCurrentUser(authentication);

        Optional<Cart> cart = cartRepo.findCartByIdAndUser(cartId, user);
        if (cart.isEmpty()) {
            throw new BadRequestException("Cart not found or unauthorized");
        }

        return cartService.deleteCart(cartId, user);
    }


    private AppUser getCurrentUser(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();

        return appUserRepo.findByUsername(user.getUsername()).orElseThrow(() -> new AuthenticationFailedException("User not found"));
    }
}
