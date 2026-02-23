package com.booking.domain.state;

import com.booking.domain.Booking;

// Terminal state
// Booking is completed, no further actions are allowed
public class CompletedState implements BookingState {

  // Attempts to change this state should log an error
  @Override
  public void confirm(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already completed.");
  }

  @Override
  public void reject(Booking booking) {
    System.out.println(
        "[State] Cannot reject — booking #" + booking.getId() + " is already completed.");
  }

  @Override
  public void markPendingPayment(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already completed.");
  }

  @Override
  public void markPaid(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already completed.");
  }

  @Override
  public void cancel(Booking booking) {
    System.out.println(
        "[State] Cannot cancel — booking #" + booking.getId() + " is already completed.");
  }

  @Override
  public void complete(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already completed.");
  }

  @Override
  public String getStateName() {
    return "COMPLETED";
  }
}
