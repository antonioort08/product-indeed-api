package com.indeed.product_indeed_api.services;

import com.indeed.product_indeed_api.exceptions.InvalidPriceRangeException;
import com.indeed.product_indeed_api.exceptions.ProductAlreadyExistsException;
import com.indeed.product_indeed_api.models.Product;
import com.indeed.product_indeed_api.respositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .barcode("74001755")
                .item("Ball Gown")
                .category("Full Body Outfits")
                .price(3548)
                .discount(7)
                .available(1)
                .build();

        product2 = Product.builder()
                .barcode("74002423")
                .item("Shawl")
                .category("Accessories")
                .price(758)
                .discount(12)
                .available(1)
                .build();
    }

    @Test
    void testGetAllProducts_ReturnsProductList() {
        Mockito.when(productRepository.findAll())
                .thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.getAllProdcuts();

        assert(result.size() == 2);
        assert(result.contains(product1));
        assert(result.contains(product2));

    }

    @Test
    void testCreateProduct_CreatesProductSuccessfully() {
        Mockito.when(productRepository.existsById(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenReturn(product1);

        Product result = productService.createProduct(product1);

        assert(result != null);
        assert(result.getBarcode().equals(product1.getBarcode()));

        Mockito.verify(productRepository).save(product1);
    }

    @Test
    void testCreateProduct_ThrowsExceptionWhenProductExists() {
        Mockito.when(productRepository.existsById(Mockito.anyString()))
                .thenReturn(true);

        try {
            productService.createProduct(product1);
        } catch (ProductAlreadyExistsException ex) {
            assert(ex.getMessage().contains(product1.getBarcode()));
        }
    }

    @Test
    void testFilterByPrice_ReturnsFilteredProducts() {
        Mockito.when(productRepository.findByPriceBetween(100, 5000))
                .thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.filterByPrice(100, 5000);

        assert(result.size() == 2);
        assert(result.contains(product1));
        assert(result.contains(product2));
    }

    @Test
    void testFilterByPrice_ThrowsExceptionWhenRangeIsInvalid() {
        try {
            productService.filterByPrice(5000, 1000);
        } catch (InvalidPriceRangeException ex) {
            assert(ex.getMessage().contains("Price range is invalid"));
        }

        try {
            productService.filterByPrice(-100, -500);
        } catch (InvalidPriceRangeException ex) {
            assert(ex.getMessage().contains("Price range is invalid"));
        }

        try {
            productService.filterByPrice(3000, 1000);
        } catch (InvalidPriceRangeException ex) {
            assert(ex.getMessage().contains("Price range is invalid"));
        }
    }

    @Test
    void testSortByPrice_ReturnsSortedProductNames() {
        List<Product> sortedProducts = Arrays.asList(product2, product1);
        Mockito.when(productRepository.findAllByOrderByPriceAsc())
                .thenReturn(sortedProducts);

        List<String> result = productService.sortByPrice();

        assert(result.size() == 2);
        assert(result.get(0).equals("Shawl"));
        assert(result.get(1).equals("Ball Gown"));
    }

    @Test
    void testSortByPrice_ReturnsEmptyListWhenNoProducts() {
        Mockito.when(productRepository.findAllByOrderByPriceAsc())
                .thenReturn(List.of());

        List<String> result = productService.sortByPrice();

        assert(result.isEmpty());
    }
}
