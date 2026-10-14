package ro.msg.learning.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import ro.msg.learning.shop.model.Address;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderResponse {
    private String username;
    private LocalDateTime createdAt;
    private Address deliveryAddress;
    private BigDecimal totalAmount;
    private List<ProductOrderResponseDto> products;
}
