package com.example.ecommerceapi.Services;


import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Product;
import com.example.ecommerceapi.Models.ProductPageDTO;
import com.example.ecommerceapi.Repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepo productRepo;

    @Cacheable("productPages")
    public ProductPageDTO findAll(Pageable pageable) {
        log.warn("Fetching data from DB...");
        Page<Product> page = productRepo.findAll(pageable);

        return new ProductPageDTO(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    @CachePut(value = "productData", key = "#result.id")
    @CacheEvict(value = "productPages", allEntries = true)
    public Product saveProduct(Product product, AppUser user) {
        product.setUser(user);
        return productRepo.save(product);
    }

    @Transactional
    @CachePut(value = "productData", key = "#result.id")
    @CacheEvict(value = "productPages", allEntries = true)
    public Product changeProduct(Optional<Product> product, Product newProduct) throws BadRequestException {
        if (product.isEmpty()) {
            throw new BadRequestException("Product not found.");
        }

        Product existingProduct = product.get();
        existingProduct.setName(newProduct.getName());
        existingProduct.setDescription(newProduct.getDescription());
        existingProduct.setPrice(newProduct.getPrice());
        existingProduct.setInventoryCount(newProduct.getInventoryCount());

        return productRepo.save(existingProduct);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "productData", key = "#productId"),
                    @CacheEvict(value = "productPages", allEntries = true)
            }
    )
    public ResponseEntity<HttpStatus> deleteProduct(long productId) {
        productRepo.deleteById(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
