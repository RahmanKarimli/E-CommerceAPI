package com.example.ecommerceapi.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductPageDTO {
    private List<Product> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
