package com.app.ui;

import com.booking.domain.Admin;
import com.booking.domain.Booking;
import com.booking.domain.Client;
import com.booking.domain.Consultant;
import com.booking.domain.Payment;
import com.booking.domain.PaymentMethod;
import com.booking.domain.Service;
import com.booking.domain.TimeSlot;
import com.booking.domain.User;
import com.booking.policy.SystemPolicy;
import com.booking.service.AdminService;
import com.booking.service.BookingService;
import com.booking.service.ConsultantService;
import com.booking.service.PaymentService;
import com.booking.singleton.DatabaseManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

// Terminal UI for the Service Booking & Consulting Platform
// Demonstrates all 12 use cases via role-based menus (Client, Consultant, Admin)
// Uses in-memory storage only - no database persistence between runs (yet)
public class ConsoleApp {

    private static final DatabaseManager db = DatabaseManager.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    private static ConsultantService consultantService;
    private static BookingService bookingService;
    private static PaymentService paymentService;
    private static AdminService adminService;
    private static SystemPolicy systemPolicy;

    // null when no user is logged in
    private static User currentUser = null;

    public static void main(String[] args) {
        initServices();
        populateData();

        System.out.println("--- Service Booking & Consulting Platform ---");

        // Main loop: show login screen or the appropriate role menu
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                switch (currentUser.getRole()) {
                    case "CLIENT":
                        showClientMenu();
                        break;
                    case "CONSULTANT": 
                        showConsultantMenu();
                        break;
                    case "ADMIN":      
                        showAdminMenu();      
                        break;
                }
            }
        }
    }

    // Instantiate all services with the default system policy
    private static void initServices() {
        systemPolicy = new SystemPolicy();
        consultantService = new ConsultantService();
        // BookingService needs the active cancellation policy at construction time
        bookingService = new BookingService(systemPolicy.getCancellationPolicy());
        paymentService = new PaymentService();
        adminService = new AdminService(consultantService, systemPolicy);
    }

    // Pre-populate the system with sample users, services, slots and a payment method so the app can be tested immediately without manual setup
    private static void populateData() {
        // One admin who can approve consultants and configure policies
        Admin admin = new Admin(1, "Alice Admin", "admin@platform.com", "admin123");
        db.saveUser(admin);

        // Two clients - Bob has a pre-saved credit card, Carol does not
        Client client1 = new Client(2, "Bob Client", "bob@email.com", "pass123");
        Client client2 = new Client(3, "Carol Client", "carol@email.com", "pass456");
        db.saveUser(client1);
        db.saveUser(client2);

        // Dave is approved and bookable, Eve is pending admin approval
        Consultant c1 = new Consultant(4, "Dave Consultant", "dave@consult.com", "pass789", "Software Engineering");
        Consultant c2 = new Consultant(5, "Eve Consultant", "eve@consult.com", "passabc", "Career Advising");
        db.saveUser(c1);
        db.saveUser(c2);
        consultantService.approveConsultant(4);

        // Three services across two consultants
        Service s1 = new Service(1, "Software Architecture Review", 60, 150.00, "Review your system design");
        Service s2 = new Service(2, "Career Coaching Session", 45, 90.00, "Plan your career path");
        Service s3 = new Service(3, "Code Review & Best Practices", 30,  75.00, "Code quality analysis");
        db.saveService(s1);
        db.saveService(s2);
        db.saveService(s3);
        // Dave offers software services, Eve offers career advising
        c1.addService(s1);
        c1.addService(s3);
        c2.addService(s2);
        db.saveUser(c1);
        db.saveUser(c2);

        // Random timeslots, using current local time plus some random increments
        TimeSlot ts1 = new TimeSlot(1, LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                                       LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        TimeSlot ts2 = new TimeSlot(2, LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                                       LocalDateTime.now().plusDays(1).withHour(15).withMinute(0));
        TimeSlot ts3 = new TimeSlot(3, LocalDateTime.now().plusDays(2).withHour(11).withMinute(0),
                                       LocalDateTime.now().plusDays(2).withHour(12).withMinute(0));
        c1.addAvailableSlot(ts1);
        c1.addAvailableSlot(ts2);
        c1.addAvailableSlot(ts3);
        db.saveTimeSlot(ts1);
        db.saveTimeSlot(ts2);
        db.saveTimeSlot(ts3);
        db.saveUser(c1);

        // Pre-saved valid credit card for Bob so payment can be tested right away
        PaymentMethod pm = paymentService.createPaymentMethod("CREDIT_CARD");
        pm.addDetail("cardNumber", "4111111111111111");
        pm.addDetail("expiry", "12/27");
        pm.addDetail("cvv", "123");
        client1.addPaymentMethod(pm);
        db.saveUser(client1);

        System.out.println("Sample data loaded for demonstration and testing. Have fun.\n");
    }

    // Prompt for credentials; typing 0 exits the app
    private static void showLoginMenu() {
        System.out.println("\nLOGIN");
        System.out.println("  bob@email.com    / pass123  (Client)");
        System.out.println("  carol@email.com  / pass456  (Client)");
        System.out.println("  dave@consult.com / pass789  (Consultant - Approved)");
        System.out.println("  eve@consult.com  / passabc  (Consultant - Pending)");
        System.out.println("  admin@platform.com / admin123 (Admin)");
        System.out.println("  0 to exit");

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        // Allow the user to exit cleanly from the login screen
        if (email.equals("0")) {
            System.out.println("Goodbye!");
            System.exit(0);
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        // Search all users for a matching email + password pair
        currentUser = db.findAllUsers().stream().filter(u -> u.login(email, password)).findFirst().orElse(null);

        if (currentUser == null)
            System.out.println("Invalid credentials. Try again.");
        else
            System.out.println("Welcome, " + currentUser.getName() + " [" + currentUser.getRole() + "]");
    }

    private static void logout() {
        System.out.println("Goodbye, " + currentUser.getName() + "!");
        // Setting to null causes the main loop to redirect back to the login screen
        currentUser = null;
    }


    // CLIENT MENU

    private static void showClientMenu() {
        // Cast is safe here since we only reach this method when role == CLIENT
        Client client = (Client) currentUser;
        System.out.println("\nCLIENT MENU");
        System.out.println("  1  Browse Services");
        System.out.println("  2  Request Booking");
        System.out.println("  3  Cancel Booking");
        System.out.println("  4  View Booking History");
        System.out.println("  5  Process Payment");
        System.out.println("  6  Manage Payment Methods");
        System.out.println("  7  View Payment History");
        System.out.println("  0  Logout");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1": 
                browseServices();             
                break;
            case "2": 
                requestBooking(client);       
                break;
            case "3": 
                cancelBooking(client);        
                break;
            case "4": 
                viewBookingHistory(client);   
                break;
            case "5": 
                processPayment(client);       
                break;
            case "6": 
                managePaymentMethods(client); 
                break;
            case "7": 
                viewPaymentHistory(client);   
                break;
            case "0": 
                logout();                    
                break;
            default:  
                System.out.println("Invalid choice.");
        }
    }

    // UC1: list all platform services
    private static void browseServices() {
        List<Service> services = consultantService.getAllServices();
        if (services.isEmpty()) { 
            System.out.println("No services available."); 
            return; 
        }
        // Each Service.toString() prints id, name, duration, price and description
        services.forEach(s -> System.out.println("  " + s));
    }

    // UC2: pick consultant -> slot -> service, then submit booking request
    private static void requestBooking(Client client) {
        // Only show consultants who have been approved by the admin
        List<Consultant> consultants = consultantService.getAllApprovedConsultants();
        if (consultants.isEmpty()) { 
            System.out.println("No approved consultants available."); 
            return; 
        }

        consultants.forEach(c -> System.out.println("  [" + c.getId() + "] " + c.getName() + " (" + c.getSpecialization() + ")"));
        System.out.print("Consultant ID: ");
        Consultant consultant = consultantService.getConsultantById(readInt());
        if (consultant == null) { 
            System.out.println("Not found."); 
            return; 
        }

        // Only show slots that are not already taken by another booking
        List<TimeSlot> slots = consultantService.getAvailableSlots(consultant.getId());
        if (slots.isEmpty()) { 
            System.out.println("No available slots."); 
            return; 
        }
        slots.forEach(ts -> System.out.println("  [" + ts.getId() + "] " + ts));
        System.out.print("Slot ID: ");
        TimeSlot slot = consultant.findSlotById(readInt());
        // Double-check availability in case slot was taken between listing and selection
        if (slot == null || !slot.isAvailable()) { 
            System.out.println("Slot not available."); 
            return; 
        }

        // Show only the services this specific consultant offers
        List<Service> services = consultant.getOfferedServices();
        services.forEach(s -> System.out.println("  [" + s.getId() + "] " + s));
        System.out.print("Service ID: ");
        int sid = readInt();
        Service service = services.stream().filter(s -> s.getId() == sid).findFirst().orElse(null);
        if (service == null) { 
            System.out.println("Service not found."); 
            return; 
        }

        // BookingService handles state initialization, observer wiring and slot reservation
        Booking booking = bookingService.createBooking(client, consultant, service, slot);
        if (booking != null) System.out.println("Booking created: " + booking);
    }

    // UC3: cancel a booking, refund amount depends on the active cancellation policy
    private static void cancelBooking(Client client) {
        List<Booking> bookings = bookingService.getBookingHistory(client.getId());
        if (bookings.isEmpty()) { 
            System.out.println("No bookings found."); 
            return; 
        }

        bookings.forEach(b -> System.out.println("  [" + b.getId() + "] " + b));
        System.out.print("Booking ID to cancel: ");
        // cancelBooking returns the refund amount (0.0 if no refund applies under current policy)
        double refund = bookingService.cancelBooking(readInt(), client.getId());
        System.out.printf("Booking cancelled. Refund: $%.2f%n", refund);
    }

    // UC4: show all bookings and their current states
    private static void viewBookingHistory(Client client) {
        List<Booking> bookings = bookingService.getBookingHistory(client.getId());
        if (bookings.isEmpty()) { 
            System.out.println("No bookings found."); 
            return; 
        }
        // Booking.toString() includes id, client, consultant, service name and current state
        bookings.forEach(b -> System.out.println("  " + b));
    }

    // UC5: pay for a booking that is in PENDING_PAYMENT state
    private static void processPayment(Client client) {
        // Filter down to only the bookings that are actually waiting for payment
        List<Booking> pending = bookingService.getBookingHistory(client.getId()).stream()
                .filter(b -> "PENDING_PAYMENT".equals(b.getCurrentStateName()))
                .collect(Collectors.toList());
        if (pending.isEmpty()) { System.out.println("No bookings awaiting payment."); return; }

        pending.forEach(b -> System.out.println("  [" + b.getId() + "] " + b + " $" + b.getTotalPrice()));
        System.out.print("Booking ID: ");
        // Fetch directly from DB to get the live object (state may have changed since list was built)
        Booking booking = db.findBookingById(readInt());
        if (booking == null) { 
            System.out.println("Booking not found."); 
            return; 
        }

        // Show all payment methods saved to this client's account
        List<PaymentMethod> methods = client.getPaymentMethods();
        if (methods.isEmpty()) { 
            System.out.println("No saved payment methods. Add one via option 6."); 
            return; 
        }
        methods.forEach(pm -> System.out.println("  [" + pm.getId() + "] " + pm));
        System.out.print("Payment Method ID (0 = default): ");
        int pmId = readInt();
        // 0 is a shortcut to use whichever method the client has marked as default
        PaymentMethod method = pmId == 0 ? client.getDefaultPaymentMethod() : client.getPaymentMethodById(pmId);
        if (method == null) { 
            System.out.println("Payment method not found."); 
            return; 
        }

        // PaymentService validates the method details, simulates processing delay,
        // generates a transaction ID and advances the booking state to PAID on success
        Payment payment = paymentService.processPayment(booking, method);
        if (payment != null) {
            System.out.println("Payment complete: " + payment);
            System.out.println("Booking state: " + booking.getCurrentStateName());
        }
    }

    // UC6: add, view, remove, or change default payment methods
    private static void managePaymentMethods(Client client) {
        System.out.println("  1 View  2 Add Credit Card  3 Add Debit Card");
        System.out.println("  4 Add PayPal  5 Add Bank Transfer  6 Remove  7 Set Default");
        System.out.print("Choice: ");
        switch (scanner.nextLine().trim()) {
            case "1": 
                // Print all saved methods with their IDs and types
                client.getPaymentMethods().forEach(pm -> System.out.println("  " + pm)); 
                break;
            case "2": 
                addCardMethod(client, "CREDIT_CARD"); 
                break;
            case "3": 
                addCardMethod(client, "DEBIT_CARD");  
                break;
            case "4": 
                addPayPalMethod(client);              
                break;
            case "5": 
                addBankMethod(client);                
                break;
            case "6":
                client.getPaymentMethods().forEach(pm -> System.out.println("  [" + pm.getId() + "] " + pm));
                System.out.print("ID to remove: ");
                client.removePaymentMethod(readInt());
                break;
            case "7":
                client.getPaymentMethods().forEach(pm -> System.out.println("  [" + pm.getId() + "] " + pm));
                System.out.print("ID to set as default: ");
                client.setDefaultPaymentMethod(readInt());
                System.out.println("Default updated.");
                break;
        }
    }

    // Shared helper for adding CREDIT_CARD or DEBIT_CARD - validation rules are identical for both
    private static void addCardMethod(Client client, String type) {
        System.out.print("Card number (16 digits): "); 
        String num = scanner.nextLine().trim();

        System.out.print("Expiry (MM/YY): ");          
        String exp = scanner.nextLine().trim();

        System.out.print("CVV (3-4 digits): ");         
        String cvv = scanner.nextLine().trim();

        PaymentMethod pm = paymentService.createPaymentMethod(type);
        // Store details as key-value pairs; the payment strategy reads these on validation
        pm.addDetail("cardNumber", num);
        pm.addDetail("expiry", exp);
        pm.addDetail("cvv", cvv);
        client.addPaymentMethod(pm);
        // Persist the updated client so the new method survives for the rest of the session
        db.saveUser(client);
        System.out.println(type + " added.");
    }

    private static void addPayPalMethod(Client client) {
        System.out.print("PayPal email: ");
        PaymentMethod pm = paymentService.createPaymentMethod("PAYPAL");
        // PayPal only needs an email address - validated by regex in PayPalStrategy
        pm.addDetail("email", scanner.nextLine().trim());
        client.addPaymentMethod(pm);
        db.saveUser(client);
        System.out.println("PayPal added.");
    }

    private static void addBankMethod(Client client) {
        System.out.print("Account number (8-17 digits): "); 
        String acct = scanner.nextLine().trim();

        System.out.print("Routing number (9 digits): ");
        String routing = scanner.nextLine().trim();

        PaymentMethod pm = paymentService.createPaymentMethod("BANK_TRANSFER");
        // BankTransferStrategy validates account number length and routing number format
        pm.addDetail("accountNumber", acct);
        pm.addDetail("routingNumber", routing);
        client.addPaymentMethod(pm);
        db.saveUser(client);
        System.out.println("Bank transfer added.");
    }

    // UC7: show full payment history for this client
    private static void viewPaymentHistory(Client client) {
        List<Payment> payments = paymentService.getPaymentHistory(client.getId());
        if (payments.isEmpty()) { 
            System.out.println("No payments found."); 
            return; 
        }
        // Payment.toString() shows transaction ID, amount, method type, status and timestamp
        payments.forEach(p -> System.out.println("  " + p));
    }


    // CONSULTANT MENU

    private static void showConsultantMenu() {
        // Cast is safe here since we only reach this method when role == CONSULTANT
        Consultant consultant = (Consultant) currentUser;
        System.out.println("\nCONSULTANT MENU");
        System.out.println("  1  Manage Availability");
        System.out.println("  2  View Pending Requests");
        System.out.println("  3  Accept Booking");
        System.out.println("  4  Reject Booking");
        System.out.println("  5  Complete Booking");
        System.out.println("  6  View All My Bookings");
        System.out.println("  0  Logout");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1": 
                manageAvailability(consultant);     
                break;
            case "2": 
                viewPendingBookings(consultant);    
                break;
            case "3": 
                acceptBooking(consultant);          
                break;
            case "4": 
                rejectBooking(consultant);          
                break;
            case "5": 
                completeBooking(consultant);        
                break;
            case "6": 
                viewConsultantBookings(consultant); 
                break;
            case "0": 
                logout();                           
                break;
            default:  
                System.out.println("Invalid choice.");
        }
    }

    // UC8: add a new time slot to the consultant's schedule
    private static void manageAvailability(Consultant consultant) {
        // Show existing slots before prompting to add a new one
        consultant.getAvailableSlots().forEach(ts -> System.out.println("  " + ts));

        System.out.print("Days from today: ");  
        int days = readInt();

        System.out.print("Start hour (0-23): "); 
        int startH = readInt();

        System.out.print("Duration (hours): "); 
        int duration = readInt();
        // Use milliseconds mod 100000 as a simple unique ID that avoids conflicts with seeded slot IDs
        int id = (int)(System.currentTimeMillis() % 100000);
        TimeSlot slot = new TimeSlot(id,
                LocalDateTime.now().plusDays(days).withHour(startH).withMinute(0).withSecond(0),
                LocalDateTime.now().plusDays(days).withHour(startH + duration).withMinute(0).withSecond(0));
        // addSlot persists to both the consultant object and the DatabaseManager
        consultantService.addSlot(consultant.getId(), slot);
        System.out.println("Slot added: " + slot);
    }

    // UC9: show bookings still waiting for a decision
    private static void viewPendingBookings(Consultant consultant) {
        // Only returns bookings in REQUESTED state - confirmed/rejected ones are filtered out
        List<Booking> pending = bookingService.getPendingBookingsForConsultant(consultant.getId());
        if (pending.isEmpty()) { 
            System.out.println("No pending requests."); 
            return; 
        }
        pending.forEach(b -> System.out.println("  [" + b.getId() + "] " + b));
    }

    // UC9: accept moves booking REQUESTED -> CONFIRMED -> PENDING_PAYMENT
    private static void acceptBooking(Consultant consultant) {
        viewPendingBookings(consultant);
        System.out.print("Booking ID to accept: ");
        // Both state transitions happen inside acceptBooking - confirmed then immediately pending payment
        bookingService.acceptBooking(readInt(), consultant.getId());
    }

    // UC9: reject moves booking REQUESTED -> REJECTED and frees the slot
    private static void rejectBooking(Consultant consultant) {
        viewPendingBookings(consultant);
        System.out.print("Booking ID to reject: ");
        // Rejecting also calls slot.markAvailable() so the slot can be booked by someone else
        bookingService.rejectBooking(readInt(), consultant.getId());
    }

    // UC10: mark a PAID booking as COMPLETED after the session has taken place
    private static void completeBooking(Consultant consultant) {
        // Only PAID bookings can be completed - the state machine enforces this
        List<Booking> paid = bookingService.getBookingsForConsultant(consultant.getId()).stream()
                .filter(b -> "PAID".equals(b.getCurrentStateName()))
                .collect(Collectors.toList());
        if (paid.isEmpty()) { 
            System.out.println("No paid bookings to complete."); 
            return; 
        }
        paid.forEach(b -> System.out.println("  [" + b.getId() + "] " + b));
        System.out.print("Booking ID to complete: ");
        bookingService.completeBooking(readInt(), consultant.getId());
    }

    private static void viewConsultantBookings(Consultant consultant) {
        // Shows all bookings regardless of state - useful for a full history view
        bookingService.getBookingsForConsultant(consultant.getId())
                .forEach(b -> System.out.println("  " + b));
    }


    // ADMIN MENU

    private static void showAdminMenu() {
        System.out.println("\nADMIN MENU");
        System.out.println("  1  View Pending Consultants");
        System.out.println("  2  Approve Consultant");
        System.out.println("  3  Reject Consultant");
        System.out.println("  4  Set Cancellation Policy");
        System.out.println("  5  Set Default Payment Method");
        System.out.println("  6  Set Notification Channel");
        System.out.println("  7  View System Policy");
        System.out.println("  0  Logout");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1":
                // Lists consultants whose isApproved flag is still false
                adminService.getPendingConsultants().forEach(u -> System.out.println("  " + u));
                break;
            case "2":
                System.out.print("Consultant ID to approve: ");
                adminService.approveConsultant(readInt());
                // Re-sync the booking service with the latest policy after any admin action
                bookingService.setCancellationPolicy(systemPolicy.getCancellationPolicy());
                break;
            case "3":
                System.out.print("Consultant ID to reject: ");
                adminService.rejectConsultant(readInt());
                break;
            case "4":
                System.out.println("Options: FLEXIBLE | STRICT | NO_CANCELLATION");
                System.out.print("Policy: ");
                adminService.setCancellationPolicy(scanner.nextLine().trim().toUpperCase());
                // Must push the new policy to BookingService - it holds its own reference to the strategy
                bookingService.setCancellationPolicy(systemPolicy.getCancellationPolicy());
                break;
            case "5":
                System.out.println("Options: CREDIT_CARD | DEBIT_CARD | PAYPAL | BANK_TRANSFER");
                System.out.print("Method: ");
                adminService.setDefaultPaymentMethod(scanner.nextLine().trim().toUpperCase());
                break;
            case "6":
                System.out.println("Options: EMAIL | IN_APP | BOTH");
                System.out.print("Channel: ");
                // Controls whether notifications are sent via email, in-app, or both
                adminService.setNotificationSetting("channel", scanner.nextLine().trim().toUpperCase());
                break;
            case "7":
                // SystemPolicy.toString() prints cancellation type, default payment method and notification settings
                System.out.println(adminService.getSystemPolicy());
                break;
            case "0": 
                logout(); 
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Reads an integer from stdin; returns -1 on bad input instead of crashing
    private static int readInt() {
        try { 
            return Integer.parseInt(scanner.nextLine().trim()); 
        } catch (NumberFormatException e) { 
            return -1; 
        }
    }
}