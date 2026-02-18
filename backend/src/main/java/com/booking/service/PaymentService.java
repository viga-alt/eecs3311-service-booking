package com.booking.service;

import com.booking.domain.Booking;
import com.booking.domain.Payment;
import com.booking.domain.PaymentMethod;
import com.booking.singleton.DatabaseManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/* Service layer for payment operations
 * Delegates payment processing to PaymentProcessor (Strategy context)
 */
public class PaymentService {
    // Auto‑incrementing ID generator for new payment methods, thread-safe atomic, starts at 500
    private static final AtomicInteger pmIdGen = new AtomicInteger(500);

    private final DatabaseManager db;   // Persistent storage instance (Singleton)
    private final PaymentProcessor processor;   // Context that selects and runs the appropriate payment strategy

    public PaymentService() {
        this.db = DatabaseManager.getInstance();
        this.processor = new PaymentProcessor();
    }

    // Process Payment
    /* Processes payment for a confirmed booking
     * Booking must be in PENDING_PAYMENT state
     * On success, updates booking to PAID state
     */
    public Payment processPayment(Booking booking, PaymentMethod method) {
        // Verify that the booking is in the expected state
        if (!"PENDING_PAYMENT".equals(booking.getCurrentStateName())) {
            System.out.println("[PaymentService] Cannot process payment — booking #"
                    + booking.getId() + " is not in PENDING_PAYMENT state (current: "
                    + booking.getCurrentStateName() + ").");
            return null;
        }

        System.out.println("[PaymentService] Processing payment of $" + booking.getTotalPrice()
                + " via " + method.getType() + "...");

        // Delegate to the strategy context
        Payment payment = processor.executePayment(booking, method);

        // Handle the result of the transaction
        if (payment != null && "SUCCESS".equals(payment.getStatus())) {
            booking.setPayment(payment);
            booking.markPaid();         // Transition booking to PAID state
            db.savePayment(payment);    // Persist the payment record
            db.saveBooking(booking);    // Persist the updated booking
            System.out.println("[PaymentService] Payment successful: " + payment);
        } else {
            System.out.println("[PaymentService] Payment failed for booking #" + booking.getId());
        }

        return payment;
    }

    // View Payment History
    // Retrieves all payments made by a given client (clientId)
    public List<Payment> getPaymentHistory(int clientId) {
        return db.findPaymentsByClientId(clientId);
    }

    // Manage Payment Methods

    // Creates a new payment method, assigns a unique ID, saves it, and returns it 
    public PaymentMethod createPaymentMethod(String type) {
        int id = pmIdGen.getAndIncrement();
        PaymentMethod pm = new PaymentMethod(id, type);
        db.savePaymentMethod(pm);
        return pm;
    }

    // Deletes a payment method by its ID and logs the removal
    public void removePaymentMethod(int methodId) {
        db.deletePaymentMethod(methodId);
        System.out.println("[PaymentService] Payment method #" + methodId + " removed from system.");
    }

    // Refund
    // Issues a refund for an existing payment (sets status to REFUNDED)
    public void issueRefund(int paymentId) {
        Payment payment = db.findPaymentById(paymentId);
        if (payment == null) {
            System.out.println("[PaymentService] Payment #" + paymentId + " not found.");
            return;
        }
        payment.markRefunded();
        db.savePayment(payment);
        System.out.println("[PaymentService] Refund issued for payment #" + paymentId
                + " (amount: $" + payment.getAmount() + ")");
    }

    // Helper method, find a payment by its ID (used by other methods)
    public Payment findPaymentById(int id) {
        return db.findPaymentById(id);
    }
}
