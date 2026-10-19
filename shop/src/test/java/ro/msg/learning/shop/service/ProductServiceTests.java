package ro.msg.learning.shop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.model.ProductCategory;
import ro.msg.learning.shop.repository.ProductRepository;

import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryService productCategoryService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private ProductService productService;

    private Product productWithId(UUID id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product 1");
        product.setDescription("Description 1");
        product.setPrice(BigDecimal.TEN);
        product.setWeight(100.0);

        ProductCategory category = productCategoryWithId(id);
        category.setName("Category 1");
        category.setDescription("Category description 1");
        product.setCategory(category);

        return product;
    }

    private ProductCategory productCategoryWithId(UUID id) {
        ProductCategory category = new ProductCategory();
        category.setId(id);
        return category;
    }

    @Test
    void testCreateProduct() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();


        ProductCategory category = productCategoryWithId(categoryId);

        Product product = productWithId(productId);
        product.setCategory(category);


        when(productRepository.save(product)).thenReturn(product);
        when(productCategoryService.getCategoryById(categoryId)).thenReturn(category);

        Product createdProduct = productService.createProduct(product);

        assertEquals("Product 1", createdProduct.getName());
        assertEquals(product, createdProduct);
        verify(productRepository).save(product);
    }

    @Test
    void testGetProductById() {
        UUID productId = UUID.randomUUID();
        Product product = productWithId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(productId);

        assertEquals(product, foundProduct);
        verify(productRepository).findById(productId);
    }

    @Test
    void testGetProductByIdNotFound() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        try {
            productService.getProductById(productId);
        } catch (Exception e) {
            assertEquals("Product with id " + productId + " not found", e.getMessage());
        }

        verify(productRepository).findById(productId);
    }

    @Test
    void testUpdateProduct() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductCategory category = productCategoryWithId(categoryId);

        Product existingProduct = productWithId(productId);

        Product updatedProduct = productWithId(productId);
        updatedProduct.setName("New Product");
        updatedProduct.setDescription("New Description");
        updatedProduct.setPrice(BigDecimal.valueOf(15.0));
        updatedProduct.setWeight(150.0);
        updatedProduct.setCategory(category);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productCategoryService.getCategoryById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(productId, updatedProduct);

        assertEquals("New Product", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(BigDecimal.valueOf(15.0), result.getPrice());
        assertEquals(150.0, result.getWeight());
        assertEquals(category, result.getCategory());
        verify(productRepository).findById(productId);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void testUpdateProductNotFound() {
        UUID productId = UUID.randomUUID();
        Product updatedProduct = productWithId(productId);
        updatedProduct.setName("New Product");
        updatedProduct.setDescription("New Description");
        updatedProduct.setPrice(BigDecimal.valueOf(15.0));
        updatedProduct.setWeight(150.0);
        updatedProduct.setCategory(productCategoryWithId(UUID.randomUUID()));

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        try {
            productService.updateProduct(productId, updatedProduct);
        } catch (Exception e) {
            assertEquals("Product with id " + productId + " not found", e.getMessage());
        }

        verify(productRepository).findById(productId);
    }

    @Test
    void testDeleteProduct() {
        UUID productId = UUID.randomUUID();

        when(productRepository.existsById(productId)).thenReturn(true);

        productService.deleteProduct(productId);

        verify(productRepository).existsById(productId);
        verify(stockService).deleteAllByProductId(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void testDeleteProductNotFound() {
        UUID productId = UUID.randomUUID();

        when(productRepository.existsById(productId)).thenReturn(false);

        try {
            productService.deleteProduct(productId);
        } catch (Exception e) {
            assertEquals("Product with id " + productId + " not found", e.getMessage());
        }

        verify(productRepository).existsById(productId);
    }

    @Test
    void testGetAllProducts() {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        Product product1 = productWithId(productId1);
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(BigDecimal.valueOf(10.0));
        product1.setWeight(100.0);
        product1.setCategory(productCategoryWithId(UUID.randomUUID()));
        product1.setImageUrl("image1.jpg");

        Product product2 = productWithId(productId2);
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(BigDecimal.valueOf(20.0));
        product2.setWeight(200.0);
        product2.setCategory(productCategoryWithId(UUID.randomUUID()));
        product2.setImageUrl("image2.jpg");

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertEquals(product1, products.get(0));
        assertEquals(product2, products.get(1));
        verify(productRepository).findAll();
    }
}
