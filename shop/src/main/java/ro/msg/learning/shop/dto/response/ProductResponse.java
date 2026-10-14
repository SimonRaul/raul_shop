package ro.msg.learning.shop.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductResponse {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Product description is required")
    private String description;

    @NotNull(message = "Product price is required")
    private BigDecimal price;

    @NotNull(message = "Product weight is required")
    private Double weight;

    @NotBlank(message = "Product category is required")
    private String category;

    @NotBlank(message = "Product category description is required")
    private String categoryDescription;

    private String imageUrl;
}
