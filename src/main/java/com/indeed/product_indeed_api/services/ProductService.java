package com.indeed.product_indeed_api.services;

import com.indeed.product_indeed_api.exceptions.InvalidPriceRangeException;
import com.indeed.product_indeed_api.exceptions.ProductAlreadyExistsException;
import com.indeed.product_indeed_api.models.Product;
import com.indeed.product_indeed_api.respositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    private ProductService(ProductRepository productRepository) {
        Assert.notNull(productRepository, "productRepository must not be null");
        this.productRepository = productRepository;
    }


    public List<Product> getAllProdcuts() {
        return productRepository.findAll();
    }

    public Product createProduct(final Product product) {
        if (productRepository.existsById(product.getBarcode())) {
            throw new ProductAlreadyExistsException(product.getBarcode());
        }
        return productRepository.save(product);
    }

    public List<Product> filterByPrice(final int initialRange, final int finalRange) {
        if (initialRange < 0 || finalRange < 0 || initialRange > finalRange) {
            throw new InvalidPriceRangeException("Price range is invalid: "
                    + initialRange + " to " + finalRange);
        }
        return productRepository.findByPriceBetween(initialRange, finalRange);
    }

    public List<String> sortByPrice() {
        List<Product> sortedProducts = productRepository.findAllByOrderByPriceAsc();
        return sortedProducts.stream()
                .map(Product::getItem)
                .collect(Collectors.toList());
    }
}
