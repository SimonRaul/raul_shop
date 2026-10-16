package ro.msg.learning.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.shop.model.Revenue;

import java.time.LocalDate;
import java.util.UUID;

public interface RevenueRepository extends JpaRepository<Revenue, UUID> {
    boolean existsByDate(LocalDate date);
}
