package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ro.msg.learning.shop.exception.EntityAlreadyExistsException;
import ro.msg.learning.shop.model.Location;
import ro.msg.learning.shop.model.Revenue;
import ro.msg.learning.shop.repository.RevenueRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final RevenueRepository revenueRepository;

    private final OrderDetailService orderDetailService;

    public void createRevenuesForDate(LocalDate date) {
        if(revenueRepository.existsByDate(date)) {
            throw new EntityAlreadyExistsException("Revenue for date " + date);
        }

        List<Object[]> revenueData = orderDetailService.aggregateRevenueByDate(date);

        BigDecimal amount;
        for (Object[] data : revenueData) {
            Location location = (Location) data[0];

            if(data[1] instanceof Number number){
                amount = BigDecimal.valueOf(number.doubleValue());
            } else {
                amount = BigDecimal.ZERO;
            }

            Revenue revenue = Revenue.builder()
                    .amount(amount)
                    .date(date.atStartOfDay())
                    .location(location)
                    .build();

            revenueRepository.save(revenue);
        }
    }
}
