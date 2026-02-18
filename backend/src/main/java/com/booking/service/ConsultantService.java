package com.booking.service;

import com.booking.domain.Consultant;
import com.booking.domain.Service;
import com.booking.domain.TimeSlot;
import com.booking.domain.User;
import com.booking.singleton.DatabaseManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

//Service layer for consultant-related operations
public class ConsultantService {
    // Thread‑safe ID generator for new consultant accounts
    private static final AtomicInteger idGen = new AtomicInteger(100);

    private final DatabaseManager db;   // Persistent storage instance (Singleton)

    public ConsultantService() {
        this.db = DatabaseManager.getInstance();
    }

    // Approve Consultant Registration
    // Sets the consultant’s status to approved and persists the change (database)
    public void approveConsultant(int consultantId) {
        User user = db.findUserById(consultantId);
        if (user instanceof Consultant) {
            ((Consultant) user).approve();
            db.saveUser(user);
            System.out.println("[ConsultantService] Consultant #" + consultantId + " approved.");
        } else {
            System.out.println("[ConsultantService] User #" + consultantId + " is not a consultant.");
        }
    }

    // Same as above but rejects instead
    public void rejectConsultantRegistration(int consultantId) {
        User user = db.findUserById(consultantId);
        if (user instanceof Consultant) {
            ((Consultant) user).reject();
            db.saveUser(user);
            System.out.println("[ConsultantService] Consultant #" + consultantId + " registration rejected.");
        }
    }

    // Manage Availability
    // Replaces the consultant’s entire availability list and persists each TimeSlot
    public void updateAvailability(int consultantId, List<TimeSlot> slots) {
        User user = db.findUserById(consultantId);
        if (user instanceof Consultant) {
            Consultant consultant = (Consultant) user;
            consultant.manageAvailability(slots);
            slots.forEach(db::saveTimeSlot);
            db.saveUser(consultant);
        } else {
            System.out.println("[ConsultantService] Consultant #" + consultantId + " not found.");
        }
    }

    // Add a single slot to the consultant’s availability
    public void addSlot(int consultantId, TimeSlot slot) {
        User user = db.findUserById(consultantId);
        if (user instanceof Consultant) {
            ((Consultant) user).addAvailableSlot(slot);
            db.saveTimeSlot(slot);
            db.saveUser(user);
        }
    }

    // Retrieve the list of available time slots for a consultant
    public List<TimeSlot> getAvailableSlots(int consultantId) {
        User user = db.findUserById(consultantId);
        if (user instanceof Consultant) {
            return ((Consultant) user).getAvailableSlots();
        }
        return List.of();
    }

    // Service management
    // Add a new Service to a consultant
    public void addServiceToConsultant(int consultantId, Service service) {
        User user = db.findUserById(consultantId);
        if (user instanceof Consultant) {
            ((Consultant) user).addService(service);
            db.saveService(service);
            db.saveUser(user);
        }
    }

    // Browse consultants
    // Return all approved consultants
    public List<Consultant> getAllApprovedConsultants() {
        return db.findConsultants().stream()
                .map(u -> (Consultant) u)
                .filter(Consultant::isApproved)
                .collect(Collectors.toList());
    }

    // Retrieve consultants whose registration is still pending
    public List<User> getPendingConsultants() {
        return db.findPendingConsultants();
    }

    // Get a single consultant by ID (or null if not a consultant)
    public Consultant getConsultantById(int id) {
        User user = db.findUserById(id);
        return (user instanceof Consultant) ? (Consultant) user : null;
    }

    // Browse all services across platform
    public List<Service> getAllServices() {
        return db.findAllServices();
    }
}
