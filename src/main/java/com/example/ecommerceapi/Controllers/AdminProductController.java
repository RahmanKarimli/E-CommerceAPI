package com.example.ecommerceapi.Controllers;


import com.example.ecommerceapi.Exceptions.AuthenticationFailedException;
import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Product;
import com.example.ecommerceapi.Repositories.AppUserRepo;
import com.example.ecommerceapi.Repositories.ProductRepo;
import com.example.ecommerceapi.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private final ProductService productService;
    private final ProductRepo productRepo;
    private final AppUserRepo appUserRepo;

    @PostMapping("")
    private ResponseEntity<Product> addProduct(@RequestBody Product newProduct, Authentication authentication) {
        AppUser currentUser = getCurrentUser(authentication);

        newProduct.setUser(currentUser);

        return new ResponseEntity<>(productService.saveProduct(newProduct, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<Product> changeProduct(@PathVariable long id, @RequestBody Product newProduct, Authentication authentication) throws BadRequestException {
        AppUser currentUser = getCurrentUser(authentication);

        Optional<Product> product = productRepo.findProductByIdAndUser(id, currentUser);
        if (product.isEmpty()) {
            throw new BadRequestException("Product not found or unauthorized");
        }

        return new ResponseEntity<>(productService.changeProduct(product, newProduct, currentUser), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable long id, Authentication authentication) throws BadRequestException {
        AppUser currentUser = getCurrentUser(authentication);

        Optional<Product> product = productRepo.findProductByIdAndUser(id, currentUser);
        if (product.isEmpty()) {
            throw new BadRequestException("Product not found or unauthorized");
        }

        return productService.deleteProduct(id, currentUser);
    }


    private AppUser getCurrentUser(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();

        return appUserRepo.findByUsername(user.getUsername()).orElseThrow(() -> new AuthenticationFailedException("User not found"));
    }
}
