package com.indeed.product_indeed_api.controllers;

import com.indeed.product_indeed_api.models.Product;
import com.indeed.product_indeed_api.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

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
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void testGetAllProducts_ReturnsProducts() throws Exception {
        when(productService.getAllProdcuts()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].item").value("Ball Gown"))
                .andExpect(jsonPath("$[1].item").value("Shawl"));
    }

    @Test
    void testGetAllProducts_ReturnsEmptyList() throws Exception {
        when(productService.getAllProdcuts()).thenReturn(List.of());

        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testCreateProduct_ReturnsCreatedProduct() throws Exception {
        when(productService.createProduct(product1)).thenReturn(product1);

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"barcode\":\"74001755\", \"item\":\"Ball Gown\", \"category\":\"Full Body Outfits\", \"price\":3548, \"discount\":7, \"available\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.barcode").value("74001755"))
                .andExpect(jsonPath("$.item").value("Ball Gown"));
    }

    @Test
    void testFilterByPrice_ReturnsFilteredProducts() throws Exception {
        List<Product> filteredProducts = Arrays.asList(product1, product2);

        when(productService.filterByPrice(700, 4000)).thenReturn(filteredProducts);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/products/filter/price/{initialRange}/{finalRange}", 700, 4000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Esperamos un status 200 OK
                .andExpect(jsonPath("$[0].item").value("Ball Gown"))
                .andExpect(jsonPath("$[1].item").value("Shawl"));
    }

    @Test
    void testFilterByPrice_ReturnsEmptyListWhenNoProductsFound() throws Exception {
        when(productService.filterByPrice(5000, 10000)).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/products/filter/price/{initialRange}/{finalRange}", 5000, 10000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    void testSortByPrice_ReturnsSortedProductNames() throws Exception {
        List<String> sortedNames = Arrays.asList("Shawl", "Ball Gown");

        when(productService.sortByPrice()).thenReturn(sortedNames);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/products/sort/price")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Shawl"))
                .andExpect(jsonPath("$[1]").value("Ball Gown"));
    }

    @Test
    void testSortByPrice_ReturnsEmptyListWhenNoProducts() throws Exception {
        when(productService.sortByPrice()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/products/sort/price")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testSortByPrice_ReturnsSingleProductWhenOnlyOneProduct() throws Exception {
        List<String> sortedNames = List.of("Ball Gown");

        when(productService.sortByPrice()).thenReturn(sortedNames);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/products/sort/price")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Ball Gown"));
    }

}
