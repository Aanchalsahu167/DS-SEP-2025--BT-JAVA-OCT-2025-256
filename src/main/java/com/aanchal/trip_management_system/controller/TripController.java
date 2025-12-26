package com.aanchal.trip_management_system.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping; // Rest of the imports using wildcard
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // 1. Create Trip (POST /api/trips)
    @PostMapping
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody Trip trip) {
        Trip savedTrip = tripService.createTrip(trip);
        return new ResponseEntity<>(savedTrip, HttpStatus.CREATED);
    }

    // 2. GET All Trips with Pagination & Sorting (GET /api/trips)
    // Fix: Method Signature को तीन Parameters (int, int, String) के साथ ठीक किया गया।
    @GetMapping 
    public ResponseEntity<Page<Trip>> getAllTrips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        // Service method कॉल तीन Parameters के साथ (जो TripService से मेल खाता है)
        Page<Trip> tripPage = tripService.getAllTripsPaginated(page, size, sortBy);
        
        return new ResponseEntity<>(tripPage, HttpStatus.OK);
    }

    // 3. Get Trip by ID (GET /api/trips/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Integer id) {
        try {
             Trip trip = tripService.getTripById(id);
             return new ResponseEntity<>(trip, HttpStatus.OK);
        } catch (NoSuchElementException e) {
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 4. Update Trip (PUT /api/trips/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Integer id, @Valid @RequestBody Trip tripDetails) {
        try {
            Trip updatedTrip = tripService.updateTrip(id, tripDetails);
            return new ResponseEntity<>(updatedTrip, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 5. Delete Trip (DELETE /api/trips/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
      
    // 7. Get Trip Summary (GET /api/trips/summary)
    @GetMapping("/summary")
    public ResponseEntity<TripSummaryDTO> getTripSummary() {
        TripSummaryDTO summary = tripService.getTripSummary();
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
}