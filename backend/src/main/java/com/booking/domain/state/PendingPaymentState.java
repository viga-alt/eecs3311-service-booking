package com.booking.domain.state;

import com.booking.domain.Booking;

/*
 * Booking confirmed, still awaiting payment from client
 * Valid transitions:
 *      markPaid -> PaidState,
 *      cancel -> CancelledState
 */
public class PendingPaymentState implements BookingState {

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
    return "PENDING_PAYMENT";
  }
}
