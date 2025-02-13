package com.example.ecommerceapi.Services;


import com.example.ecommerceapi.Exceptions.InsufficientInventoryException;
import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Cart;
import com.example.ecommerceapi.Repositories.AppUserRepo;
import com.example.ecommerceapi.Repositories.CartRepo;
import com.example.ecommerceapi.Repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepo cartRepo;
    private final AppUserRepo appUserRepo;
    private final ProductRepo productRepo;

    public List<Cart> findAllByUser(AppUser user) {
        return cartRepo.findCartsByUser(user);
    }

    public Double getTotalPrice(AppUser currentUser) {
        double totalPrice = 0;

        for (Cart cart : currentUser.getCarts()) {
            totalPrice += productRepo.findById(cart.getProductId()).get().getPrice();
        }

        return totalPrice;
    }

    public Cart addProductToCart(Cart newCart, AppUser user) {
        Optional<Cart> existingCart = cartRepo.findCartByIdAndUser(newCart.getProductId(), user);
        int productInventoryCount = productRepo.findById(newCart.getProductId()).get().getInventoryCount();

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();

            if ((cart.getQuantity() + newCart.getQuantity()) > productInventoryCount) {
                throw new InsufficientInventoryException("Insufficient inventory for the requested quantity.");
            }

            cart.setQuantity(cart.getQuantity() + newCart.getQuantity());
            Cart updatedCart = cartRepo.save(cart);

            user.getCarts().removeIf(c -> c.getId() == updatedCart.getId());
            user.getCarts().add(updatedCart);
            appUserRepo.save(user);

            return updatedCart;
        }
        if (newCart.getQuantity() > productInventoryCount) {
            throw new InsufficientInventoryException("Insufficient inventory for the requested quantity.");
        }

        newCart.setCreatedAt(new Date());
        Cart savedCart = cartRepo.save(newCart);

        user.getCarts().add(savedCart);
        return savedCart;
    }

    public Cart changeCart(Optional<Cart> cart, Cart newCart, AppUser user) throws BadRequestException {
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
        Cart updatedCart = cartRepo.save(existingCart);

        user.getCarts().removeIf(c -> c.getId() == updatedCart.getId());
        user.getCarts().add(updatedCart);
        appUserRepo.save(user);

        return updatedCart;
    }

    public ResponseEntity<HttpStatus> deleteCart(long cartId, AppUser user) {
        Cart deletedCart = cartRepo.findById(cartId).get();
        cartRepo.deleteById(cartId);

        user.getCarts().removeIf(c -> c.getId() == deletedCart.getId());
        appUserRepo.save(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
