package ro.msg.learning.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.shop.model.Stock;
import ro.msg.learning.shop.model.StockId;

public interface StockRepository extends JpaRepository<Stock, StockId> {
}
