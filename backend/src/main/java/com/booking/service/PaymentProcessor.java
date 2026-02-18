package com.booking.service;

import com.booking.domain.Booking;
import com.booking.domain.Payment;
import com.booking.domain.PaymentMethod;
import com.booking.strategy.payment.*;

/* STRATEGY PATTERN - Context for payment processing
 * Selects and executes the appropriate PaymentStrategy based on the payment method type
 */
public class PaymentProcessor {
    private PaymentStrategy strategy;   // The concrete strategy to use for this payment operation

    public PaymentProcessor() {
    }

    public PaymentProcessor(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    // Selects strategy automatically based on payment method type, validates, and processes
    public Payment executePayment(Booking booking, PaymentMethod method) {
        // Auto-select strategy based on method type
        selectStrategyForMethod(method.getType());

        System.out.println("[PaymentProcessor] Using strategy: " + strategy.getMethodName());

        // Validate the payment details before processing
        if (!strategy.validate(method)) {
            System.out.println("[PaymentProcessor] Payment validation failed for method: " + method.getType());
            return null;
        }

        // Perform the actual payment processing
        Payment payment = strategy.process(booking.getTotalPrice(), method, booking.getId());
        return payment;
    }

    // Chooses the appropriate strategy implementation based on the method type
    // The type is compared in a case‑insensitive manner, throws IllegalArgumentException if the type is unknown
    private void selectStrategyForMethod(String type) {
        switch (type.toUpperCase()) {
            case "CREDIT_CARD":
                this.strategy = new CreditCardStrategy(); break;
            case "DEBIT_CARD":
                this.strategy = new DebitCardStrategy(); break;
            case "PAYPAL":
                this.strategy = new PayPalStrategy(); break;
            case "BANK_TRANSFER":
                this.strategy = new BankTransferStrategy(); break;
            default:
                throw new IllegalArgumentException("Unknown payment method type: " + type);
        }
    }

    // Retrieve the currently selected strategy
    public PaymentStrategy getStrategy() { 
        return strategy;
    }
}
