package com.booking.controller;

import com.booking.domain.Consultant;
import com.booking.domain.User;
import com.booking.policy.SystemPolicy;
import com.booking.service.AdminService;
import com.booking.service.BookingService;
import com.booking.singleton.DatabaseManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final BookingService bookingService;
    private final SystemPolicy systemPolicy;

    public AdminController(AdminService adminService, BookingService bookingService, SystemPolicy systemPolicy) {
        this.adminService = adminService;
        this.bookingService = bookingService;
        this.systemPolicy = systemPolicy;
    }

    @GetMapping("/consultants/pending")
    public ResponseEntity<?> getPendingConsultants() {
        List<Map<String, Object>> result = adminService.getPendingConsultants().stream()
                .map(this::userToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/consultants/{id}/approve")
    public ResponseEntity<?> approveConsultant(@PathVariable int id) {
        adminService.approveConsultant(id);
        bookingService.setCancellationPolicy(systemPolicy.getCancellationPolicy());
        return ResponseEntity.ok(Map.of("message", "Consultant approved"));
    }

    @PostMapping("/consultants/{id}/reject")
    public ResponseEntity<?> rejectConsultant(@PathVariable int id) {
        adminService.rejectConsultant(id);
        return ResponseEntity.ok(Map.of("message", "Consultant rejected"));
    }

    @GetMapping("/policy")
    public ResponseEntity<?> getPolicy() {
        Map<String, Object> policy = new LinkedHashMap<>();
        policy.put("cancellationPolicy", systemPolicy.getCancellationPolicyType());
        policy.put("defaultPaymentMethod", systemPolicy.getDefaultPaymentMethod());
        policy.put("notificationSettings", systemPolicy.getNotificationSettings());
        policy.put("refundPercentageOverride", systemPolicy.getRefundPercentageOverride());
        return ResponseEntity.ok(policy);
    }

    @PutMapping("/policy/cancellation")
    public ResponseEntity<?> setCancellationPolicy(@RequestBody Map<String, String> request) {
        String policyType = request.get("policyType");
        adminService.setCancellationPolicy(policyType);
        bookingService.setCancellationPolicy(systemPolicy.getCancellationPolicy());
        return ResponseEntity.ok(Map.of("message", "Cancellation policy updated to: " + policyType));
    }

    @PutMapping("/policy/payment-method")
    public ResponseEntity<?> setDefaultPaymentMethod(@RequestBody Map<String, String> request) {
        String method = request.get("method");
        adminService.setDefaultPaymentMethod(method);
        return ResponseEntity.ok(Map.of("message", "Default payment method set to: " + method));
    }

    @PutMapping("/policy/notification")
    public ResponseEntity<?> setNotificationChannel(@RequestBody Map<String, String> request) {
        String channel = request.get("channel");
        adminService.setNotificationSetting("channel", channel);
        return ResponseEntity.ok(Map.of("message", "Notification channel set to: " + channel));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getSystemStatus() {
        DatabaseManager db = DatabaseManager.getInstance();
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("totalUsers", db.findAllUsers().size());
        status.put("totalBookings", db.findAllBookings().size());
        status.put("totalServices", db.findAllServices().size());
        status.put("totalPayments", db.findAllPayments().size());
        status.put("pendingConsultants", db.findPendingConsultants().size());
        return ResponseEntity.ok(status);
    }

    private Map<String, Object> userToMap(User u) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", u.getId());
        map.put("name", u.getName());
        map.put("email", u.getEmail());
        map.put("role", u.getRole());
        if (u instanceof Consultant) {
            map.put("specialization", ((Consultant) u).getSpecialization());
            map.put("approved", ((Consultant) u).isApproved());
        }
        return map;
    }
}
