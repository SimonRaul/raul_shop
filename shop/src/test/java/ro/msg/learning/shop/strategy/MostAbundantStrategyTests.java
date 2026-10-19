package ro.msg.learning.shop.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.msg.learning.shop.exception.OutOfStockException;
import ro.msg.learning.shop.model.Location;
import ro.msg.learning.shop.model.Order;
import ro.msg.learning.shop.model.OrderDetail;
import ro.msg.learning.shop.model.Product;
import ro.msg.learning.shop.model.Stock;
import ro.msg.learning.shop.repository.ProductRepository;
import ro.msg.learning.shop.repository.StockRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MostAbundantStrategyTests {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private MostAbundantStrategy mostAbundantStrategy;

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

    private static Stock stock(Product product, Location location, int quantity) {
        return Stock.builder()
                .product(product)
                .location(location)
                .quantity(quantity)
                .build();
    }

    @Test
    void testDetermineLocations() {
        UUID firstProductId = UUID.randomUUID();
        UUID secondProductId = UUID.randomUUID();
        Product firstProduct = productWithId(firstProductId);
        Product secondProduct = productWithId(secondProductId);
        Location firstLocation = new Location();
        Location secondLocation = new Location();
        Stock firstStock = stock(firstProduct, firstLocation, 4);
        Stock secondStock = stock(secondProduct, secondLocation, 7);
        Order order = Order.builder()
                .orderDetails(List.of(
                        orderDetail(firstProduct, 4),
                        orderDetail(secondProduct, 3)))
                .build();
        when(stockRepository.findFirstByProductIdOrderByQuantityDesc(firstProductId)).thenReturn(firstStock);
        when(stockRepository.findFirstByProductIdOrderByQuantityDesc(secondProductId)).thenReturn(secondStock);
        when(productRepository.existsById(firstProductId)).thenReturn(true);
        when(productRepository.existsById(secondProductId)).thenReturn(true);

        List<StockAllocation> allocations = mostAbundantStrategy.determineLocations(order);

        assertEquals(List.of(
                new StockAllocation(firstLocation, firstProduct, 4),
                new StockAllocation(secondLocation, secondProduct, 3)), allocations);
        verify(stockRepository).findFirstByProductIdOrderByQuantityDesc(firstProductId);
        verify(stockRepository).findFirstByProductIdOrderByQuantityDesc(secondProductId);
        verify(productRepository).existsById(firstProductId);
        verify(productRepository).existsById(secondProductId);
    }

    @Test
    void testDetermineLocationsReturnsEmptyListForEmptyOrder() {
        Order order = Order.builder().orderDetails(List.of()).build();

        List<StockAllocation> allocations = mostAbundantStrategy.determineLocations(order);

        assertEquals(List.of(), allocations);
        verifyNoInteractions(stockRepository);
        verifyNoInteractions(productRepository);
    }

    @Test
    void testDetermineLocationsStockIsMissing() {
        UUID productId = UUID.randomUUID();
        Order order = Order.builder()
                .orderDetails(List.of(orderDetail(productWithId(productId), 1)))
                .build();
        when(stockRepository.findFirstByProductIdOrderByQuantityDesc(productId)).thenReturn(null);
        when(productRepository.existsById(productId)).thenReturn(true);

        OutOfStockException exception = assertThrows(
                OutOfStockException.class,
                () -> mostAbundantStrategy.determineLocations(order));

        assertEquals("No location can fulfill all products in the required quantities.", exception.getMessage());
        verify(productRepository).existsById(productId);
        verify(stockRepository).findFirstByProductIdOrderByQuantityDesc(productId);
    }

    @Test
    void testDetermineLocationsStockQuantityIsInsufficient() {
        UUID productId = UUID.randomUUID();
        Product product = productWithId(productId);
        Order order = Order.builder()
                .orderDetails(List.of(orderDetail(product, 5)))
                .build();
        when(stockRepository.findFirstByProductIdOrderByQuantityDesc(productId))
                .thenReturn(stock(product, new Location(), 4));
        when(productRepository.existsById(productId)).thenReturn(true);

        OutOfStockException exception = assertThrows(
                OutOfStockException.class,
                () -> mostAbundantStrategy.determineLocations(order));

        assertEquals("No location can fulfill all products in the required quantities.", exception.getMessage());
        verify(productRepository).existsById(productId);
        verify(stockRepository).findFirstByProductIdOrderByQuantityDesc(productId);
    }
}
