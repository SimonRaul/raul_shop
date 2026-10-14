package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.model.ProductCategory;
import ro.msg.learning.shop.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductCategoryService productCategoryService;

    public Product createProduct(Product product) {
        product.setCategory(productCategoryService.getCategoryById(product.getCategory().getId()));

        return productRepository.save(product);
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product with id " + id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(UUID id, Product product) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Product with id " + id));

        ProductCategory category = productCategoryService.getCategoryById(product.getCategory().getId());

        existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setWeight(product.getWeight());
            existingProduct.setCategory(category);
            return productRepository.save(existingProduct);
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
