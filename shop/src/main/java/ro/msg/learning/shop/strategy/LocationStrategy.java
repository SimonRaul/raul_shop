package ro.msg.learning.shop.strategy;

import ro.msg.learning.shop.model.Order;

import java.util.List;

public interface LocationStrategy {
    List<StockAllocation> determineLocations(Order order);
}
