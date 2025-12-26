package com.aanchal.trip_management_system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.aanchal.trip_management_system.model.Trip;
import com.aanchal.trip_management_system.model.TripStatus;
import com.aanchal.trip_management_system.model.TripSummaryDTO;
import com.aanchal.trip_management_system.repository.TripRepository;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    // 1. Create Trip
    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    // 2. Get All Trips Paginated and Sorted
    public Page<Trip> getAllTripsPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return tripRepository.findAll(pageable);
    }

    // 3. Get Trip by ID
    public Trip getTripById(Integer id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trip not found with id: " + id));
    }

    // 4. Update Trip 
    public Trip updateTrip(Integer id, Trip tripDetails) {
        Trip existingTrip = getTripById(id);

        existingTrip.setDestination(tripDetails.getDestination());
        existingTrip.setStartDate(tripDetails.getStartDate());
        existingTrip.setEndDate(tripDetails.getEndDate());
        existingTrip.setStatus(tripDetails.getStatus());

        return tripRepository.save(existingTrip);
    }

    // 5. Delete Trip
    public void deleteTrip(Integer id) {
        if (!tripRepository.existsById(id)) {
            throw new NoSuchElementException("Trip not found with id: " + id);
        }
        tripRepository.deleteById(id);
    }

    // 6. Search and Sort 
    public List<Trip> searchAndSortTrips(String destination, TripStatus status, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return tripRepository.findAll(sort);
    }

    // 7. Get Trip Summary (Type is correctly Long)
    public TripSummaryDTO getTripSummary() {
        Long totalTrips = tripRepository.count();
        Long pendingTripsCount = 0L;

        TripSummaryDTO summary = new TripSummaryDTO();

        summary.setTotalTrips(totalTrips);
        summary.setPendingTrips(pendingTripsCount);

        return summary;
    }

    // 8. FINAL FIX: Renamed from getTripBetweenDates to getTripsBetweenDates
    public List<Trip> getTripsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return tripRepository.findAll();
    }
}
