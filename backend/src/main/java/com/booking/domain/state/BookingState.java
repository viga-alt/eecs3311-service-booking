package com.booking.domain.state;

import com.booking.domain.Booking;

/* STATE PATTERN
 * BookingState interface
 * Concrete states encapsulate behavior for lifecycle phase
 * Booking is context that delegates lifecycle calls in its currentState
 */
public interface BookingState {
  // Methods to change between different Booking states
  // Each concrete state class implements methods to perform state specific actions
  void confirm(Booking booking);

  void reject(Booking booking);

  void markPendingPayment(Booking booking);

  void markPaid(Booking booking);

  void cancel(Booking booking);

  void complete(Booking booking);

  // Return name of state
  String getStateName();
}
