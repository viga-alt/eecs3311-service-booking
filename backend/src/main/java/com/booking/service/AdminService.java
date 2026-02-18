package com.booking.service;

import com.booking.domain.Admin;
import com.booking.domain.User;
import com.booking.policy.SystemPolicy;
import com.booking.singleton.DatabaseManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

// Service layer for admin operations
public class AdminService {
    // Thread‑safe counter used to generate unique IDs (e.g., for new users or consultants)
    private static final AtomicInteger idGen = new AtomicInteger(900);

    private final DatabaseManager db;   // Handles persistence (DB access), shared instance from a singleton
    private final ConsultantService consultantService;  // Delegates consultant‑related operations (approve, reject, list)
    private SystemPolicy systemPolicy;  // Holds the current platform‑wide system policy (cancellation, payments, etc.)

    public AdminService(ConsultantService consultantService, SystemPolicy systemPolicy) {
        this.db = DatabaseManager.getInstance();
        this.consultantService = consultantService;
        this.systemPolicy = systemPolicy;
    }

    // Approve Consultant Registration
    public void approveConsultant(int consultantId) {
        consultantService.approveConsultant(consultantId);
    }

    // Reject Consultant Registration
    public void rejectConsultant(int consultantId) {
        consultantService.rejectConsultantRegistration(consultantId);
    }

    // Retrieve all consultants who are pending approval
    public List<User> getPendingConsultants() {
        return consultantService.getPendingConsultants();
    }

    // Define System Policies & Getter

    public void setCancellationPolicy(String policyType) {
        systemPolicy.setCancellationPolicyType(policyType);
        System.out.println("[AdminService] Cancellation policy set to: " + policyType);
    }

    public void setDefaultPaymentMethod(String method) {
        systemPolicy.setDefaultPaymentMethod(method);
    }

    public void setNotificationSetting(String key, String value) {
        systemPolicy.setNotificationSetting(key, value);
    }

    public void setRefundOverride(double pct) {
        systemPolicy.setRefundPercentageOverride(pct);
    }

    public SystemPolicy getSystemPolicy() {
        return systemPolicy;
    }

    // User registration

    public void registerUser(User user) {
        db.saveUser(user);
        System.out.println("[AdminService] User registered: " + user);
    }
}
