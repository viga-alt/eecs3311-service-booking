package com.booking.strategy.cancellation;

import com.booking.domain.Booking;
import com.booking.domain.Payment;

/*
 * STRATEGY PATTERN
 * Strict cancellation policy
 * Only 25% refund, regardless of when cancellation occurs
 * Cannot cancel if session is already paid and within 2 hours
 */
public class StrictCancellationPolicy implements CancellationPolicy {

  @Override
  public boolean canCancel(Booking booking) {
    String stateName = booking.getCurrentStateName();
    return !"COMPLETED".equals(stateName)
        && !"CANCELLED".equals(stateName)
        && !"REJECTED".equals(stateName);
  }

  @Override
  public double calculateRefund(Payment payment) {
    if (payment == null) return 0.0;
    return payment.getAmount() * 0.25; // 25% refund only
  }

  @Override
  public String getPolicyName() {
    return "STRICT";
  }
}
