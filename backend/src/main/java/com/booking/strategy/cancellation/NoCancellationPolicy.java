package com.booking.strategy.cancellation;

import com.booking.domain.Booking;
import com.booking.domain.Payment;

/*
 * STRATEGY PATTERN
 * No-cancellation policy
 * Cancellations are not permitted once the booking is confirmed
 * Only allowed in the REQUESTED state
 */
public class NoCancellationPolicy implements CancellationPolicy {

  @Override
  public boolean canCancel(Booking booking) {
    String stateName = booking.getCurrentStateName();
    // Only allow cancellation if still in REQUESTED state
    return "REQUESTED".equals(stateName);
  }

  @Override
  public double calculateRefund(Payment payment) {
    return 0.0; // No refund under any circumstances
  }

  @Override
  public String getPolicyName() {
    return "NO_CANCELLATION";
  }
}
