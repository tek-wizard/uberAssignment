package com.prateek.uber.service;

import lombok.RequiredArgsConstructor;
import com.prateek.uber.dto.RideRequest;
import com.prateek.uber.model.Ride;
import com.prateek.uber.model.User;
import com.prateek.uber.repository.RideRepository;
import com.prateek.uber.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Ride createRide(RideRequest request) {
        User user = getCurrentUser();
        Ride ride = new Ride();
        ride.setUserId(user.getId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropLocation(request.getDropLocation());
        ride.setFare(request.getFare());
        ride.setDistanceKm(request.getDistanceKm());
        ride.setStatus("REQUESTED");
        return rideRepository.save(ride);
    }

    public List<Ride> getPendingRides() {
        return rideRepository.findByStatus("REQUESTED");
    }

    public Ride acceptRide(String rideId) {
        User driver = getCurrentUser();
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!"REQUESTED".equals(ride.getStatus())) {
            throw new RuntimeException("Ride is not available");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus("ACCEPTED");
        return rideRepository.save(ride);
    }

    public Ride completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!"ACCEPTED".equals(ride.getStatus())) {
            throw new RuntimeException("Ride must be accepted first");
        }

        ride.setStatus("COMPLETED");
        return rideRepository.save(ride);
    }

    public List<Ride> getUserRides() {
        User user = getCurrentUser();
        return rideRepository.findByUserId(user.getId());
    }
}