package ro.msg.learning.shop.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.exception.OutOfStockException;
import ro.msg.learning.shop.model.Location;
import ro.msg.learning.shop.model.Order;
import ro.msg.learning.shop.model.OrderDetail;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.repository.LocationRepository;
import ro.msg.learning.shop.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SingleLocationStrategyTests {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SingleLocationStrategy singleLocationStrategy;

    private static Product productWithId(UUID id) {
        Product product = new Product();
        product.setId(id);
        return product;
    }

    private static OrderDetail orderDetail(Product product, int quantity) {
        return OrderDetail.builder()
                .product(product)
                .quantity(quantity)
                .build();
    }

    @Test
    void testDetermineLocations() {
        UUID firstProductId = UUID.randomUUID();
        UUID secondProductId = UUID.randomUUID();
        Product orderedFirstProduct = productWithId(firstProductId);
        Product orderedSecondProduct = productWithId(secondProductId);
        Product fullFirstProduct = productWithId(firstProductId);
        Product fullSecondProduct = productWithId(secondProductId);
        Location location = new Location();
        OrderDetail firstDetail = orderDetail(orderedFirstProduct, 2);
        OrderDetail secondDetail = orderDetail(orderedSecondProduct, 5);
        Order order = Order.builder().orderDetails(List.of(firstDetail, secondDetail)).build();

        when(locationRepository.findAll(isA(Specification.class))).thenReturn(List.of(location));
        when(productRepository.findById(firstProductId)).thenReturn(Optional.of(fullFirstProduct));
        when(productRepository.findById(secondProductId)).thenReturn(Optional.of(fullSecondProduct));

        List<StockAllocation> allocations = singleLocationStrategy.determineLocations(order);

        assertEquals(2, allocations.size());
        assertEquals(new StockAllocation(location, fullFirstProduct, 2), allocations.get(0));
        assertEquals(new StockAllocation(location, fullSecondProduct, 5), allocations.get(1));
        verify(locationRepository).findAll(isA(Specification.class));
        verify(productRepository).findById(firstProductId);
        verify(productRepository).findById(secondProductId);
    }

    @Test
    void testDetermineLocationsNoLocationCanFulfillOrder() {
        Order order = Order.builder()
                .orderDetails(List.of(orderDetail(productWithId(UUID.randomUUID()), 1)))
                .build();
        when(locationRepository.findAll(isA(Specification.class))).thenReturn(List.of());

        OutOfStockException exception = assertThrows(
                OutOfStockException.class,
                () -> singleLocationStrategy.determineLocations(order));

        assertEquals("No location can fulfill all products in the required quantities.", exception.getMessage());
        verify(locationRepository).findAll(isA(Specification.class));
        verifyNoInteractions(productRepository);
    }

    @Test
    void testDetermineLocationsIsNotFound() {
        UUID productId = UUID.randomUUID();
        Order order = Order.builder()
                .orderDetails(List.of(orderDetail(productWithId(productId), 1)))
                .build();
        when(locationRepository.findAll(isA(Specification.class))).thenReturn(List.of(new Location()));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> singleLocationStrategy.determineLocations(order));

        assertEquals("Product with id " + productId + " not found", exception.getMessage());
        verify(locationRepository).findAll(isA(Specification.class));
        verify(productRepository).findById(productId);
    }
}
