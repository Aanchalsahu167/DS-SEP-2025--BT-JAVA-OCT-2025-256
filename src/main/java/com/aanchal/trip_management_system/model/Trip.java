package com.aanchal.trip_management_system.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    // Default Constructor (JPA ke liye zaroori)
    public Trip() {}

    // Constructor for Tests and Service Layer (Iska hona zaroori hai!)
    public Trip(Integer id, String destination, LocalDate startDate, LocalDate endDate, Double price, TripStatus status) {
        this.id = id;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.status = status;
    }
    
    // Getters and Setters (Agar ye code mein missing the toh copy karein)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }
}