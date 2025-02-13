package com.example.ecommerceapi.Services;


import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Models.Product;
import com.example.ecommerceapi.Repositories.AppUserRepo;
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
public class ProductService {
    private final ProductRepo productRepo;
    private final AppUserRepo appUserRepo;

    public List<Product> findAll() {
        return productRepo.findAll();
    }

    public Product saveProduct(Product product, AppUser user) {
        product.setCreatedAt(new Date());
        Product newProduct = productRepo.save(product);

        user.getProducts().add(newProduct);
        appUserRepo.save(user);

        return newProduct;
    }

    public Product changeProduct(Optional<Product> product, Product newProduct, AppUser user) throws BadRequestException {
        if (product.isEmpty()) {
            throw new BadRequestException("Product not found.");
        }

        Product existingProduct = product.get();
        existingProduct.setName(newProduct.getName());
        existingProduct.setDescription(newProduct.getDescription());
        existingProduct.setPrice(newProduct.getPrice());
        existingProduct.setInventoryCount(newProduct.getInventoryCount());
        Product updatedProduct = productRepo.save(existingProduct);

        user.getProducts().removeIf(p -> p.getId() == updatedProduct.getId());
        user.getProducts().add(updatedProduct);
        appUserRepo.save(user);

        return updatedProduct;
    }

    public ResponseEntity<HttpStatus> deleteProduct(long id, AppUser user) {
        Product deletedProduct = productRepo.findById(id).get();
        productRepo.deleteById(id);

        user.getProducts().removeIf(p -> p.getId() == deletedProduct.getId());
        appUserRepo.save(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
