package com.app.ui;

import com.booking.domain.*;
import com.booking.policy.SystemPolicy;
import com.booking.service.*;
import com.booking.singleton.DatabaseManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

public class ConsoleApp {

    private static final DatabaseManager db = DatabaseManager.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    private static ConsultantService consultantService;
    private static BookingService bookingService;
    private static PaymentService paymentService;
    private static AdminService adminService;
    private static SystemPolicy systemPolicy;

    private static User currentUser = null;

    public static void main(String[] args) {
        initServices();
        populateData();
        System.out.println("--- Service Booking & Consulting Platform ---");
        while (true) {
            if (currentUser == null) showLoginMenu();
            else {
                switch (currentUser.getRole()) {
                    case "CLIENT":     showClientMenu();     break;
                    case "CONSULTANT": showConsultantMenu(); break;
                    case "ADMIN":      showAdminMenu();      break;
                }
            }
        }
    }

    private static void initServices() {
        systemPolicy      = new SystemPolicy();
        consultantService = new ConsultantService();
        bookingService    = new BookingService(systemPolicy.getCancellationPolicy());
        paymentService    = new PaymentService();
        adminService      = new AdminService(consultantService, systemPolicy);
    }

    private static void populateData() {
        Admin admin = new Admin(1, "Alice Admin", "admin@platform.com", "admin123");
        db.saveUser(admin);

        Client client1 = new Client(2, "Bob Client", "bob@email.com", "pass123");
        Client client2 = new Client(3, "Carol Client", "carol@email.com", "pass456");
        db.saveUser(client1);
        db.saveUser(client2);

        Consultant c1 = new Consultant(4, "Dave Consultant", "dave@consult.com", "pass789", "Software Engineering");
        Consultant c2 = new Consultant(5, "Eve Consultant", "eve@consult.com", "passabc", "Career Advising");
        db.saveUser(c1); db.saveUser(c2);
        consultantService.approveConsultant(4);

        Service s1 = new Service(1, "Software Architecture Review", 60, 150.00, "Review your system design");
        Service s2 = new Service(2, "Career Coaching Session", 45, 90.00, "Plan your career path");
        Service s3 = new Service(3, "Code Review & Best Practices", 30, 75.00, "Code quality analysis");
        db.saveService(s1); db.saveService(s2); db.saveService(s3);
        c1.addService(s1); c1.addService(s3); c2.addService(s2);
        db.saveUser(c1); db.saveUser(c2);

        TimeSlot ts1 = new TimeSlot(1, LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                                       LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        TimeSlot ts2 = new TimeSlot(2, LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                                       LocalDateTime.now().plusDays(1).withHour(15).withMinute(0));
        TimeSlot ts3 = new TimeSlot(3, LocalDateTime.now().plusDays(2).withHour(11).withMinute(0),
                                       LocalDateTime.now().plusDays(2).withHour(12).withMinute(0));
        c1.addAvailableSlot(ts1); c1.addAvailableSlot(ts2); c1.addAvailableSlot(ts3);
        db.saveTimeSlot(ts1); db.saveTimeSlot(ts2); db.saveTimeSlot(ts3);
        db.saveUser(c1);

        // seed Bob's credit card so payment can be tested without manual setup
        PaymentMethod pm = paymentService.createPaymentMethod("CREDIT_CARD");
        pm.addDetail("cardNumber", "4111111111111111");
        pm.addDetail("expiry", "12/27");
        pm.addDetail("cvv", "123");
        client1.addPaymentMethod(pm);
        db.saveUser(client1);

        System.out.println("Sample data loaded for demonstration and testing. Have fun.\n");
    }

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
        if (email.equals("0")) { System.out.println("Goodbye!"); System.exit(0); }
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        currentUser = db.findAllUsers().stream()
                .filter(u -> u.login(email, password)).findFirst().orElse(null);

        if (currentUser == null) System.out.println("Invalid credentials. Try again.");
        else System.out.println("Welcome, " + currentUser.getName() + " [" + currentUser.getRole() + "]");
    }

    private static void logout() {
        System.out.println("Goodbye, " + currentUser.getName() + "!");
        currentUser = null;
    }

    // CLIENT MENU

    private static void showClientMenu() {
        Client client = (Client) currentUser;
        System.out.println("\nCLIENT MENU");
        System.out.println("  1  Browse Services");
        System.out.println("  2  Request Booking");
        System.out.println("  3  Cancel Booking");
        System.out.println("  4  View Booking History");
        System.out.println("  5  Process Payment       [coming soon]");
        System.out.println("  6  Manage Payment Methods [coming soon]");
        System.out.println("  7  View Payment History   [coming soon]");
        System.out.println("  0  Logout");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1": browseServices();           break;
            case "2": requestBooking(client);     break;
            case "3": cancelBooking(client);      break;
            case "4": viewBookingHistory(client); break;
            case "0": logout();                   break;
            default:  System.out.println("Invalid choice.");
        }
    }

    private static void browseServices() {
        List<Service> services = consultantService.getAllServices();
        if (services.isEmpty()) { System.out.println("No services available."); return; }
        services.forEach(s -> System.out.println("  " + s));
    }

    private static void requestBooking(Client client) {
        List<Consultant> consultants = consultantService.getAllApprovedConsultants();
        if (consultants.isEmpty()) { System.out.println("No approved consultants available."); return; }

        consultants.forEach(c -> System.out.println("  [" + c.getId() + "] " + c.getName() + " (" + c.getSpecialization() + ")"));
        System.out.print("Consultant ID: ");
        Consultant consultant = consultantService.getConsultantById(readInt());
        if (consultant == null) { System.out.println("Not found."); return; }

        List<TimeSlot> slots = consultantService.getAvailableSlots(consultant.getId());
        if (slots.isEmpty()) { System.out.println("No available slots."); return; }
        slots.forEach(ts -> System.out.println("  [" + ts.getId() + "] " + ts));
        System.out.print("Slot ID: ");
        TimeSlot slot = consultant.findSlotById(readInt());
        if (slot == null || !slot.isAvailable()) { System.out.println("Slot not available."); return; }

        List<Service> services = consultant.getOfferedServices();
        services.forEach(s -> System.out.println("  [" + s.getId() + "] " + s));
        System.out.print("Service ID: ");
        int sid = readInt();
        Service service = services.stream().filter(s -> s.getId() == sid).findFirst().orElse(null);
        if (service == null) { System.out.println("Service not found."); return; }

        Booking booking = bookingService.createBooking(client, consultant, service, slot);
        if (booking != null) System.out.println("Booking created: " + booking);
    }

    private static void cancelBooking(Client client) {
        List<Booking> bookings = bookingService.getBookingHistory(client.getId());
        if (bookings.isEmpty()) { System.out.println("No bookings found."); return; }
        bookings.forEach(b -> System.out.println("  [" + b.getId() + "] " + b));
        System.out.print("Booking ID to cancel: ");
        double refund = bookingService.cancelBooking(readInt(), client.getId());
        System.out.printf("Booking cancelled. Refund: $%.2f%n", refund);
    }

    private static void viewBookingHistory(Client client) {
        List<Booking> bookings = bookingService.getBookingHistory(client.getId());
        if (bookings.isEmpty()) { System.out.println("No bookings found."); return; }
        bookings.forEach(b -> System.out.println("  " + b));
    }

    // CONSULTANT MENU — stub

    private static void showConsultantMenu() {
        System.out.println("\nCONSULTANT MENU — coming soon");
        logout();
    }

    // ADMIN MENU — stub

    private static void showAdminMenu() {
        System.out.println("\nADMIN MENU — coming soon");
        logout();
    }

    private static int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}