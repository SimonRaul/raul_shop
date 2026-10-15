package ro.msg.learning.shop.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.msg.learning.shop.model.*;
import ro.msg.learning.shop.repository.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class OrderControllerITTests {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    private UUID existingProductId;
    private UUID existingUserId;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        locationRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        addressRepository.deleteAll();

        User user = new User("John", "Doe", "johndoe", "password", "john.doe@example.com", UserRole.USER);
        user = userRepository.save(user);
        this.existingUserId = user.getId();

        ProductCategory category = new ProductCategory("Electronics", "Electronic devices and gadgets");
        category = categoryRepository.save(category);

        Product product = new Product("Laptop", "Desc", BigDecimal.valueOf(1000), 2.0, category, "img.jpg");
        product = productRepository.save(product);
        this.existingProductId = product.getId();

        Address address = new Address("Cluj-Napoca", "Romania", "CJ", "Strada X");
        address = addressRepository.save(address);

        Location location = new Location("Depozit Central", address);
        location = locationRepository.save(location);

        Stock stock = new Stock(new StockId(product.getId(), location.getId()), product, location, 10);
        stockRepository.save(stock);
    }

    @Test
    void testCreateOrderSuccess() {
        String requestBody = String.format("""
                {
                    "createdAt": "2026-10-14T15:00:00",
                    "deliveryAddress": {
                        "country": "Romania",
                        "city": "Cluj-Napoca",
                        "county": "Cluj",
                        "streetAddress": "Observatorului"
                    },
                    "products": [
                        {
                            "productId": "%s",
                            "quantity": 2
                        }
                    ],
                    "customerId": "%s"
                }
                """, existingProductId, existingUserId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(201)
                .body("deliveryAddress", notNullValue());
    }

    @Test
    void testCreateOrderInsufficientStock() {
        String requestBody = String.format("""
                {
                    "createdAt": "2026-10-14T15:00:00",
                    "deliveryAddress": {
                        "country": "Romania",
                        "city": "Cluj-Napoca",
                        "county": "Cluj",
                        "streetAddress": "Observatorului"
                    },
                    "products": [
                        {
                            "productId": "%s",
                            "quantity": 101
                        }
                    ],
                    "customerId": "%s"
                }
                """, existingProductId, existingUserId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(422);
    }

}
