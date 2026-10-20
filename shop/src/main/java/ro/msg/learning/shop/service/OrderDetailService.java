package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.msg.learning.shop.repository.OrderDetailRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderDetailRepository detailRepository;

    public List<Object[]> aggregateRevenueByDate(LocalDate date) {
        return detailRepository.aggregateRevenueByDate(date);
    }
}
