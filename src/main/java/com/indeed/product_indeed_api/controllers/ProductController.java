package com.indeed.product_indeed_api.controllers;

import com.indeed.product_indeed_api.models.Product;
import com.indeed.product_indeed_api.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/v1/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    @Autowired
    private ProductController(ProductService productService) {
        Assert.notNull(productService, "productService must not be null");
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProdcuts() {
        return ResponseEntity.ok(productService.getAllProdcuts());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        final Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping("/filter/price/{initialRange}/{finalRange}")
    public ResponseEntity<List<Product>> filterByPrice(@PathVariable int initialRange,
                                                       @PathVariable int finalRange) {
        final List<Product> filteredProducts = productService.filterByPrice(initialRange, finalRange);
        return ResponseEntity.ok(filteredProducts);
    }

    @GetMapping("/sort/price")
    public ResponseEntity<List<String>> sortByPrice() {
        final List<String> sortedProductNames = productService.sortByPrice();
        return ResponseEntity.ok(sortedProductNames);
    }
}
