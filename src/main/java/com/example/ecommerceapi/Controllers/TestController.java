package com.example.ecommerceapi.Controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public ResponseEntity<String> hi() {
        return ResponseEntity.ok("Hello, World!");
    }
}
