package com.aanchal.trip_management_system.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aanchal.trip_management_system.model.Trip;
import com.aanchal.trip_management_system.model.TripStatus;
import com.aanchal.trip_management_system.model.TripSummaryDTO;
import com.aanchal.trip_management_system.service.TripService;

@RestController
@RequestMapping("/api/trips")
public class TripController { // <-- Ensure this class declaration is correct

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // 1. Create Trip (POST /api/trips)
    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {
        try {
            Trip createdTrip = tripService.createTrip(trip);
            return new ResponseEntity<>(createdTrip, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 2. Get Trip by ID (GET /api/trips/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Integer id) {
        try {
            Trip trip = tripService.getTripById(id);
            return new ResponseEntity<>(trip, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // 3. Update Trip (PUT /api/trips/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Integer id, @RequestBody Trip tripDetails) {
        try {
            Trip updatedTrip = tripService.updateTrip(id, tripDetails);
            return new ResponseEntity<>(updatedTrip, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 4. Delete Trip (DELETE /api/trips/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // 5. Get All Trips (GET /api/trips)
    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trips = tripService.getAllTrips();
        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    // 6. Search and Sort (GET /api/trips/search)
    @GetMapping("/search")
    public ResponseEntity<List<Trip>> searchTrips(
            @RequestParam String destination, 
            @RequestParam TripStatus status, 
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        List<Trip> trips = tripService.searchAndSortTrips(destination, status, sortBy, sortDirection);
        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    // 7. Find Trips Between Dates (GET /api/trips/dates)
    @GetMapping("/dates")
    public ResponseEntity<List<Trip>> getTripsBetweenDates(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            List<Trip> trips = tripService.getTripsBetweenDates(startDate, endDate);
            return new ResponseEntity<>(trips, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // 8. Get Trip Summary (GET /api/trips/summary)
    @GetMapping("/summary")
    public ResponseEntity<TripSummaryDTO> getTripSummary() {
        TripSummaryDTO summary = tripService.getTripSummary();
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
}