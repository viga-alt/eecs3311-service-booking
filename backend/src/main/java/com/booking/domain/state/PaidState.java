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

  @Override
  public void confirm(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already confirmed and paid.");
  }

  @Override
  public void reject(Booking booking) {
    System.out.println("[State] Cannot reject — booking is already paid.");
  }

  @Override
  public void markPendingPayment(Booking booking) {
    System.out.println("[State] Payment already completed for booking #" + booking.getId() + ".");
  }

  @Override
  public void markPaid(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " is already paid.");
  }

  @Override
  public void cancel(Booking booking) {
    System.out.println(
        "[State] Booking #" + booking.getId() + " cancelled after payment (refund may apply).");
    booking.setState(new CancelledState());
    booking.notifyObservers("BOOKING_CANCELLED_AFTER_PAYMENT");
  }

  @Override
  public void complete(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " marked as completed.");
    booking.setState(new CompletedState());
    booking.notifyObservers("BOOKING_COMPLETED");
  }

  @Override
  public String getStateName() {
    return "PAID";
  }
}
