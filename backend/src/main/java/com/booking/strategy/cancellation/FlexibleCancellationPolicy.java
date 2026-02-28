package com.booking.strategy.cancellation;

import com.booking.domain.Booking;
import com.booking.domain.Payment;

/*
 * STRATEGY PATTERN
 * Flexible cancellation policy
 * We give full refund if cancelled >24 hours before the session
 * 50% refund if cancelled within 24 hours
 * No refund if session is in the past
 */
public class FlexibleCancellationPolicy implements CancellationPolicy {

  @Override
  public boolean canCancel(Booking booking) {
    // Can always cancel, refund depends on timing
    String stateName = booking.getCurrentStateName();
    return !"COMPLETED".equals(stateName)
        && !"CANCELLED".equals(stateName)
        && !"REJECTED".equals(stateName);
  }

  @Override
  public double calculateRefund(Payment payment) {
    if (payment == null) return 0.0;
    // Simulated: assume booking is cancelled well in advance — full refund
    // In a real system, we'd compare booking's session time with now
    return payment.getAmount(); // 100% refund
  }

  @Override
  public String getPolicyName() {
    return "FLEXIBLE";
  }
}
