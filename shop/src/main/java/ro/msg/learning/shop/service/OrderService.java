package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.exception.OutOfStockException;
import ro.msg.learning.shop.model.*;
import ro.msg.learning.shop.repository.OrderRepository;
import ro.msg.learning.shop.strategy.LocationStrategy;
import ro.msg.learning.shop.strategy.StockAllocation;

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

    private final LocationStrategy locationStrategy;

    @Transactional
    public Order createOrder(Order order, UUID userId) {
        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product.");
        }

        List<StockAllocation> allocations = locationStrategy.determineLocations(order);

        if (allocations == null || allocations.isEmpty()) {
            throw new OutOfStockException();
        }

        order.getOrderDetails().forEach(detail -> {
            StockAllocation allocation = allocations.stream()
                    .filter(a -> a.product().getId().equals(detail.getProduct().getId()))
                    .findFirst().orElseThrow(OutOfStockException::new);
            detail.setId(new OrderDetailId(order.getId(),allocation.product().getId()));
            detail.setShippedFrom(allocation.location());
            detail.setProduct(allocation.product());
            detail.setUnitPrice(allocation.product().getPrice());
        });

        order.setTotalAmount(calculateTotalAmount(order));
        updateStock(allocations);

        order.setUser(userService.getUserById(userId));

        return orderRepository.save(order);
    }

    public BigDecimal calculateTotalAmount(Order order) {
        return order.getOrderDetails().stream()
                .map(detail -> detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateStock(List<StockAllocation> allocations) {
        allocations.forEach(allocation -> {
            Stock stock = stockService.getStockByLocationIdAndProductId(allocation.location().getId(),
                    allocation.product().getId());
            if (stock != null) {
                stock.setQuantity(stock.getQuantity() - allocation.quantity());
                stockService.updateStock(stock.getId(), stock);
            }
            else {
                throw new NotFoundException("Stock for product " + allocation.product().getId() +
                        " at location " + allocation.location().getId());
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
