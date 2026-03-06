package com.booking.domain.state;

import com.booking.domain.Booking;

/*
 * Initial state when a client submits a booking request
 * Valid transitions:
 *      confirm -> ConfirmedState,
 *      reject -> RejectedState,
 *      cancel -> CancelledState
 */
public class RequestedState implements BookingState {

  // TODO: implement this class

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
    return "REQUESTED";
  }
}
