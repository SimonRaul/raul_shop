package ro.msg.learning.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.shop.model.Stock;
import ro.msg.learning.shop.model.StockId;

import java.util.List;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, StockId> {
    Stock findByLocationIdAndProductId(UUID locationId, UUID productId);
    Stock findFirstByProductIdOrderByQuantityDesc(UUID productId);
    List<Stock> findByLocationIdOrderByProduct_NameAscProduct_IdAsc(UUID locationId);
}
