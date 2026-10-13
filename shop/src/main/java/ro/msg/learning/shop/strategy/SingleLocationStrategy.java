package ro.msg.learning.shop.strategy;

import lombok.RequiredArgsConstructor;
import ro.msg.learning.shop.exception.OutOfStockException;
import ro.msg.learning.shop.model.Location;
import ro.msg.learning.shop.model.Order;
import ro.msg.learning.shop.repository.LocationRepository;
import ro.msg.learning.shop.repository.specification.LocationSpecification;

import java.util.List;

@RequiredArgsConstructor
public class SingleLocationStrategy implements LocationStrategy{

    private final LocationRepository locationRepository;

    @Override
    public List<StockAllocation> determineLocations(Order order) {
        List<Location> validLocations = locationRepository.findAll(
                LocationSpecification.hasAllProductsInStock(order.getOrderDetails()));

        if (validLocations.isEmpty()) {
            throw new OutOfStockException();
        }

            return order.getOrderDetails().stream()
                .map(detail ->
                        new StockAllocation(validLocations.getFirst(), detail.getProduct(), detail.getQuantity()))
                .toList();
    }
}
