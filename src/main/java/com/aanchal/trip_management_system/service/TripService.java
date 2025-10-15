package com.aanchal.trip_management_system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.aanchal.trip_management_system.model.Trip;
import com.aanchal.trip_management_system.model.TripStatus;
import com.aanchal.trip_management_system.model.TripSummaryDTO;
import com.aanchal.trip_management_system.repository.TripRepository;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    // 1. Create Trip
    public Trip createTrip(Trip trip) {
        // Validation check for dates
        if (trip.getStartDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        return tripRepository.save(trip);
    }

    // 2. Get Trip by ID
    public Trip getTripById(Integer id) {
        return tripRepository.findById(id)
                             .orElseThrow(() -> new NoSuchElementException("Trip not found with ID: " + id));
    }
    
    // 3. Update Trip
    public Trip updateTrip(Integer id, Trip updatedTripDetails) {
        Trip trip = tripRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("Trip not found with ID: " + id));

        // Validation check for dates
        if (updatedTripDetails.getStartDate().isAfter(updatedTripDetails.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        // Update fields
        trip.setDestination(updatedTripDetails.getDestination());
        trip.setStartDate(updatedTripDetails.getStartDate());
        trip.setEndDate(updatedTripDetails.getEndDate());
        trip.setPrice(updatedTripDetails.getPrice());
        trip.setStatus(updatedTripDetails.getStatus());

        return tripRepository.save(trip);
    }

    // 4. Delete Trip
    public void deleteTrip(Integer id) {
        tripRepository.deleteById(id);
    }

    // 5. Search and Sort (THE FIX IS HERE)
    public List<Trip> searchAndSortTrips(String destination, TripStatus status, String sortBy, String sortDirection) {
        // 1. Sort Object Creation
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), sortBy);

        // 2. Repository Call (This call matches the method in TripRepository.java)
        return tripRepository.findByDestinationContainingIgnoreCaseAndStatus(destination, status, sort);
    }

    // 6. Find Trips Between Dates
    public List<Trip> getTripsBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        return tripRepository.findByStartDateBetween(startDate, endDate);
    }

    // 7. Calculate Trip Summary
    public TripSummaryDTO getTripSummary() {
        Long totalTrips = tripRepository.countAllTrips();
        Double minPrice = tripRepository.findMinPrice();
        Double maxPrice = tripRepository.findMaxPrice();
        Double averagePrice = tripRepository.findAveragePrice();

        // Handle case where no trips exist (to prevent NullPointerException)
        if (totalTrips == null || totalTrips == 0) {
            return new TripSummaryDTO(0L, 0.0, 0.0, 0.0);
        }

        return new TripSummaryDTO(totalTrips, minPrice, maxPrice, averagePrice);
    }
    
    // 8. Method for Controller (If needed, although the final controller code might only use the above)
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }
}