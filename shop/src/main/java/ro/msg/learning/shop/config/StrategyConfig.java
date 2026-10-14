package ro.msg.learning.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.msg.learning.shop.repository.LocationRepository;
import ro.msg.learning.shop.repository.ProductRepository;
import ro.msg.learning.shop.repository.StockRepository;
import ro.msg.learning.shop.strategy.LocationStrategy;
import ro.msg.learning.shop.strategy.MostAbundantStrategy;
import ro.msg.learning.shop.strategy.SingleLocationStrategy;

@Configuration
public class StrategyConfig {
    @Bean
    public LocationStrategy locationStrategy(
            @Value("${shop.locationstrategy}") String strategyType,
            LocationRepository locationRepository,
            StockRepository stockRepository,
            ProductRepository productRepository) {

        return switch (strategyType.toLowerCase()) {
            case "single" -> new SingleLocationStrategy(locationRepository, productRepository);
            case "abundant" -> new MostAbundantStrategy(stockRepository);
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategyType);
        };
    }
}
