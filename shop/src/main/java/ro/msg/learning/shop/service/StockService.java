package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.model.Stock;
import ro.msg.learning.shop.model.StockId;
import ro.msg.learning.shop.repository.StockRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock getStockById(StockId stockId) {
        return stockRepository.findById(stockId).orElseThrow(() -> new NotFoundException("Stock with id " + stockId));
    }

    public Stock getStockByLocationIdAndProductId(UUID locationId, UUID productId) {
        return stockRepository.findByLocationIdAndProductId(locationId, productId);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock updateStock(StockId stockId, Stock stock) {
        Stock existingStock = stockRepository.findById(stockId).orElseThrow(() ->
                new NotFoundException("Stock with id " + stockId));
        existingStock.setProduct(stock.getProduct());
        existingStock.setLocation(stock.getLocation());
        existingStock.setQuantity(stock.getQuantity());
        return stockRepository.save(existingStock);
    }

    public void deleteAllStocks() {
        stockRepository.deleteAll();
    }
}
