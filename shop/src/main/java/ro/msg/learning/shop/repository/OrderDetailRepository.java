package ro.msg.learning.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.msg.learning.shop.model.OrderDetail;
import ro.msg.learning.shop.model.OrderDetailId;

import java.time.LocalDate;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    @Query("SELECT od.shippedFrom, SUM(od.unitPrice * od.quantity) " +
            "FROM OrderDetail od " +
            "WHERE CAST(od.order.createdAt AS date) = :date " +
            "GROUP BY od.shippedFrom")
    List<Object[]> aggregateRevenueByDate(@Param("date") LocalDate date);
}
