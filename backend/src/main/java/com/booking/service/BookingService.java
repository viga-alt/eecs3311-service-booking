package com.booking.service;

import com.booking.domain.*;
import com.booking.observer.*;
import com.booking.singleton.DatabaseManager;
import com.booking.strategy.cancellation.CancellationPolicy;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/* Service layer for booking operations
 * Enforces the active CancellationPolicy when clients attempt to cancel
 * Uses DatabaseManager (Singleton) for persistence
 * Starts up Observer notifications when a booking is created
 */
public class BookingService {
    // Atomic counter for generating unique booking IDs (thread‑safe)
    private static final AtomicInteger idGen = new AtomicInteger(1);

    private final DatabaseManager db;   // Persistent storage instance (Singleton)
    private CancellationPolicy cancellationPolicy;  // Current cancellation policy applied to all bookings
    private final NotificationService notificationService;  // Notification hub used by observers to push messages

    public BookingService(CancellationPolicy cancellationPolicy) {
        this.db = DatabaseManager.getInstance();
        this.cancellationPolicy = cancellationPolicy;
        this.notificationService = new NotificationService("BOTH");
    }

    /* Request a booking
     * Client selects a consultant, a time slot, and a service
     * System validates availability and creates a booking in REQUESTED state
     */
    public Booking createBooking(Client client, Consultant consultant, Service service, TimeSlot slot) {
        if (!consultant.isApproved()) {
            System.out.println("[BookingService] Cannot book — consultant is not approved.");
            return null;
        }
        if (!slot.isAvailable()) {
            System.out.println("[BookingService] Cannot book — time slot is not available.");
            return null;
        }

        int bookingId = idGen.getAndIncrement();
        Booking booking = new Booking(bookingId, client, consultant, service, slot);

        // Start up observers
        booking.addObserver(new ClientNotificationObserver(client, notificationService));
        booking.addObserver(new ConsultantNotificationObserver(consultant, notificationService));

        // Mark the slot as taken
        slot.markUnavailable();

        // Persist storage, save necessary information
        db.saveBooking(booking);

        System.out.println("[BookingService] Booking #" + bookingId + " created in REQUESTED state.");
        booking.notifyObservers("BOOKING_REQUESTED");

        return booking;
    }

    /* Cancel a booking
     * Client cancels an existing booking
     * Cancellation rules from CancellationPolicy are applied
     * If paid, refund is calculated
     */
    public double cancelBooking(int bookingId, int clientId) {
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("[BookingService] Booking #" + bookingId + " not found.");
            return 0;
        }
        if (booking.getClient().getId() != clientId) {
            System.out.println("[BookingService] Unauthorized: booking does not belong to this client.");
            return 0;
        }

        if (!cancellationPolicy.canCancel(booking)) {
            System.out.println("[BookingService] Cancellation not permitted under current policy: "
                    + cancellationPolicy.getPolicyName());
            return 0;
        }

        Payment payment = booking.getPayment();
        double refund = cancellationPolicy.calculateRefund(payment);

        booking.cancel();

        if (payment != null && refund > 0) {
            payment.markRefunded();
            db.savePayment(payment);
            System.out.printf("[BookingService] Refund of $%.2f applied under %s policy.%n",
                    refund, cancellationPolicy.getPolicyName());
        }

        // Release the time slot
        booking.getTimeSlot().markAvailable();
        db.saveBooking(booking);

        return refund;
    }

    // View Booking History
    public List<Booking> getBookingHistory(int clientId) {
        return db.findBookingsByClientId(clientId);
    }

    // Accept a Booking
    public void acceptBooking(int bookingId, int consultantId) {
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("[BookingService] Booking #" + bookingId + " not found.");
            return;
        }
        if (booking.getConsultant().getId() != consultantId) {
            System.out.println("[BookingService] Unauthorized: booking does not belong to this consultant.");
            return;
        }
        booking.confirm();
        booking.markPendingPayment(); // Immediately moves to PENDING_PAYMENT after confirmation
        db.saveBooking(booking);
    }

    // Reject a Booking
    public void rejectBooking(int bookingId, int consultantId) {
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("[BookingService] Booking #" + bookingId + " not found.");
            return;
        }
        if (booking.getConsultant().getId() != consultantId) {
            System.out.println("[BookingService] Unauthorized: booking does not belong to this consultant.");
            return;
        }
        booking.reject();
        // Release the slot
        booking.getTimeSlot().markAvailable();
        db.saveBooking(booking);
    }

    // Complete a Booking
    public void completeBooking(int bookingId, int consultantId) {
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("[BookingService] Booking #" + bookingId + " not found.");
            return;
        }
        if (booking.getConsultant().getId() != consultantId) {
            System.out.println("[BookingService] Unauthorized: booking does not belong to this consultant.");
            return;
        }
        booking.complete();
        db.saveBooking(booking);
    }

    // Pending bookings for consultant
    public List<Booking> getPendingBookingsForConsultant(int consultantId) {
        return db.findBookingsByConsultantId(consultantId).stream()
                .filter(b -> "REQUESTED".equals(b.getCurrentStateName()))
                .collect(java.util.stream.Collectors.toList());
    }

    // Getter bookings
    public List<Booking> getBookingsForConsultant(int consultantId) {
        return db.findBookingsByConsultantId(consultantId);
    }

    // Getter & Setter cancellation policy

    public void setCancellationPolicy(CancellationPolicy policy) {
        this.cancellationPolicy = policy;
        System.out.println("[BookingService] Cancellation policy changed to: " + policy.getPolicyName());
    }

    public CancellationPolicy getCancellationPolicy() { 
        return cancellationPolicy; 
    }
}
