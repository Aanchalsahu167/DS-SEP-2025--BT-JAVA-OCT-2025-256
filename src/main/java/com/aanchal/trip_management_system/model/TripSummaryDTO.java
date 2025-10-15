package com.aanchal.trip_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO (Data Transfer Object) for sending trip summary statistics
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSummaryDTO {
    
    // Assignment requirements: Returns total trips, minimum price, maximum price, average price.
    private Long totalTrips;
    private Double minPrice;
    private Double maxPrice;
    private Double averagePrice;
}
