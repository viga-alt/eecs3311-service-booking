package com.booking.domain.state;

import com.booking.domain.Booking;

// Terminal state
// No further actions are allowed after cancelling
public class CancelledState implements BookingState {

  // Any attempt to change the state in this terminal state simply logs an error
  // TODO: implement this

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
    return "CANCELLED";
  }
}
