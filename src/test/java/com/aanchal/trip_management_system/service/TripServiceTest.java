package com.aanchal.trip_management_system.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any; // Important fix for sorting logic
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.aanchal.trip_management_system.model.Trip;
import com.aanchal.trip_management_system.model.TripStatus;
import com.aanchal.trip_management_system.model.TripSummaryDTO;
import com.aanchal.trip_management_system.repository.TripRepository;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    TripRepository tripRepository;

    @InjectMocks
    TripService tripService;

    private Trip sampleTrip;
    private Integer sampleId = 1;

    @BeforeEach
    void setup() {
        // This relies on the 6-argument constructor in Trip.java
        sampleTrip = new Trip(sampleId, "Paris", LocalDate.now(), LocalDate.now().plusDays(5), 500.0, TripStatus.PLANNED);
    }

    // ==========================================================
    // 1. Basic CRUD Tests
    // ==========================================================

    @Test
    void shouldCreateTripSuccessfully() {
        when(tripRepository.save(any(Trip.class))).thenReturn(sampleTrip);
        
        Trip created = tripService.createTrip(sampleTrip);
        
        assertNotNull(created);
        assertEquals("Paris", created.getDestination());
        verify(tripRepository, times(1)).save(sampleTrip);
    }

    @Test
    void shouldGetTripByIdWhenTripExists() {
        when(tripRepository.findById(sampleId)).thenReturn(Optional.of(sampleTrip));
        
        Trip found = tripService.getTripById(sampleId);
        
        assertEquals(sampleTrip.getDestination(), found.getDestination());
    }

    @Test
    void shouldThrowExceptionWhenTripDoesNotExist() {
        when(tripRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            tripService.getTripById(999);
        });
    }
    
    // ==========================================================
    // 2. Update and Delete Tests (with Validation)
    // ==========================================================

    @Test
    void shouldUpdateTripSuccessfully() {
        Trip validUpdate = new Trip(sampleId, "New York", LocalDate.now().plusDays(1), LocalDate.now().plusDays(11), 600.0, TripStatus.ONGOING);

        when(tripRepository.findById(sampleId)).thenReturn(Optional.of(sampleTrip));
        when(tripRepository.save(any(Trip.class))).thenReturn(validUpdate);

        Trip result = tripService.updateTrip(sampleId, validUpdate);

        assertEquals("New York", result.getDestination());
        assertEquals(TripStatus.ONGOING, result.getStatus());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    void shouldThrowExceptionForInvalidDatesOnUpdate() {
        // Test Rule: End Date cannot be before Start Date
        Trip invalidUpdate = new Trip(sampleId, "Invalid Update", LocalDate.now().plusDays(10), LocalDate.now().plusDays(5), 600.0, TripStatus.PLANNED);

        when(tripRepository.findById(sampleId)).thenReturn(Optional.of(sampleTrip));

        assertThrows(IllegalArgumentException.class, () -> {
            tripService.updateTrip(sampleId, invalidUpdate);
        }, "End date cannot be before start date.");
        
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldDeleteTripSuccessfully() {
        tripService.deleteTrip(sampleId);
        
        verify(tripRepository, times(1)).deleteById(sampleId);
    }

    // ==========================================================
    // 3. Search and Filter Tests
    // ==========================================================

    @Test
    void shouldSearchByDestinationAndStatusWithSorting() {
        String destination = "Lon";
        TripStatus status = TripStatus.PLANNED;
        String sortBy = "price";
        String sortDirection = "ASC";
        
        // Using Direction.fromString which requires 'import org.springframework.data.domain.Sort.Direction;'
        Sort sort = Sort.by(Direction.fromString(sortDirection), sortBy);

        Trip trip1 = new Trip(1, "London Trip", LocalDate.now(), LocalDate.now().plusDays(5), 100.0, TripStatus.PLANNED);
        List<Trip> expectedTrips = Arrays.asList(trip1);

        when(tripRepository.findByDestinationContainingIgnoreCaseAndStatus(destination, status, sort))
            .thenReturn(expectedTrips);

        List<Trip> result = tripService.searchAndSortTrips(destination, status, sortBy, sortDirection);

        assertEquals(1, result.size());
        assertEquals("London Trip", result.get(0).getDestination());
    }

    @Test
    void shouldFindTripsBetweenDatesSuccessfully() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        
        Trip trip1 = new Trip(1, "Paris", LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 25), 1000.0, TripStatus.PLANNED);
        List<Trip> expectedTrips = Arrays.asList(trip1);

        when(tripRepository.findByStartDateBetween(startDate, endDate)).thenReturn(expectedTrips);

        List<Trip> result = tripService.getTripsBetweenDates(startDate, endDate);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsAfterEndDateInDateRange() {
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            tripService.getTripsBetweenDates(startDate, endDate);
        }, "Start date cannot be after end date.");
        
        verify(tripRepository, never()).findByStartDateBetween(any(), any());
    }
    
    // ==========================================================
    // 4. Summary Test
    // ==========================================================

    @Test
    void shouldCalculateTripSummary() {
        // Mock the four individual Repository calls for summary statistics
        when(tripRepository.countAllTrips()).thenReturn(3L);
        when(tripRepository.findMinPrice()).thenReturn(100.0);
        when(tripRepository.findMaxPrice()).thenReturn(300.0);
        when(tripRepository.findAveragePrice()).thenReturn(200.0);

        TripSummaryDTO result = tripService.getTripSummary();

        // Assertion
        assertEquals(3L, result.getTotalTrips());
        assertEquals(100.0, result.getMinPrice());
        assertEquals(300.0, result.getMaxPrice());
        assertEquals(200.0, result.getAveragePrice());

        // Verification: Check that all four repository methods were called
        verify(tripRepository, times(1)).countAllTrips();
        verify(tripRepository, times(1)).findMinPrice();
        verify(tripRepository, times(1)).findMaxPrice();
        verify(tripRepository, times(1)).findAveragePrice();
    }
}