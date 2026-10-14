package ro.msg.learning.shop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.mapper.ProductMapper;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.model.ProductCategory;
import ro.msg.learning.shop.service.ProductService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
@Import(ProductMapper.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private Product productWithId(UUID id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product 1");
        product.setDescription("Description 1");
        product.setPrice(BigDecimal.TEN);
        product.setWeight(100.0);

        ProductCategory category = new ProductCategory();
        category.setId(id);
        category.setName("Category 1");
        category.setDescription("Category description 1");
        product.setCategory(category);
        product.setImageUrl("image1.jpg");

        return product;
    }

    private List<Product> productsWithIds(List<UUID> ids) {
        return ids.stream().map(this::productWithId).toList();
    }

    @Test
    void testGetAllProducts() throws Exception {
        UUID firstProductId = UUID.randomUUID();
        UUID secondProductId = UUID.randomUUID();
        List<Product> products = productsWithIds(List.of(firstProductId, secondProductId));
        products.get(0).setName("Product 1");
        products.get(1).setName("Product 2");

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(products.size()))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }

    @Test
    void testGetAllProductsEmpty() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetProductById() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = productWithId(productId);

        when(productService.getProductById(product.getId())).thenReturn(product);

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.description").value("Description 1"));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById(any(UUID.class))).thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct() throws Exception {
        UUID validCategoryId = UUID.randomUUID();
        UUID generatedProductId = UUID.randomUUID();

        Product product = productWithId(generatedProductId);

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        String jsonPayload = String.format(
                "{\"name\":\"Product 1\",\"description\":\"Description 1\",\"price\":10.0,\"weight\":100.0,\"categoryId\":\"%s\",\"imageUrl\":\"image1.jpg\"}",
                validCategoryId
        );

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.description").value("Description 1"));
    }

    @Test
    void testCreateProductInvalidPayload() throws Exception {
        UUID validCategoryId = UUID.randomUUID();

        String jsonPayload = String.format(
                "{\"name\":\"\",\"description\":\"Description 1\",\"price\":10.0,\"weight\":100.0,\"categoryId\":\"%s\",\"imageUrl\":\"image1.jpg\"}",
                validCategoryId
        );

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID validCategoryId = UUID.randomUUID();

        Product product = productWithId(productId);

        when(productService.updateProduct(any(UUID.class), any(Product.class))).thenReturn(product);

        String jsonPayload = String.format(
                "{\"name\":\"Product 1\",\"description\":\"Description 1\",\"price\":10.0,\"weight\":100.0,\"categoryId\":\"%s\",\"imageUrl\":\"image1.jpg\"}",
                validCategoryId
        );

        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType("application/json")
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.description").value("Description 1"));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID validCategoryId = UUID.randomUUID();

        when(productService.updateProduct(any(UUID.class), any(Product.class)))
                .thenThrow(new NotFoundException("Product not found"));

        String jsonPayload = String.format(
                "{\"name\":\"Product 1\",\"description\":\"Description 1\",\"price\":10.0,\"weight\":100.0,\"categoryId\":\"%s\",\"imageUrl\":\"image1.jpg\"}",
                validCategoryId
        );

        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType("application/json")
                        .content(jsonPayload))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();

        doThrow(new NotFoundException("Product not found"))
                .when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNotFound());
    }
}
