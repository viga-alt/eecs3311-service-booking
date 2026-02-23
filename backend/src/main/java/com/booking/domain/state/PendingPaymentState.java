package com.booking.domain.state;

import com.booking.domain.Booking;

/*
 * Booking confirmed, still awaiting payment from client
 * Valid transitions:
 *      markPaid -> PaidState,
 *      cancel -> CancelledState
 */
public class PendingPaymentState implements BookingState {

  @Override
  public void confirm(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already confirmed.");
  }

  @Override
  public void reject(Booking booking) {
    System.out.println("[State] Cannot reject — booking is confirmed and awaiting payment.");
  }

  @Override
  public void markPendingPayment(Booking booking) {
    System.out.println(
        "[State] Booking #" + booking.getId() + " is already in Pending Payment state.");
  }

  @Override
  public void markPaid(Booking booking) {
    System.out.println("[State] Payment received for booking #" + booking.getId() + ".");
    booking.setState(new PaidState());
    booking.notifyObservers("PAYMENT_COMPLETED");
  }

  @Override
  public void cancel(Booking booking) {
    System.out.println(
        "[State] Booking #" + booking.getId() + " cancelled during pending payment.");
    booking.setState(new CancelledState());
    booking.notifyObservers("BOOKING_CANCELLED");
  }

  @Override
  public void complete(Booking booking) {
    System.out.println("[State] Cannot complete — payment not yet processed.");
  }

  @Override
  public String getStateName() {
    return "PENDING_PAYMENT";
  }
}
