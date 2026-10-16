package ro.msg.learning.shop.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.msg.learning.shop.service.RevenueService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "shop.revenue.enabled", havingValue = "true", matchIfMissing = true)
public class RevenueReportTask {

    private final RevenueService revenueService;

    @Scheduled(cron = "${tasks.end-of-day.cron}", zone = "${tasks.end-of-day.timezone}")
    public void reportRevenueForPreviousDay() {
        revenueService.createRevenuesForDate(LocalDate.now().minusDays(1));
    }
}
