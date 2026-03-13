package com.booking.domain.state;

import com.booking.domain.Booking;

// Terminal state
// No further actions are allowed after cancelling
public class CancelledState implements BookingState {

  // Any attempt to change the state in this terminal state simply logs an error

  @Override
  public void confirm(Booking booking) {
    System.out.println("[State] Cannot confirm — booking #" + booking.getId() + " was cancelled.");
  }

  @Override
  public void reject(Booking booking) {
    System.out.println(
        "[State] Cannot reject — booking #" + booking.getId() + " was already cancelled.");
  }

  @Override
  public void markPendingPayment(Booking booking) {
    System.out.println("[State] Cannot process payment — booking was cancelled.");
  }

  @Override
  public void markPaid(Booking booking) {
    System.out.println("[State] Cannot mark paid — booking was cancelled.");
  }

  @Override
  public void cancel(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already cancelled.");
  }

  @Override
  public void complete(Booking booking) {
    System.out.println("[State] Cannot complete — booking was cancelled.");
  }

  @Override
  public String getStateName() {
    return "CANCELLED";
  }
}
