package com.booking.domain.state;

import com.booking.domain.Booking;

/*
 * Terminal state
 * Consultant declined the booking, no changes to state allowed
 */
public class RejectedState implements BookingState {

  // TODO: implement this class
  // attempting to modify this state should create an error

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
    return "REJECTED";
  }
}
