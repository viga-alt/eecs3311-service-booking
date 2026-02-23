package com.booking.domain.state;

import com.booking.domain.Booking;

/*
 * STATE PATTERN
 * Initial state when a client submits a booking request
 * Valid transitions:
 *      confirm -> ConfirmedState,
 *      reject -> RejectedState,
 *      cancel -> CancelledState
 */
public class RequestedState implements BookingState {

  @Override
  public void confirm(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " confirmed by consultant.");
    booking.setState(new ConfirmedState());
    booking.notifyObservers("BOOKING_CONFIRMED");
  }

  @Override
  public void reject(Booking booking) {
    System.out.println("[State] Booking #" + booking.getId() + " rejected by consultant.");
    booking.setState(new RejectedState());
    booking.notifyObservers("BOOKING_REJECTED");
  }

  @Override
  public void markPendingPayment(Booking booking) {
    System.out.println("[State] Cannot mark pending payment — booking not yet confirmed.");
  }

  @Override
  public void markPaid(Booking booking) {
    System.out.println("[State] Cannot mark paid — booking not yet confirmed.");
  }

  @Override
  public void cancel(Booking booking) {
    System.out.println(
        "[State] Booking #" + booking.getId() + " cancelled while in Requested state.");
    booking.setState(new CancelledState());
    booking.notifyObservers("BOOKING_CANCELLED");
  }

  @Override
  public void complete(Booking booking) {
    System.out.println("[State] Cannot complete — booking not yet confirmed or paid.");
  }

  @Override
  public String getStateName() {
    return "REQUESTED";
  }
}
