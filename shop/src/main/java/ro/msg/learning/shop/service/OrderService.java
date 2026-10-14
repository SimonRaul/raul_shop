package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.exception.OutOfStockException;
import ro.msg.learning.shop.model.*;
import ro.msg.learning.shop.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final StockService stockService;

    private final ProductService productService;

    private final UserService userService;

    private final LocationService locationService;

    @Transactional
    public Order createOrder(Order order, UUID userId) {
        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product.");
        }

        Location location = determineLocation(order);

        if (location == null) {
            throw new OutOfStockException();
        }

        order.getOrderDetails().forEach(detail -> {
            detail.setShippedFrom(location);
            Product product = productService.getProductById(detail.getProduct().getId());
            detail.setId(new OrderDetailId(order.getId(), product.getId()));
            detail.setProduct(product);
            detail.setUnitPrice(product.getPrice());
        });

        order.setTotalAmount(calculateTotalAmount(order));
        updateStock(order);

        order.setUser(userService.getUserById(userId));

        return orderRepository.save(order);
    }

    public Location determineLocation(Order order) {
        for (Location location : locationService.getAllLocations()) {
            boolean canFulfill = order.getOrderDetails().stream()
                    .allMatch(detail -> {
                        Stock stock = stockService.getStockByLocationIdAndProductId(location.getId(), detail.getProduct().getId());
                        return stock != null && stock.getQuantity() >= detail.getQuantity();
                    });
            if (canFulfill) {
                return location;
            }
        }
        return null;
    }

    public BigDecimal calculateTotalAmount(Order order) {
        return order.getOrderDetails().stream()
                .map(detail -> detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateStock(Order order) {
        order.getOrderDetails().forEach(detail -> {
            Stock stock = stockService.getStockByLocationIdAndProductId(detail.getShippedFrom().getId(),
                    detail.getProduct().getId());
            if (stock != null) {
                stock.setQuantity(stock.getQuantity() - detail.getQuantity());
                stockService.updateStock(stock.getId(), stock);
            }
            else {
                throw new NotFoundException("Stock for product " + detail.getProduct().getId() +
                        " at location " + detail.getShippedFrom().getId());
            }
        });
    }

    public Order getOrderById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(UUID id, Order order) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order with id " + id);
        }
        order.setId(id);
        return orderRepository.save(order);
    }

    public void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }
}
