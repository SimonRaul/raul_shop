package ro.msg.learning.shop.mapper;
import org.springframework.stereotype.Component;
import ro.msg.learning.shop.dto.request.ProductRequest;
import ro.msg.learning.shop.dto.response.ProductResponse;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.model.ProductCategory;

@Component
public class ProductMapper {
    public ProductResponse toResponseDto(Product product) {
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

    public Product toModelFromResponse(ProductResponse dto) {
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

    public Product toModelFromRequest(ProductRequest request) {
        ProductCategory category = ProductCategory.builder()
                .id(request.getCategoryId())
                .build();

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .weight(request.getWeight())
                .category(category)
                .imageUrl(request.getImageUrl())
                .build();
    }
}