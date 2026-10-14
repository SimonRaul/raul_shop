package ro.msg.learning.shop.strategy;

import lombok.RequiredArgsConstructor;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.exception.OutOfStockException;
import ro.msg.learning.shop.model.Location;
import ro.msg.learning.shop.model.Order;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.repository.LocationRepository;
import ro.msg.learning.shop.repository.ProductRepository;
import ro.msg.learning.shop.repository.specification.LocationSpecification;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class SingleLocationStrategy implements LocationStrategy {

    private final LocationRepository locationRepository;

    private final ProductRepository productRepository;

    @Override
    public List<StockAllocation> determineLocations(Order order) {
        List<Location> validLocations = locationRepository.findAll(
                LocationSpecification.hasAllProductsInStock(order.getOrderDetails()));

        if (validLocations.isEmpty()) {
            throw new OutOfStockException();
        }

        return order.getOrderDetails().stream()
                .map(detail -> {
                    UUID productId = detail.getProduct().getId();
                    Product fullProduct = productRepository.findById(productId)
                            .orElseThrow(() -> new NotFoundException("Product with id " + productId));

                    return new StockAllocation(validLocations.getFirst(), fullProduct, detail.getQuantity());
                })
                .toList();
    }
}
