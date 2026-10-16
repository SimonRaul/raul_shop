package ro.msg.learning.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.msg.learning.shop.service.RevenueService;

@RestController
@RequestMapping("/api/revenues")
@RequiredArgsConstructor
public class RevenueController {
    private final RevenueService revenueService;

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('ADMIN')")
    private String exportRevenues(@RequestBody RevenueRequest request) {


        return revenueService.exportCsv();
    }

}
