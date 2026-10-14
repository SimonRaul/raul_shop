package ro.msg.learning.shop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.model.ProductCategory;
import ro.msg.learning.shop.service.ProductService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class ProductControllerTests {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = List.of(
                new Product("Product 1", "Description 1", BigDecimal.valueOf(10.0),
                        100.0, new ProductCategory("Category 1", "Description 1"), "image1.jpg"),
                new Product("Product 2", "Description 2", BigDecimal.valueOf(20.0),
                        200.0, new ProductCategory("Category 2", "Description 2"), "image2.jpg")
        );

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(products.size()))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = new Product("Product 1", "Description 1", BigDecimal.valueOf(10.0),
                100.0, new ProductCategory("Category 1", "Description 1"), "image1.jpg");

        when(productService.getProductById(product.getId())).thenReturn(product);

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.description").value("Description 1"));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById(any(UUID.class))).thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/{id}", "non-existing-id"))
                .andExpect(status().isNotFound());
    }
}
