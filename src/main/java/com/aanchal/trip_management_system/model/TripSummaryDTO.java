package com.aanchal.trip_management_system.model;

public class TripSummaryDTO {

    private long totalTrips;
    private long pendingTrips;

    // Default Constructor
    public TripSummaryDTO() {
    }

    // Parameterized Constructor
    public TripSummaryDTO(long totalTrips, long pendingTrips) {
        this.totalTrips = totalTrips;
        this.pendingTrips = pendingTrips;
    }

    // Getters and Setters
    public long getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(long totalTrips) {
        this.totalTrips = totalTrips;
    }

    public long getPendingTrips() {
        return pendingTrips;
    }

    public void setPendingTrips(long pendingTrips) {
        this.pendingTrips = pendingTrips;
    }
}
