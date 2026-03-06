package com.booking.domain.state;

import com.booking.domain.Booking;

// Terminal state
// Booking is completed, no further actions are allowed
public class CompletedState implements BookingState {

  // TODO: implement this class
  // Attempts to change this state should log an error
  @Override
  public void confirm(Booking booking) {
    return;
  }

  @Override
  public void reject(Booking booking) {
    return;
  }

  @Override
  public void markPendingPayment(Booking booking) {
    return;
  }

  @Override
  public void markPaid(Booking booking) {
    return;
  }

  @Override
  public void cancel(Booking booking) {
    return;
  }

  @Override
  public void complete(Booking booking) {
    return;
  }

  @Override
  public String getStateName() {
    return "COMPLETED";
  }
}
