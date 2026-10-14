package ro.msg.learning.shop.mapper;

import org.springframework.stereotype.Component;

import ro.msg.learning.shop.dto.request.CreateOrderRequest;
import ro.msg.learning.shop.dto.response.OrderResponse;
import ro.msg.learning.shop.dto.response.ProductOrderResponseDto;
import ro.msg.learning.shop.model.Address;
import ro.msg.learning.shop.model.Order;
import ro.msg.learning.shop.model.OrderDetail;
import ro.msg.learning.shop.model.Product;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toDto(Order order) {
        List<ProductOrderResponseDto> products = order.getOrderDetails().stream()
                .map(detail -> ProductOrderResponseDto.builder()
                        .productName(detail.getProduct().getName())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getProduct().getPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .username(order.getUser().getUsername())
                .createdAt(order.getCreatedAt())
                .deliveryAddress(order.getAddress())
                .totalAmount(order.getTotalAmount())
                .products(products)
                .build();
    }

    public Order toModel(CreateOrderRequest createDto){
        Address deliveryAddress = Address.builder()
                .country(createDto.getDeliveryAddress().getCountry())
                .city(createDto.getDeliveryAddress().getCity())
                .county(createDto.getDeliveryAddress().getCounty())
                .streetAddress(createDto.getDeliveryAddress().getStreetAddress())
                .build();

        Order order = Order.builder()
                .createdAt(createDto.getCreatedAt())
                .address(deliveryAddress)
                .build();

        List<OrderDetail> details = createDto.getProducts().stream().map(prodDto -> {

            Product productRef = Product.builder().id(prodDto.getProductId()).build();

            return OrderDetail.builder()
                    .order(order)
                    .product(productRef)
                    .quantity(prodDto.getQuantity())
                    .build();

        }).toList();

        order.setOrderDetails(details);
        return order;
    }
}
