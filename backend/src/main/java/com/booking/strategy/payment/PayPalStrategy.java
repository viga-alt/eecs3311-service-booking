package com.booking.strategy.payment;

import com.booking.domain.Payment;
import com.booking.domain.PaymentMethod;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/* STRATEGY PATTERN - PayPal payment processing
 * Validates: valid email address format
 */
public class PayPalStrategy implements PaymentStrategy {
    // Simple id generator for payments created by this strategy, thread-safe atomic (start at 3000)
    private static final AtomicInteger idGen = new AtomicInteger(3000);

    // Validate the required fields of the PaymentMethod
    @Override
    public boolean validate(PaymentMethod method) {
        String email = method.getDetail("email");
        // Email must include proper characters (has to have @, no special characters, etc.)
        if (email == null || !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("[PayPal] Invalid email address: '" + email + "'");
            return false;
        }
        return true;
    }

    // Simulate processing a credit‑card payment
    @Override
    public Payment process(double amount, PaymentMethod method, int bookingId) {
        System.out.println("[PayPal] Simulating PayPal payment...");
        
        // Pretend to wait as if talking to a card‑processor (this just makes it cooler)
        simulateDelay();
        
        // Create a fake transaction id
        String txId = "PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Build and mark the payment as successful
        Payment payment = new Payment(idGen.getAndIncrement(), amount, txId, "PAYPAL", bookingId);
        payment.markSuccess();
        
        System.out.println("[PayPal] Payment successful. Transaction ID: " + txId);
        return payment;
    }

    // Return the unique name of this payment method
    @Override
    public String getMethodName() { 
        return "PAYPAL";
    }

    // Helper that sleeps a bit to mimic real processing latency
    private void simulateDelay() {
        try { 
            Thread.sleep(2500); // 2.5 second pause
        } catch (InterruptedException ignored) {
            // Intentionally ignored cause this is a demo only
        }
    }
}
