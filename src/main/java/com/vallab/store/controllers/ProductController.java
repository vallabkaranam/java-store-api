package com.vallab.store.controllers;

import com.vallab.store.dtos.ProductDto;
import com.vallab.store.entities.Product;
import com.vallab.store.mappers.ProductMapper;
import com.vallab.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping
    public List<ProductDto> getAllProducts(@RequestParam(required = false, name = "categoryId") Byte categoryId) {
        List<Product> products;
        
        if (categoryId == null) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByCategoryId(categoryId);
        }

        return products
            .stream()
            .map(productMapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(product));
    }
}