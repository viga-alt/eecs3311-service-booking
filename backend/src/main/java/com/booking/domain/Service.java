package com.booking.domain;

// Represents a consulting service offered on the platform

public class Service {
  private int id; // Unique service identifier
  private String name; // Service name
  private int durationMinutes; // Duration of the session in minutes
  private double basePrice; // Base price before any adjustments
  private String description; // Brief description of the service

  public Service(int id, String name, int durationMinutes, double basePrice, String description) {
    this.id = id;
    this.name = name;
    this.durationMinutes = durationMinutes;
    this.basePrice = basePrice;
    this.description = description;
  }

  /* Getter Methods */

  public String getDetails() {
    return String.format(
        "Service[id=%d, name='%s', duration=%d min, price=$%.2f, desc='%s']",
        id, name, durationMinutes, basePrice, description);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getDurationMinutes() {
    return durationMinutes;
  }

  public double getBasePrice() {
    return basePrice;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return getDetails();
  }
}
