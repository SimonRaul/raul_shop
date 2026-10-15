package ro.msg.learning.shop;

import org.springframework.boot.SpringApplication;
import ro.msg.learning.shop.config.TestConfig;

public class TestShopApplication {

    public static void main(String[] args) {
        SpringApplication.from(ShopApplication::main)
                .with(TestConfig.class)
                .withAdditionalProfiles("test")
                .run(args);
    }
}
