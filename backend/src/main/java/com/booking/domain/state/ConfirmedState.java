package com.booking.domain.state;

import com.booking.domain.Booking;

/* STATE PATTERN
 * Consultant accepted booking, await payment setup
 * Valid transitions: markPendingPayment -> PendingPaymentState, cancel -> CancelledState
 */
public class ConfirmedState implements BookingState {

  @Override
  public void confirm(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already confirmed.");
  }

  @Override
  public void reject(Booking booking) {
    System.out.println("[State] Cannot reject — booking is already confirmed.");
  }

  @Override
  public void markPendingPayment(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " awaiting payment.");
    booking.setState(new PendingPaymentState());
    booking.notifyObservers("BOOKING_PENDING_PAYMENT");
  }

  @Override
  public void markPaid(Booking booking) {
    System.out.println("[State] Must enter PendingPayment before marking Paid.");
  }

  @Override
  public void cancel(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " cancelled after confirmation.");
    booking.setState(new CancelledState());
    booking.notifyObservers("BOOKING_CANCELLED");
  }

  @Override
  public void complete(Booking booking) {
    System.out.println("[State] Cannot complete — payment not yet processed.");
  }

  @Override
  public String getStateName() {
    return "CONFIRMED";
  }
}
