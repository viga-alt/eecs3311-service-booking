package com.booking.config;

import com.booking.domain.*;
import com.booking.service.ConsultantService;
import com.booking.service.PaymentService;
import com.booking.singleton.DatabaseManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ConsultantService consultantService;
    private final PaymentService paymentService;

    public DataInitializer(ConsultantService consultantService, PaymentService paymentService) {
        this.consultantService = consultantService;
        this.paymentService = paymentService;
    }

    @Override
    public void run(String... args) {
        DatabaseManager db = DatabaseManager.getInstance();

        Admin admin = new Admin(1, "Alice Admin", "admin@platform.com", "admin123");
        db.saveUser(admin);

        Client client1 = new Client(2, "Bob Client", "bob@email.com", "pass123");
        Client client2 = new Client(3, "Carol Client", "carol@email.com", "pass456");
        db.saveUser(client1);
        db.saveUser(client2);

        Consultant c1 = new Consultant(4, "Dave Consultant", "dave@consult.com", "pass789", "Software Engineering");
        Consultant c2 = new Consultant(5, "Eve Consultant", "eve@consult.com", "passabc", "Career Advising");
        db.saveUser(c1);
        db.saveUser(c2);
        consultantService.approveConsultant(4);

        Service s1 = new Service(1, "Software Architecture Review", 60, 150.00, "Review your system design");
        Service s2 = new Service(2, "Career Coaching Session", 45, 90.00, "Plan your career path");
        Service s3 = new Service(3, "Code Review & Best Practices", 30, 75.00, "Code quality analysis");
        db.saveService(s1);
        db.saveService(s2);
        db.saveService(s3);

        c1.addService(s1);
        c1.addService(s3);
        c2.addService(s2);
        db.saveUser(c1);
        db.saveUser(c2);

        TimeSlot ts1 = new TimeSlot(1,
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        TimeSlot ts2 = new TimeSlot(2,
                LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(15).withMinute(0));
        TimeSlot ts3 = new TimeSlot(3,
                LocalDateTime.now().plusDays(2).withHour(11).withMinute(0),
                LocalDateTime.now().plusDays(2).withHour(12).withMinute(0));
        c1.addAvailableSlot(ts1);
        c1.addAvailableSlot(ts2);
        c1.addAvailableSlot(ts3);
        db.saveTimeSlot(ts1);
        db.saveTimeSlot(ts2);
        db.saveTimeSlot(ts3);
        db.saveUser(c1);

        PaymentMethod pm = paymentService.createPaymentMethod("CREDIT_CARD");
        pm.addDetail("cardNumber", "4111111111111111");
        pm.addDetail("expiry", "12/27");
        pm.addDetail("cvv", "123");
        client1.addPaymentMethod(pm);
        db.saveUser(client1);

        System.out.println("Sample data loaded successfully.");
    }
}
