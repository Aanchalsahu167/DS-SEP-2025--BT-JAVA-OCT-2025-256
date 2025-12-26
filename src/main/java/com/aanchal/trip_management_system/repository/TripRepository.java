package com.aanchal.trip_management_system.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aanchal.trip_management_system.model.Trip;
import com.aanchal.trip_management_system.model.TripStatus;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {

    // 1. Search and Sort Method 
    List<Trip> findByDestinationContainingIgnoreCaseAndStatus(String destination, TripStatus status, Sort sort);

    // 2. Date Range Search Method
    List<Trip> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // 3. Summary Queries (Used by TripServiceTest for mocking)
    @Query("SELECT COUNT(t) FROM Trip t")
    Long countAllTrips();

    @Query("SELECT MIN(t.price) FROM Trip t")
    Double findMinPrice();

    @Query("SELECT MAX(t.price) FROM Trip t")
    Double findMaxPrice();

    @Query("SELECT AVG(t.price) FROM Trip t")
    Double findAveragePrice();
}
