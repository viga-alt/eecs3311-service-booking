package com.booking.singleton;

import com.booking.domain.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/* SINGLETON PATTERN - DatabaseManager
 * Single shared instance that manages all in-memory data stores
 * Acts as the simulated persistence layer for all entities
 *
 * Stores:
 *   - Users (Clients, Consultants, Admins)
 *   - Services
 *   - TimeSlots
 *   - Bookings
 *   - Payments
 *   - PaymentMethods
 */
public class DatabaseManager {

    // Singleton instance, volatile used because we need visibility, values must be up-to-date
    private static volatile DatabaseManager instance;

    // In-memory data stores (sql database implementation not yet done)
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Service> services = new ConcurrentHashMap<>();
    private final Map<Integer, TimeSlot> timeSlots = new ConcurrentHashMap<>();
    private final Map<Integer, Booking> bookings = new ConcurrentHashMap<>();
    private final Map<Integer, Payment> payments = new ConcurrentHashMap<>();
    private final Map<Integer, PaymentMethod> paymentMethods = new ConcurrentHashMap<>();

    // Private constructor
    private DatabaseManager() {
    }

    // getInstance(), thread‑safe locking
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    // Generic save / update

    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    public void saveService(Service s) {
        services.put(s.getId(), s);
    }

    public void saveTimeSlot(TimeSlot ts) {
        timeSlots.put(ts.getId(), ts);
    }

    public void saveBooking(Booking b) {
        bookings.put(b.getId(), b);
    }

    public void savePayment(Payment p) {
        payments.put(p.getId(), p);
    }

    public void savePaymentMethod(PaymentMethod pm) {
        paymentMethods.put(pm.getId(), pm);
    }

    // Find by ID

    public User findUserById(int id) {
        return users.get(id);
    }

    public Service findServiceById(int id) {
        return services.get(id);
    }

    public TimeSlot findTimeSlotById(int id) {
        return timeSlots.get(id);
    }

    public Booking findBookingById(int id) {
        return bookings.get(id);
    }

    public Payment findPaymentById(int id) {
        return payments.get(id);
    }

    public PaymentMethod findPaymentMethodById(int id) {
        return paymentMethods.get(id);
    }

    // Find all

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Service> findAllServices() {
        return new ArrayList<>(services.values());
    }

    public List<TimeSlot> findAllTimeSlots() {
        return new ArrayList<>(timeSlots.values());
    }

    public List<Booking> findAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    public List<Payment> findAllPayments() {
        return new ArrayList<>(payments.values());
    }

    public List<PaymentMethod> findAllPaymentMethods() {
        return new ArrayList<>(paymentMethods.values());
    }

    // Filtered queries

    // Return all bookings made by a particular client
    public List<Booking> findBookingsByClientId(int clientId) {
        // Keep only bookings whose client id matches the requested one
        return bookings.values().stream()
                .filter(b -> b.getClient().getId() == clientId)
                .collect(Collectors.toList());
    }

    // Return all bookings assigned to a particular consultant
    public List<Booking> findBookingsByConsultantId(int consultantId) {
        // Keep only bookings whose consultant id matches the requested one
        return bookings.values().stream()
                .filter(b -> b.getConsultant().getId() == consultantId)
                .collect(Collectors.toList());
    }

    // Return all payments made by a particular client
    public List<Payment> findPaymentsByClientId(int clientId) {
        // Only consider bookings for this client that already have a payment
        return bookings.values().stream()
                .filter(b -> b.getClient().getId() == clientId && b.getPayment() != null)
                .map(Booking::getPayment)
                .collect(Collectors.toList());
    }

    // Return all payment methods owned by a particular client
    public List<PaymentMethod> findPaymentMethodsByClientId(int clientId) {
        return paymentMethods.values().stream()
                .filter(pm -> {
                    User u = users.get(clientId);
                    // Only a client can own payment methods
                    if (u instanceof Client) {
                        // Check that the method is in the client's list
                        return ((Client) u).getPaymentMethods().contains(pm);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // Return all users that are consultants
    public List<User> findConsultants() {
        // Keep only those whose type is Consultant
        return users.values().stream()
                .filter(u -> u instanceof Consultant)
                .collect(Collectors.toList());
    }

    // Return consultants that are still pending approval
    public List<User> findPendingConsultants() {
        // Keep only consultants that are not yet approved
        return users.values().stream()
                .filter(u -> u instanceof Consultant && !((Consultant) u).isApproved())
                .collect(Collectors.toList());
    }

    // Delete

    public void deletePaymentMethod(int id) {
        paymentMethods.remove(id);
    }

    public void deleteBooking(int id) {
        bookings.remove(id);
    }

    public void deleteUser(int id) {
        users.remove(id);
    }
}
