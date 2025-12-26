package com.aanchal.trip_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Data; // <--- @Data covers @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;

// DTO (Data Transfer Object) for sending trip summary statistics
@Data
@NoArgsConstructor // Lombok Setter/Getter
@AllArgsConstructor // Lombok Constructor
public class TripSummaryDTO {

    // Assignment requirements: total trips, minimum price, maximum price, average price.
    private Long totalTrips;
    private Double minPrice;
    private Double maxPrice;
    private Double averagePrice;
    private Long pendingTrips;

}
