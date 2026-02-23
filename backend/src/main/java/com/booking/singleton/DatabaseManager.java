package com.booking.singleton;

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
