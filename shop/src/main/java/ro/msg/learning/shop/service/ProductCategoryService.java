package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.model.ProductCategory;
import ro.msg.learning.shop.repository.ProductCategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository categoryRepository;

    public ProductCategory createCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    public ProductCategory getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Product category with id " + id));
    }

    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public ProductCategory updateCategory(UUID id, ProductCategory category) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Product category with id " + id);
        }
        category.setId(id);
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}
