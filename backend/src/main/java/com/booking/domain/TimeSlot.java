package com.booking.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents an available time slot for a consultant
public class TimeSlot {
  private int id; // Unique identifier
  private LocalDateTime startTime; // Slot start time
  private LocalDateTime endTime; // Slot end time
  private boolean isAvailable; // Availability flag

  public TimeSlot(int id, LocalDateTime startTime, LocalDateTime endTime) {
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
    this.isAvailable = true;
  }

  // Set slot as not available or available, respectively

  public void markUnavailable() {
    this.isAvailable = false;
  }

  public void markAvailable() {
    this.isAvailable = true;
  }

  /* Getter Methods */

  public int getId() {
    return id;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  @Override
  public String toString() {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return String.format(
        "TimeSlot[id=%d, %s -> %s, available=%b]",
        id, startTime.format(fmt), endTime.format(fmt), isAvailable);
  }
}
