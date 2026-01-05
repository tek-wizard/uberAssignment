package com.prateek.uber.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.prateek.uber.dto.RideRequest;
import com.prateek.uber.model.Ride;
import com.prateek.uber.service.RideService;
import com.prateek.uber.service.RideSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final RideSearchService searchService;

    // Basic ->

    @PostMapping("/rides")
    public ResponseEntity<Ride> createRide(@RequestBody @Valid RideRequest request) {
        return ResponseEntity.ok(rideService.createRide(request));
    }

    @GetMapping("/user/rides")
    public ResponseEntity<List<Ride>> getUserRides() {
        return ResponseEntity.ok(rideService.getUserRides());
    }

    @GetMapping("/driver/rides/requests")
    public ResponseEntity<List<Ride>> getPendingRides() {
        return ResponseEntity.ok(rideService.getPendingRides());
    }

    @PostMapping("/driver/rides/{rideId}/accept")
    public ResponseEntity<Ride> acceptRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.acceptRide(rideId));
    }

    @PostMapping("/rides/{rideId}/complete")
    public ResponseEntity<Ride> completeRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    // Search & Filters ->

    @GetMapping("/search")
    public ResponseEntity<List<Ride>> search(@RequestParam String text) {
        return ResponseEntity.ok(searchService.searchRides(text));
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<List<Ride>> advancedSearch(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(searchService.advancedSearch(status, text, page, size));
    }

    @GetMapping("/filter-status")
    public ResponseEntity<List<Ride>> filterByStatus(
            @RequestParam String status,
            @RequestParam String text) {
        return ResponseEntity.ok(searchService.advancedSearch(status, text, 0, 100));
    }

    @GetMapping("/filter-distance")
    public ResponseEntity<List<Ride>> filterDistance(@RequestParam Double min, @RequestParam Double max) {
        return ResponseEntity.ok(searchService.filterByDistance(min, max));
    }

    @GetMapping("/filter-date-range")
    public ResponseEntity<List<Ride>> filterDate(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return ResponseEntity.ok(searchService.filterByDate(start, end));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Ride>> getRidesOnDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(searchService.getRidesByDate(date));
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Ride>> sortByFare(@RequestParam String order) {
        return ResponseEntity.ok(searchService.sortByFare(order));
    }

    // ID BASED ->

    // get rides for a specific user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ride>> getRidesByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(searchService.getRidesByFieldAndStatus("userId", userId, null));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<Ride>> getUserRidesByStatus(
            @PathVariable String userId, @PathVariable String status) {
        return ResponseEntity.ok(searchService.getRidesByFieldAndStatus("userId", userId, status));
    }

    // get active rides (accepted) for a driver
    @GetMapping("/driver/{driverId}/active-rides")
    public ResponseEntity<List<Ride>> getDriverActiveRides(@PathVariable String driverId) {
        return ResponseEntity.ok(searchService.getRidesByFieldAndStatus("driverId", driverId, "ACCEPTED"));
    }
}