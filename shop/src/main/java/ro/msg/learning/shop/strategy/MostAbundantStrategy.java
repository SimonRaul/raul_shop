package ro.msg.learning.shop.strategy;

import lombok.RequiredArgsConstructor;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.exception.OutOfStockException;
import ro.msg.learning.shop.model.Order;
import ro.msg.learning.shop.model.Stock;
import ro.msg.learning.shop.repository.ProductRepository;
import ro.msg.learning.shop.repository.StockRepository;

import java.util.List;

@RequiredArgsConstructor
public class MostAbundantStrategy implements LocationStrategy {

    private final StockRepository stockRepository;

    private final ProductRepository productRepository;

    @Override
    public List<StockAllocation> determineLocations(Order order) {
        return order.getOrderDetails().stream()
                .map(detail -> {
                    if(detail.getProduct().getId() == null || !productRepository.existsById(detail.getProduct().getId())) {
                        throw new NotFoundException("Product with id " + detail.getProduct().getId());
                    }
                    Stock stock = stockRepository.findFirstByProductIdOrderByQuantityDesc(detail.getProduct().getId());
                    if (stock == null || stock.getQuantity() < detail.getQuantity()) {
                        throw new OutOfStockException();
                    }
                    return new StockAllocation(stock.getLocation(), stock.getProduct(), detail.getQuantity());
                })
                .toList();
    }
}
