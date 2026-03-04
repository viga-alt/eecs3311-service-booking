package com.booking.strategy.payment;

import com.booking.domain.Payment;
import com.booking.domain.PaymentMethod;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/* STRATEGY PATTERN - Bank Transfer payment processing
 * Validates: account number (8-17 digits), routing number (9 digits)
 */
public class BankTransferStrategy implements PaymentStrategy {
    // Simple id generator for payments produced by this strategy, thread-safe atomic, starts at 4000
    private static final AtomicInteger idGen = new AtomicInteger(4000);

    // Validate the required details on the PaymentMethod
    @Override
    public boolean validate(PaymentMethod method) {
        String accountNumber = method.getDetail("accountNumber");
        String routingNumber = method.getDetail("routingNumber");

        // Account number must exist and contain 8‑17 digits
        if (accountNumber == null || !accountNumber.matches("\\d{8,17}")) {
            System.out.println("[BankTransfer] Invalid account number (must be 8-17 digits).");
            return false;
        }
        // Routing number must exist and contain exactly 9 digits
        if (routingNumber == null || !routingNumber.matches("\\d{9}")) {
            System.out.println("[BankTransfer] Invalid routing number (must be exactly 9 digits).");
            return false;
        }
        return true;    // All checks passed
    }

    // Simulate processing a bank transfer
    @Override
    public Payment process(double amount, PaymentMethod method, int bookingId) {
        System.out.println("[BankTransfer] Simulating bank transfer...");
        
        // Pretend to wait for a few seconds as if talking to a bank (this just makes it look cooler)
        simulateDelay();

        // Create a fake transaction id
        String txId = "BT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create and mark the payment as successful
        Payment payment = new Payment(idGen.getAndIncrement(), amount, txId, "BANK_TRANSFER", bookingId);
        payment.markSuccess();
        
        System.out.println("[BankTransfer] Payment successful. Transaction ID: " + txId);
        return payment;
    }

    @Override
    public String getMethodName() { return "BANK_TRANSFER"; }

    // Helper that sleeps a few seconds to mimic real processing time (very cool)
    private void simulateDelay() {
        try { 
            Thread.sleep(3000); // 3 second pause
        } catch (InterruptedException ignored) {
            // Intentionally ignored since this is just a demo
        }
    }
}
