package com.booking.strategy.payment;

import com.booking.domain.Payment;
import com.booking.domain.PaymentMethod;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/* STRATEGY PATTERN - Credit Card payment processing
 * Validates: 16-digit card number, future expiry date (MM/YY), 3-4 digit CVV
 */
public class CreditCardStrategy implements PaymentStrategy {
    // Simple id generator for payments created by this strategy, thread-safe atomic (start at 1000)
    private static final AtomicInteger idGen = new AtomicInteger(1000);

    // Validate the required fields of the PaymentMethod
    @Override
    public boolean validate(PaymentMethod method) {
        // Remove any spaces that may be in the card number
        String cardNumber = method.getDetail("cardNumber").replaceAll("\\s", "");
        String expiry = method.getDetail("expiry");   // MM/YY or MM/YYYY
        String cvv = method.getDetail("cvv");

        // Card number must be exactly 16 digits
        if (!cardNumber.matches("\\d{16}")) {
            System.out.println("[CreditCard] Invalid card number (must be 16 digits).");
            return false;
        }
        // CVV must be 3 or 4 digits
        if (!cvv.matches("\\d{3,4}")) {
            System.out.println("[CreditCard] Invalid CVV (must be 3-4 digits).");
            return false;
        }
        // Parse the expiry date and ensure it is in the future
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(expiry.length() == 5 ? "MM/yy" : "MM/yyyy");
            YearMonth exp = YearMonth.parse(expiry, fmt);
            if (exp.isBefore(YearMonth.now())) {
                System.out.println("[CreditCard] Card is expired.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("[CreditCard] Invalid expiry date format. Use MM/YY.");
            return false;
        }
        return true;    // All checks passed
    }

    // Simulate processing a credit‑card payment
    @Override
    public Payment process(double amount, PaymentMethod method, int bookingId) {
        System.out.println("[CreditCard] Simulating payment processing...");
        
        // Pretend to wait as if talking to a card‑processor (this just makes it cooler)
        simulateDelay();
        
        // Create a fake transaction id
        String txId = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Build and mark the payment as successful
        Payment payment = new Payment(idGen.getAndIncrement(), amount, txId, "CREDIT_CARD", bookingId);
        payment.markSuccess();
        
        System.out.println("[CreditCard] Payment successful. Transaction ID: " + txId);
        return payment;
    }

    // Return the unique name of this payment method
    @Override
    public String getMethodName() { 
        return "CREDIT_CARD"; 
    }

    // Helper that sleeps a bit to mimic real processing latency
    private void simulateDelay() {
        try { 
            Thread.sleep(2000); // 2 second pause
        } catch (InterruptedException ignored) {
            // Intentionally ignored cause this is a demo only
        }
    }
}
