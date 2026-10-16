package ro.msg.learning.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.shop.model.Revenue;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RevenueRepository extends JpaRepository<Revenue, UUID> {
    boolean existsByDate(LocalDate date);
    List<Revenue> findByDateOrderByLocation_NameAscLocation_IdAsc(LocalDate date);
}
