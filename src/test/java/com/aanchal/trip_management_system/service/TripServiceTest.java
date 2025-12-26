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
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.aanchal.trip_management_system.model.Trip;
import com.aanchal.trip_management_system.model.TripStatus;
import com.aanchal.trip_management_system.repository.TripRepository;

@ExtendWith({MockitoExtension.class})
class TripServiceTest {

    @Mock
    TripRepository tripRepository;

    @InjectMocks
    TripService tripService;

    // Test Data
    private Trip sampleTrip;
    private final Integer sampleId = 1;

    // Initialize sampleTrip before each test to ensure it's not null
    @BeforeEach
    public void setUp() {
        sampleTrip = new Trip();
        sampleTrip.setId(sampleId);
        sampleTrip.setName("Test Paris Trip");
        sampleTrip.setDestination("Paris, France");
        sampleTrip.setStartDate(LocalDate.now());
        sampleTrip.setEndDate(LocalDate.now().plusDays(7));
        sampleTrip.setPrice(1500.0);
        sampleTrip.setStatus(TripStatus.PLANNED);
    }

    // No additional setup required
    // --- 1. Create Trip Test ---
    @Test
    void shouldCreateTripSuccessfully() {
        // Using any(Trip.class) to prevent Strict stubbing argument mismatch
        when(tripRepository.save(any(Trip.class))).thenReturn(sampleTrip);

        Trip createdTrip = tripService.createTrip(sampleTrip);

        assertNotNull(createdTrip);
        assertEquals(sampleId, createdTrip.getId());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    // --- 2. Get Trip By ID Test ---
    @Test
    void shouldGetTripByIdWhenTripExists() {
        // sampleTrip is now guaranteed not to be null
        when(tripRepository.findById(sampleId)).thenReturn(Optional.of(sampleTrip));

        Trip foundTrip = tripService.getTripById(sampleId);

        assertNotNull(foundTrip);
        assertEquals("Paris, France", foundTrip.getDestination());
    }

    @Test
    void shouldThrowExceptionWhenTripDoesNotExist() {
        when(tripRepository.findById(sampleId)).thenReturn(Optional.empty());

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            tripService.getTripById(sampleId);
        });
        assertNotNull(thrown);
    }

    // --- 3. Update Trip Test ---
    @Test
    void shouldUpdateTripSuccessfully() {
        Trip updatedDetails = new Trip();
        updatedDetails.setDestination("New York, USA");
        updatedDetails.setStartDate(LocalDate.now().plusDays(10));
        updatedDetails.setEndDate(LocalDate.now().plusDays(15));
        updatedDetails.setStatus(TripStatus.COMPLETED);

        when(tripRepository.findById(sampleId)).thenReturn(Optional.of(sampleTrip));
        when(tripRepository.save(any(Trip.class))).thenReturn(sampleTrip);

        Trip updatedTrip = tripService.updateTrip(sampleId, updatedDetails);

        assertEquals("New York, USA", updatedTrip.getDestination());
        assertEquals(TripStatus.COMPLETED, updatedTrip.getStatus());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    // --- 4. Delete Trip Test ---
    @Test
    void shouldDeleteTripSuccessfully() {
        when(tripRepository.existsById(sampleId)).thenReturn(true);
        doNothing().when(tripRepository).deleteById(sampleId);

        tripService.deleteTrip(sampleId);

        verify(tripRepository, times(1)).deleteById(sampleId);
    }

    @Test
    void shouldThrowExceptionWhenTripDoesNotExistOnDelete() {
        when(tripRepository.existsById(sampleId)).thenReturn(false);

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            tripService.deleteTrip(sampleId);
        });
        assertNotNull(thrown);
        verify(tripRepository, never()).deleteById(any());
    }

    // --- 5. Pagination Test ---
    @Test
    void shouldGetAllTripsPaginated() {
        List<Trip> tripList = Arrays.asList(sampleTrip);
        Page<Trip> tripPage = new PageImpl<>(tripList);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(tripRepository.findAll(pageable)).thenReturn(tripPage);

        Page<Trip> result = tripService.getAllTripsPaginated(0, 10, "id");

        assertEquals(1, result.getTotalElements());
        assertEquals("Paris, France", result.getContent().get(0).getDestination());
    }

    // --- 6. Date Range Test ---
    @Test
    void testFindTripsBetweenDatesSuccessfully() {
        List<Trip> expectedList = Arrays.asList(sampleTrip);
        when(tripRepository.findAll()).thenReturn(expectedList);

        List<Trip> result = tripService.getTripsBetweenDates(LocalDate.now().minusDays(1), LocalDate.now().plusDays(10));

        assertEquals(1, result.size());
        verify(tripRepository, times(1)).findAll();
    }
}
