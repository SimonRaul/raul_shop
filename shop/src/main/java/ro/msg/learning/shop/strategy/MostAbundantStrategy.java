package ro.msg.learning.shop.strategy;

import lombok.RequiredArgsConstructor;
import ro.msg.learning.shop.exception.OutOfStockException;
import ro.msg.learning.shop.model.Order;
import ro.msg.learning.shop.repository.StockRepository;

import java.util.List;

@RequiredArgsConstructor
public class MostAbundantStrategy implements LocationStrategy{

    private final StockRepository stockRepository;

    @Override
    public List<StockAllocation> determineLocations(Order order) {
        return order.getOrderDetails().stream()
                .map(detail -> {
                    var stock = stockRepository.findFirstByProductIdOrderByQuantityDesc(detail.getProduct().getId());
                    if (stock == null || stock.getQuantity() < detail.getQuantity()) {
                        throw new OutOfStockException();
                    }
                    return new StockAllocation(stock.getLocation(), detail.getProduct(), detail.getQuantity());
                })
                .toList();
    }
}
