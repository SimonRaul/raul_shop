package ro.msg.learning.shop.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.msg.learning.shop.dto.response.StockResponse;
import ro.msg.learning.shop.model.Stock;

@RequiredArgsConstructor
@Component
public class StockMapper {

    private final ProductMapper productMapper;
    private final LocationMapper locationMapper;

    public StockResponse toResponseDto(Stock stock) {
        return StockResponse.builder()
                .product(productMapper.toResponseDto(stock.getProduct()))
                .location(locationMapper.toResponseDto(stock.getLocation()))
                .quantity(stock.getQuantity())
                .build();
    }
}
