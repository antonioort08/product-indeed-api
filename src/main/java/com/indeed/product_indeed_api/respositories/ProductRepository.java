package com.indeed.product_indeed_api.respositories;

import com.indeed.product_indeed_api.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByPriceBetween(final int initialRange, final int finalRange);

    List<Product> findAllByOrderByPriceAsc();
}
