package com.example.ecommerceapi.Controllers;


import com.example.ecommerceapi.Models.Product;
import com.example.ecommerceapi.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
//Filter elave elemek lazimdi
//pagination elave ele
public class ProductController {
    private final ProductService productService;

    @GetMapping({"", "/"})
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> allProducts = productService.findAll();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }
}
