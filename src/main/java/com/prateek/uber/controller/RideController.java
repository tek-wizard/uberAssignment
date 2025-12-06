package com.prateek.uber.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.prateek.uber.dto.RideRequest;
import com.prateek.uber.model.Ride;
import com.prateek.uber.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

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
}