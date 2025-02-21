package com.example.ecommerceapi.Services;


import com.example.ecommerceapi.Exceptions.InsufficientInventoryException;
import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Cart;
import com.example.ecommerceapi.Repositories.CartRepo;
import com.example.ecommerceapi.Repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;

    public List<Cart> findAllByUser(AppUser user) {
        return cartRepo.findCartsByUser(user);
    }

    public Double getTotalPrice(AppUser currentUser) {
        double totalPrice = 0;

        for (Cart cart : currentUser.getCarts()) {
            totalPrice += productRepo.findById(cart.getProductId()).get().getPrice() * cart.getQuantity();
        }

        return Math.round(totalPrice * 100.0) / 100.0;
    }

    public Cart addProductToCart(Cart newCart, AppUser user) {
        Optional<Cart> existingCart = cartRepo.findByProductIdAndUser(newCart.getProductId(), user);
        int productInventoryCount = productRepo.findById(newCart.getProductId()).get().getInventoryCount();

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();

            if ((cart.getQuantity() + newCart.getQuantity()) > productInventoryCount) {
                throw new InsufficientInventoryException("Insufficient inventory for the requested quantity.");
            }

            cart.setQuantity(cart.getQuantity() + newCart.getQuantity());

            return cartRepo.save(cart);
        }
        if (newCart.getQuantity() > productInventoryCount) {
            throw new InsufficientInventoryException("Insufficient inventory for the requested quantity.");
        }

        newCart.setUser(user);

        return cartRepo.save(newCart);
    }

    public Cart changeCart(Optional<Cart> cart, Cart newCart) throws BadRequestException {
        if (cart.isEmpty()) {
            throw new BadRequestException("Cart not found.");
        }

        Cart existingCart = cart.get();
        int productInventoryCount = productRepo.findById(newCart.getProductId()).get().getInventoryCount();

        if (newCart.getQuantity() > productInventoryCount) {
            throw new InsufficientInventoryException("Insufficient inventory for the requested quantity.");
        }

        existingCart.setProductId(newCart.getProductId());
        existingCart.setQuantity(newCart.getQuantity());

        return cartRepo.save(existingCart);
    }

    public Cart deleteCart(long cartId) throws BadRequestException {
        Cart cart = cartRepo.findById(cartId).orElseThrow(() -> new BadRequestException("Cart not found"));
        cartRepo.deleteById(cartId);
        return cart;
    }

    @Transactional
    public void clearCart(AppUser user) {
        cartRepo.deleteCartsByUser(user);
    }
}
