package ro.msg.learning.shop.repository.specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ro.msg.learning.shop.model.Location;
import ro.msg.learning.shop.model.OrderDetail;
import ro.msg.learning.shop.model.Stock;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSpecification {

    public static Specification<Location> hasAllProductsInStock(List<OrderDetail> orderDetails) {
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Stock> stockRoot = subquery.from(Stock.class);

            subquery.select(stockRoot.get("location").get("id"));

            List<Predicate> orPredicates = new ArrayList<>();
            for (OrderDetail detail : orderDetails) {
                Predicate productMatch = cb.equal(stockRoot.get("product").get("id"), detail.getProduct().getId());
                Predicate quantityMatch = cb.greaterThanOrEqualTo(stockRoot.get("quantity"), detail.getQuantity());

                orPredicates.add(cb.and(productMatch, quantityMatch));
            }

            subquery.where(cb.or(orPredicates.toArray(new Predicate[0])));
            subquery.groupBy(stockRoot.get("location").get("id"));
            subquery.having(cb.equal(
                    cb.count(stockRoot.get("product").get("id")),
                    orderDetails.size()
            ));

            return cb.in(root.get("id")).value(subquery);
        };
    }
}
