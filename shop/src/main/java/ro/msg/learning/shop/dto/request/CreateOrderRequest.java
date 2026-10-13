package ro.msg.learning.shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.msg.learning.shop.model.Address;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateOrderRequest {
    private LocalDateTime createdAt;
    private Address deliveryAddress;
    private List<ProductQuantityDto> products;
}
