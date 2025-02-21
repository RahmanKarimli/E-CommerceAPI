package com.example.ecommerceapi.Controllers;

import com.example.ecommerceapi.Services.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CheckoutController {
    private final JwtUtil jwtUtil;

    @GetMapping("/checkout")
    public String checkout(@RequestParam String token, Model model) throws BadRequestException {
        if (jwtUtil.isTokenExpired(token)) {
            throw new BadRequestException("Token is not valid");
        }
        model.addAttribute("authToken", token);
        return "checkout";
    }
}
