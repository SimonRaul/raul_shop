package ro.msg.learning.shop.strategy;

import ro.msg.learning.shop.model.Location;
import ro.msg.learning.shop.model.Product;

public record StockAllocation(Location location, Product product, Integer quantity) {
}
