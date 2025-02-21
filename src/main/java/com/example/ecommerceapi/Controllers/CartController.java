package com.example.ecommerceapi.Controllers;


import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Cart;
import com.example.ecommerceapi.Repositories.CartRepo;
import com.example.ecommerceapi.Services.AppUserService;
import com.example.ecommerceapi.Services.CartService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final AppUserService appUserService;
    private final CartService cartService;
    private final CartRepo cartRepo;

    @GetMapping
    public ResponseEntity<List<Cart>> getAllProducts(Authentication authentication) {
        AppUser currentUser = appUserService.getCurrentUser(authentication);

        return new ResponseEntity<>(cartService.findAllByUser(currentUser), HttpStatus.OK);
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalPrice(Authentication authentication) {
        AppUser currentUser = appUserService.getCurrentUser(authentication);

        return new ResponseEntity<>(cartService.getTotalPrice(currentUser), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cart> addToCart(@RequestBody Cart newCart, Authentication authentication) throws BadRequestException {
        AppUser currentUser = appUserService.getCurrentUser(authentication);

        newCart.setUser(currentUser);

        return new ResponseEntity<>(cartService.addProductToCart(newCart, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<Cart> changeCart(@PathVariable long cartId, @RequestBody Cart newCart, Authentication authentication) throws BadRequestException {
        AppUser currentUser = appUserService.getCurrentUser(authentication);

        Optional<Cart> cart = cartRepo.findByIdAndUser(cartId, currentUser);
        if (cart.isEmpty()) {
            throw new BadRequestException("Cart not found or unauthorized");
        }

        return new ResponseEntity<>(cartService.changeCart(cart, newCart), HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Cart> deleteCart(@PathVariable long cartId, Authentication authentication) throws BadRequestException {
        AppUser currentUser = appUserService.getCurrentUser(authentication);

        Optional<Cart> cart = cartRepo.findByIdAndUser(cartId, currentUser);
        if (cart.isEmpty()) {
            throw new BadRequestException("Cart not found or unauthorized");
        }

        return new ResponseEntity<>(cartService.deleteCart(cartId), HttpStatus.NO_CONTENT);
    }
}
