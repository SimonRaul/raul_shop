package ro.msg.learning.shop.mapper;
import org.springframework.stereotype.Component;
import ro.msg.learning.shop.dto.response.ProductResponse;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.model.ProductCategory;

@Component
public class ProductMapper {
    public ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .weight(product.getWeight())
                .category(product.getCategory().getName())
                .categoryDescription(product.getCategory().getDescription())
                .imageUrl(product.getImageUrl())
                .build();
    }

    public Product toModel(ProductResponse dto) {
        ProductCategory category = ProductCategory.builder()
                .name(dto.getCategory())
                .description(dto.getCategoryDescription())
                .build();

        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .weight(dto.getWeight())
                .category(category)
                .imageUrl(dto.getImageUrl())
                .build();
    }
}