package com.booking.controller;

import com.booking.domain.*;
import com.booking.service.PaymentService;
import com.booking.singleton.DatabaseManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final DatabaseManager db = DatabaseManager.getInstance();

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Integer> request) {
        int bookingId = request.get("bookingId");
        int clientId = request.get("clientId");
        int paymentMethodId = request.get("paymentMethodId");

        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Booking not found"));
        }

        User user = db.findUserById(clientId);
        if (!(user instanceof Client)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid client"));
        }
        Client client = (Client) user;

        PaymentMethod method;
        if (paymentMethodId == 0) {
            method = client.getDefaultPaymentMethod();
        } else {
            method = client.getPaymentMethodById(paymentMethodId);
        }
        if (method == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment method not found"));
        }

        Payment payment = paymentService.processPayment(booking, method);
        if (payment == null || !"SUCCESS".equals(payment.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment processing failed"));
        }

        return ResponseEntity.ok(paymentToMap(payment));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getPaymentHistory(@PathVariable int clientId) {
        List<Map<String, Object>> result = paymentService.getPaymentHistory(clientId).stream()
                .map(this::paymentToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/methods/client/{clientId}")
    public ResponseEntity<?> getPaymentMethods(@PathVariable int clientId) {
        User user = db.findUserById(clientId);
        if (!(user instanceof Client)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid client"));
        }
        Client client = (Client) user;
        List<Map<String, Object>> result = client.getPaymentMethods().stream()
                .map(this::methodToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/methods/client/{clientId}")
    public ResponseEntity<?> addPaymentMethod(@PathVariable int clientId, @RequestBody Map<String, String> request) {
        User user = db.findUserById(clientId);
        if (!(user instanceof Client)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid client"));
        }
        Client client = (Client) user;
        String type = request.get("type");

        PaymentMethod pm = paymentService.createPaymentMethod(type);
        switch (type.toUpperCase()) {
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                pm.addDetail("cardNumber", request.getOrDefault("cardNumber", ""));
                pm.addDetail("expiry", request.getOrDefault("expiry", ""));
                pm.addDetail("cvv", request.getOrDefault("cvv", ""));
                break;
            case "PAYPAL":
                pm.addDetail("email", request.getOrDefault("email", ""));
                break;
            case "BANK_TRANSFER":
                pm.addDetail("accountNumber", request.getOrDefault("accountNumber", ""));
                pm.addDetail("routingNumber", request.getOrDefault("routingNumber", ""));
                break;
        }

        client.addPaymentMethod(pm);
        db.saveUser(client);
        return ResponseEntity.ok(methodToMap(pm));
    }

    @DeleteMapping("/methods/{methodId}/client/{clientId}")
    public ResponseEntity<?> removePaymentMethod(@PathVariable int methodId, @PathVariable int clientId) {
        User user = db.findUserById(clientId);
        if (!(user instanceof Client)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid client"));
        }
        Client client = (Client) user;
        boolean removed = client.removePaymentMethod(methodId);
        if (!removed) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment method not found"));
        }
        db.saveUser(client);
        return ResponseEntity.ok(Map.of("message", "Payment method removed"));
    }

    @PutMapping("/methods/{methodId}/client/{clientId}/default")
    public ResponseEntity<?> setDefaultMethod(@PathVariable int methodId, @PathVariable int clientId) {
        User user = db.findUserById(clientId);
        if (!(user instanceof Client)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid client"));
        }
        Client client = (Client) user;
        client.setDefaultPaymentMethod(methodId);
        db.saveUser(client);
        return ResponseEntity.ok(Map.of("message", "Default payment method updated"));
    }

    private Map<String, Object> paymentToMap(Payment p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.getId());
        map.put("amount", p.getAmount());
        map.put("transactionId", p.getTransactionId());
        map.put("status", p.getStatus());
        map.put("paymentMethodType", p.getPaymentMethodType());
        map.put("bookingId", p.getBookingId());
        map.put("timestamp", p.getTimestamp().toString());
        return map;
    }

    private Map<String, Object> methodToMap(PaymentMethod pm) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", pm.getId());
        map.put("type", pm.getType());
        map.put("isDefault", pm.isDefault());
        return map;
    }
}
