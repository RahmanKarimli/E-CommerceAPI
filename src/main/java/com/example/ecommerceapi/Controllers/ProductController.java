package com.example.ecommerceapi.Controllers;


import com.example.ecommerceapi.Models.ProductPageDTO;
import com.example.ecommerceapi.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping({"", "/"})
    public ResponseEntity<ProductPageDTO> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int limit, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = Sort.by(sortBy).ascending();
        if (!ascending) {
            sort = Sort.by(sortBy).descending();
        }

        Pageable pageable = PageRequest.of(page, limit, sort);

        ProductPageDTO allProducts = productService.findAll(pageable);
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }
}
