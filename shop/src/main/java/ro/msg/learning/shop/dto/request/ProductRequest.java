package ro.msg.learning.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotBlank(message = "Product description cannot be blank")
    private String description;

    @NotBlank(message = "Product price cannot be blank")
    private BigDecimal price;

    @NotBlank(message = "Product weight cannot be blank")
    private Double weight;

    @NotBlank(message = "Product category cannot be blank")
    private UUID categoryId;

    private String imageUrl;
}
