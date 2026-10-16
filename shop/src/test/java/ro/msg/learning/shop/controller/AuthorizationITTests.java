package ro.msg.learning.shop.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import ro.msg.learning.shop.config.TestConfig;
import ro.msg.learning.shop.security.JwtUtil;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class AuthorizationITTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void customerCannotDeleteOrder() {
        RestAssured.given()
                .header("Authorization", "Bearer " + customerAccessToken())
                .when()
                .delete("/api/orders/{id}", UUID.randomUUID())
                .then()
                .statusCode(403);
    }

    @Test
    void customerCannotDeleteProduct() {
        RestAssured.given()
                .header("Authorization", "Bearer " + customerAccessToken())
                .when()
                .delete("/api/products/{id}", UUID.randomUUID())
                .then()
                .statusCode(403);
    }

    private String customerAccessToken() {
        return jwtUtil.createToken(new UsernamePasswordAuthenticationToken(
                "customer",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        ));
    }
}
