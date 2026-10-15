package ro.msg.learning.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.msg.learning.shop.dto.response.StockResponse;
import ro.msg.learning.shop.mapper.StockMapper;
import ro.msg.learning.shop.service.StockService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    private final StockMapper stockMapper;

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        List<StockResponse> stocks = stockMapper.toResponseDtoList(stockService.getAllStocks());

        return ResponseEntity.ok().body(stocks);
    }

    @GetMapping(value = "/export/{locationId}", produces = "text/csv")
    public String exportStocks(@PathVariable UUID locationId) {
        return stockService.export(locationId);
    }
}
