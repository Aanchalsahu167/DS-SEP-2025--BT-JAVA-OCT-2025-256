package com.aanchal.trip_management_system.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aanchal.trip_management_system.model.Trip;
import com.aanchal.trip_management_system.model.TripStatus;
import com.aanchal.trip_management_system.repository.TripRepository;

@Controller
@RequestMapping("/trips")
public class TripController {

    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    @Autowired
    private TripRepository tripRepository;

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("trip", new Trip());
        return "add-trip";
    }

    @PostMapping("/save")
    public String saveTrip(@ModelAttribute("trip") Trip trip) {
        try {
            tripRepository.save(trip);
            return "redirect:/trips";
        } catch (Exception e) {
            logger.error("Error saving trip", e);
            return "error";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTrip(@PathVariable("id") Integer id) {
        tripRepository.deleteById(id);
        return "redirect:/trips";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid trip Id:" + id));
        model.addAttribute("trip", trip);
        return "add-trip";
    }

    @PostMapping("/update/{id}")
    public String updateTrip(@PathVariable("id") Integer id, @ModelAttribute("trip") Trip trip) {
        trip.setId(id);
        tripRepository.save(trip);
        return "redirect:/trips";
    }

    @GetMapping
    public String getAllTrips(Model model) {
        List<Trip> trips = tripRepository.findAll();

        long totalTrips = trips.size();
        long completedTrips = trips.stream().filter(t -> t.getStatus() == TripStatus.COMPLETED).count();
        double totalBudget = trips.stream().mapToDouble(Trip::getPrice).sum();

        model.addAttribute("trips", trips);
        model.addAttribute("totalTrips", totalTrips);
        model.addAttribute("completed", completedTrips);
        model.addAttribute("totalBudget", totalBudget);

        return "trip-list";
    }

    @GetMapping("/search")
    public String searchTrips(@RequestParam("keyword") String keyword, Model model) {
        List<Trip> trips = tripRepository.findByNameContainingIgnoreCaseOrDestinationContainingIgnoreCase(keyword, keyword);
        double totalBudget = trips.stream().mapToDouble(Trip::getPrice).sum();
        model.addAttribute("trips", trips);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalBudget", totalBudget);
        return "trip-list";
    }
}
