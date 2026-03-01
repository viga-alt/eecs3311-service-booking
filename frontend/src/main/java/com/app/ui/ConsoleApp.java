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
        db.saveUser(client1); db.saveUser(client2);

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
        System.out.println("  5  Process Payment");
        System.out.println("  6  Manage Payment Methods");
        System.out.println("  7  View Payment History");
        System.out.println("  0  Logout");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1": browseServices();            break;
            case "2": requestBooking(client);      break;
            case "3": cancelBooking(client);       break;
            case "4": viewBookingHistory(client);  break;
            case "5": processPayment(client);      break;
            case "6": managePaymentMethods(client);break;
            case "7": viewPaymentHistory(client);  break;
            case "0": logout();                    break;
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

    private static void processPayment(Client client) {
        List<Booking> pending = bookingService.getBookingHistory(client.getId()).stream()
                .filter(b -> "PENDING_PAYMENT".equals(b.getCurrentStateName()))
                .collect(Collectors.toList());
        if (pending.isEmpty()) { System.out.println("No bookings awaiting payment."); return; }

        pending.forEach(b -> System.out.println("  [" + b.getId() + "] " + b + " $" + b.getTotalPrice()));
        System.out.print("Booking ID: ");
        Booking booking = db.findBookingById(readInt());
        if (booking == null) { System.out.println("Booking not found."); return; }

        List<PaymentMethod> methods = client.getPaymentMethods();
        if (methods.isEmpty()) { System.out.println("No saved payment methods. Add one via option 6."); return; }
        methods.forEach(pm -> System.out.println("  [" + pm.getId() + "] " + pm));
        System.out.print("Payment Method ID (0 = default): ");
        int pmId = readInt();
        PaymentMethod method = pmId == 0 ? client.getDefaultPaymentMethod() : client.getPaymentMethodById(pmId);
        if (method == null) { System.out.println("Payment method not found."); return; }

        Payment payment = paymentService.processPayment(booking, method);
        if (payment != null) {
            System.out.println("Payment complete: " + payment);
            System.out.println("Booking state: " + booking.getCurrentStateName());
        }
    }

    private static void managePaymentMethods(Client client) {
        System.out.println("  1 View  2 Add Credit Card  3 Add Debit Card");
        System.out.println("  4 Add PayPal  5 Add Bank Transfer  6 Remove  7 Set Default");
        System.out.print("Choice: ");
        switch (scanner.nextLine().trim()) {
            case "1": client.getPaymentMethods().forEach(pm -> System.out.println("  " + pm)); break;
            case "2": addCardMethod(client, "CREDIT_CARD"); break;
            case "3": addCardMethod(client, "DEBIT_CARD");  break;
            case "4": addPayPalMethod(client);              break;
            case "5": addBankMethod(client);                break;
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

    private static void addCardMethod(Client client, String type) {
        System.out.print("Card number (16 digits): "); String num = scanner.nextLine().trim();
        System.out.print("Expiry (MM/YY): ");          String exp = scanner.nextLine().trim();
        System.out.print("CVV (3-4 digits): ");         String cvv = scanner.nextLine().trim();
        PaymentMethod pm = paymentService.createPaymentMethod(type);
        pm.addDetail("cardNumber", num);
        pm.addDetail("expiry", exp);
        pm.addDetail("cvv", cvv);
        client.addPaymentMethod(pm);
        db.saveUser(client);
        System.out.println(type + " added.");
    }

    private static void addPayPalMethod(Client client) {
        System.out.print("PayPal email: ");
        PaymentMethod pm = paymentService.createPaymentMethod("PAYPAL");
        pm.addDetail("email", scanner.nextLine().trim());
        client.addPaymentMethod(pm);
        db.saveUser(client);
        System.out.println("PayPal added.");
    }

    private static void addBankMethod(Client client) {
        System.out.print("Account number (8-17 digits): "); String acct    = scanner.nextLine().trim();
        System.out.print("Routing number (9 digits): ");    String routing = scanner.nextLine().trim();
        PaymentMethod pm = paymentService.createPaymentMethod("BANK_TRANSFER");
        pm.addDetail("accountNumber", acct);
        pm.addDetail("routingNumber", routing);
        client.addPaymentMethod(pm);
        db.saveUser(client);
        System.out.println("Bank transfer added.");
    }

    private static void viewPaymentHistory(Client client) {
        List<Payment> payments = paymentService.getPaymentHistory(client.getId());
        if (payments.isEmpty()) { System.out.println("No payments found."); return; }
        payments.forEach(p -> System.out.println("  " + p));
    }

    // CONSULTANT MENU

    private static void showConsultantMenu() {
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
            case "1": manageAvailability(consultant);     break;
            case "2": viewPendingBookings(consultant);    break;
            case "3": acceptBooking(consultant);          break;
            case "4": rejectBooking(consultant);          break;
            case "5": completeBooking(consultant);        break;
            case "6": viewConsultantBookings(consultant); break;
            case "0": logout();                           break;
            default:  System.out.println("Invalid choice.");
        }
    }

    private static void manageAvailability(Consultant consultant) {
        consultant.getAvailableSlots().forEach(ts -> System.out.println("  " + ts));
        System.out.print("Days from today: ");  int days     = readInt();
        System.out.print("Start hour (0-23): "); int startH  = readInt();
        System.out.print("Duration (hours): ");  int duration = readInt();
        int id = (int)(System.currentTimeMillis() % 100000);
        TimeSlot slot = new TimeSlot(id,
                LocalDateTime.now().plusDays(days).withHour(startH).withMinute(0).withSecond(0),
                LocalDateTime.now().plusDays(days).withHour(startH + duration).withMinute(0).withSecond(0));
        consultantService.addSlot(consultant.getId(), slot);
        System.out.println("Slot added: " + slot);
    }

    private static void viewPendingBookings(Consultant consultant) {
        List<Booking> pending = bookingService.getPendingBookingsForConsultant(consultant.getId());
        if (pending.isEmpty()) { System.out.println("No pending requests."); return; }
        pending.forEach(b -> System.out.println("  [" + b.getId() + "] " + b));
    }

    private static void acceptBooking(Consultant consultant) {
        viewPendingBookings(consultant);
        System.out.print("Booking ID to accept: ");
        bookingService.acceptBooking(readInt(), consultant.getId());
    }

    private static void rejectBooking(Consultant consultant) {
        viewPendingBookings(consultant);
        System.out.print("Booking ID to reject: ");
        bookingService.rejectBooking(readInt(), consultant.getId());
    }

    private static void completeBooking(Consultant consultant) {
        List<Booking> paid = bookingService.getBookingsForConsultant(consultant.getId()).stream()
                .filter(b -> "PAID".equals(b.getCurrentStateName()))
                .collect(Collectors.toList());
        if (paid.isEmpty()) { System.out.println("No paid bookings to complete."); return; }
        paid.forEach(b -> System.out.println("  [" + b.getId() + "] " + b));
        System.out.print("Booking ID to complete: ");
        bookingService.completeBooking(readInt(), consultant.getId());
    }

    private static void viewConsultantBookings(Consultant consultant) {
        bookingService.getBookingsForConsultant(consultant.getId())
                .forEach(b -> System.out.println("  " + b));
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