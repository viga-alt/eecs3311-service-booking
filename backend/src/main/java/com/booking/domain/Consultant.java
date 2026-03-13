package com.booking.domain;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

// Represents a consultant who offers and manages consulting services
public class Consultant extends User {
  private String specialization; // Consultant's specialization / area of expertise
  private boolean isApproved; // Approval status set by Admin
  private List<TimeSlot> availableSlots; // Slots available for booking
  private List<Service> offeredServices; // Services the consultant offers

  public Consultant(int id, String name, String email, String password, String specialization) {
    super(id, name, email, password, "CONSULTANT");
    this.specialization = specialization;
    this.isApproved = false; // Must be approved by Admin, false default
    this.availableSlots = new ArrayList<>();
    this.offeredServices = new ArrayList<>();
  }

  /* Manage Availability */

  // Add a new available time slot
  public void addAvailableSlot(TimeSlot slot) {
    availableSlots.add(slot);
    System.out.println("[Consultant] Slot added: " + slot);
  }

  // Remove a slot by its ID
  public void removeAvailableSlot(int slotId) {
    availableSlots.removeIf(s -> s.getId() == slotId);
  }

  // Replace entire availability list
  public void manageAvailability(List<TimeSlot> slots) {
    this.availableSlots = new ArrayList<>(slots);
    System.out.println("[Consultant] Availability updated with " + slots.size() + " slot(s).");
  }

  // Get only currently available slots
  public List<TimeSlot> getAvailableSlots() {
    return availableSlots.stream()
        .filter(TimeSlot::isAvailable)
        .collect(java.util.stream.Collectors.toList());
  }

  // Find a slot by its slot ID
  public TimeSlot findSlotById(int slotId) {
    return availableSlots.stream().filter(s -> s.getId() == slotId).findFirst().orElse(null);
  }

  /* Service management */

  // Add a new service to the consultant's offerings
  public void addService(Service service) {
    offeredServices.add(service);
  }

  // Retrieve a copy of offered services
  public List<Service> getOfferedServices() {
    return new ArrayList<>(offeredServices);
  }

  /* Approval */

  // Approve the consultant (Admin action)
  public void approve() {
    this.isApproved = true;
    System.out.println("[Consultant] " + name + " has been approved.");
  }

  // Reject the consultant (Admin action)
  public void reject() {
    this.isApproved = false;
    System.out.println("[Consultant] " + name + "'s registration was rejected.");
  }

  // Check approval status
  public boolean isApproved() {
    return isApproved;
  }

  // Getter
  public String getSpecialization() {
    return specialization;
  }

  @Override
  public String toString() {
    return String.format(
        "[Consultant] %s (id=%d, email=%s, specialization=%s, approved=%b)",
        name, id, email, specialization, isApproved);
  }
}
