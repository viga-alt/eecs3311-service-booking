package com.booking.strategy.cancellation;

import com.booking.domain.Booking;
import com.booking.domain.Payment;

/*
 * STRATEGY PATTERN
 * Cancellation policy interface
 * The Admin configures which policy is active via SystemPolicy
 * BookingService enforces whichever policy is currently active
 */
public interface CancellationPolicy {

  // Return whether or not booking can be cancelled
  boolean canCancel(Booking booking);

  // Determine refunded amount based on specific policy
  double calculateRefund(Payment payment);

  String getPolicyName();
}
