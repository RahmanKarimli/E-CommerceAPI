package com.example.ecommerceapi.Controllers;


import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Product;
import com.example.ecommerceapi.Repositories.ProductRepo;
import com.example.ecommerceapi.Services.AppUserService;
import com.example.ecommerceapi.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;
    private final ProductRepo productRepo;
    private final AppUserService appUserService;

    @PostMapping("")
    private ResponseEntity<Product> addProduct(@RequestBody Product newProduct, Authentication authentication) {
        AppUser currentUser = appUserService.getCurrentUser(authentication);

        newProduct.setUser(currentUser);

        return new ResponseEntity<>(productService.saveProduct(newProduct, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<Product> changeProduct(@PathVariable long id, @RequestBody Product newProduct, Authentication authentication) throws BadRequestException {
        AppUser currentUser = appUserService.getCurrentUser(authentication);

        Optional<Product> product = productRepo.findProductByIdAndUser(id, currentUser);
        if (product.isEmpty()) {
            throw new BadRequestException("Product not found or unauthorized");
        }

        return new ResponseEntity<>(productService.changeProduct(product, newProduct), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable long id, Authentication authentication) throws BadRequestException {
        AppUser currentUser = appUserService.getCurrentUser(authentication);

        Optional<Product> product = productRepo.findProductByIdAndUser(id, currentUser);
        if (product.isEmpty()) {
            throw new BadRequestException("Product not found or unauthorized");
        }

        return productService.deleteProduct(id);
    }
}
