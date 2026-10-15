package ro.msg.learning.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ro.msg.learning.shop.model.*;
import ro.msg.learning.shop.repository.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tests")
@Profile("test")
@RequiredArgsConstructor
public class PostmanAPIController {

    private final ProductRepository productRepository;

    private final LocationRepository locationRepository;

    private final StockRepository stockRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final AddressRepository addressRepository;

    private final ProductCategoryRepository categoryRepository;

    @PostMapping("/teardown")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void teardown() {
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        locationRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @PostMapping("/setup")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> setup() {
        User user = new User("John", "Doe", "johndoe", "password", "john.doe@example.com", UserRole.USER);
        userRepository.save(user);

        ProductCategory category = new ProductCategory("Electronics", "Electronic devices and gadgets");
        category = categoryRepository.save(category);

        Product product = new Product("Laptop", "Desc", BigDecimal.valueOf(1000), 2.0, category, "img.jpg");
        product = productRepository.save(product);

        Address address = new Address("Cluj-Napoca", "Romania", "CJ", "Strada X");
        address = addressRepository.save(address);

        Location location = new Location("Depozit Central", address);
        location = locationRepository.save(location);

        Stock stock = new Stock(new StockId(product.getId(), location.getId()), product, location, 10);
        stockRepository.save(stock);

        Map<String, UUID> ids = new HashMap<>();
        ids.put("userId", user.getId());
        ids.put("productId", product.getId());
        ids.put("locationId", location.getId());
        return ids;
    }
}
