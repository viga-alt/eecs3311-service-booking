package com.booking.domain.state;

import com.booking.domain.Booking;

/*
 * Payment successfully processed
 * Can now move to completed state
 * Valid transitions:
 *      complete -> CompletedState,
 *      cancel -> CancelledState
 */
public class PaidState implements BookingState {

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
    return "PAID";
  }
}
