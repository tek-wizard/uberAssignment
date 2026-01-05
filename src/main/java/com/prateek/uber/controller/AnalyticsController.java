package com.prateek.uber.controller;

import com.prateek.uber.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // driver performance stats
    @GetMapping("/driver/{driverId}/summary")
    public Object getDriverSummary(@PathVariable String driverId) {
        return analyticsService.getDriverSummary(driverId);
    }

    // user spending history
    @GetMapping("/user/{userId}/spending")
    public Object getUserSpending(@PathVariable String userId) {
        return analyticsService.getUserSpending(userId);
    }

    // breakdown of rides by current status
    @GetMapping("/status-summary")
    public Object getStatusSummary() {
        return analyticsService.getRidesByStatus();
    }

    // timeline view
    @GetMapping("/rides-per-day")
    public Object getRidesPerDay() {
        return analyticsService.getRidesPerDay();
    }
}