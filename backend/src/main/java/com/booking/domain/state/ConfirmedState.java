package com.booking.domain.state;

import com.booking.domain.Booking;

/* STATE PATTERN
 * Consultant accepted booking, await payment setup
 * Valid transitions: markPendingPayment -> PendingPaymentState, cancel -> CancelledState
 */
public class ConfirmedState implements BookingState {

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
    return "CONFIRMED";
  }
}
