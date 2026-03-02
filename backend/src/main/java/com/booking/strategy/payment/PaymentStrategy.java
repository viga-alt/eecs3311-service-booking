package com.booking.strategy.payment;

import com.booking.domain.Payment;
import com.booking.domain.PaymentMethod;

/* STRATEGY PATTERN - Payment strategy interface
 * Different payment methods implement this interface, each with their own validation logic and simulated processing behavior
 */
public interface PaymentStrategy {
    boolean validate(PaymentMethod method);
    Payment process(double amount, PaymentMethod method, int bookingId);
    String getMethodName();
}
