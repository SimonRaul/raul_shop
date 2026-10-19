package ro.msg.learning.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private static final String CSV_HEADER = "locationId,locationName,date,sum\n";

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
                    .date(date)
                    .location(location)
                    .build();

            revenueRepository.save(revenue);
        }
    }

    @Transactional(readOnly = true)
    public String exportCsv(LocalDate date) {
        StringBuilder csv = new StringBuilder(CSV_HEADER);

        findByDate(date).forEach(revenue -> appendCsvRow(csv, revenue));

        return csv.toString();
    }

    @Transactional(readOnly = true)
    public List<Revenue> findByDate(LocalDate date) {
        return revenueRepository.findByDateOrderByLocation_NameAscLocation_IdAsc(date);
    }

    private void appendCsvRow(StringBuilder csv, Revenue revenue) {

        csv.append(revenue.getLocation().getId())
                .append(',')
                .append(escapeCsv(revenue.getLocation().getName()))
                .append(',')
                .append(revenue.getDate())
                .append(',')
                .append(revenue.getAmount())
                .append('\n');
    }

    private String escapeCsv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

}
