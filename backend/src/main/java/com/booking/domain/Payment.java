package com.booking.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents a payment record for a booking
public class Payment {
    private int id;                     // Unique payment identifier
    private double amount;              // Payment amount
    private String transactionId;       // Transaction identifier
    private String status;              // Payment status: PENDING, SUCCESS, FAILED, REFUNDED
    private LocalDateTime timestamp;    // Payment timestamp
    private String paymentMethodType;   // e.g., CARD, PAYPAL, BANK TRANSFER
    private int bookingId;              // Booking identifier

    public Payment(int id, double amount, String transactionId, String paymentMethodType, int bookingId) {
        this.id = id;
        this.amount = amount;
        this.transactionId = transactionId;
        this.status = "PENDING";    // Default PENDING status
        this.timestamp = LocalDateTime.now();
        this.paymentMethodType = paymentMethodType;
        this.bookingId = bookingId;
    }

    // Set status to SUCCESS, FAILED, REFUNDED, respectively

    public void markSuccess() { 
        this.status = "SUCCESS"; 
    }

    public void markFailed() { 
        this.status = "FAILED"; 
    }

    public void markRefunded() { 
        this.status = "REFUNDED"; 
    }

    // Getters

    public int getId() { 
        return id; 
    }

    public double getAmount() { 
        return amount; 
    }

    public String getTransactionId() { 
        return transactionId; 
    }

    public String getStatus() { 
        return status; 
    }

    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }

    public String getPaymentMethodType() { 
        return paymentMethodType; 
    }

    public int getBookingId() { 
        return bookingId; 
    }

    // toString Object representation
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return  String.format("Payment[id=%d, txId=%s, amount=$%.2f, method=%s, status=%s, time=%s]",
                id, transactionId, amount, paymentMethodType, status, timestamp.format(fmt));
    }
}