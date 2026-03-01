package com.booking.policy;

import com.booking.strategy.cancellation.CancellationPolicy;
import com.booking.strategy.cancellation.FlexibleCancellationPolicy;
import com.booking.strategy.cancellation.NoCancellationPolicy;
import com.booking.strategy.cancellation.StrictCancellationPolicy;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/* Holds platform-wide system policies configured by the Admin
 * Controls: cancellation rules, default payment strategy, notification settings, refund policies
 */
public class SystemPolicy {
    private String cancellationPolicyType;   // FLEXIBLE, STRICT, NO_CANCELLATION
    private String defaultPaymentMethod;     // CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
    private Map<String, String> notificationSettings;
    private double refundPercentageOverride; // -1 means use policy default

    public SystemPolicy() {
        this.cancellationPolicyType = "FLEXIBLE";
        this.defaultPaymentMethod = "CREDIT_CARD";
        this.notificationSettings = new HashMap<>();
        this.notificationSettings.put("channel", "BOTH");
        this.notificationSettings.put("emailEnabled", "true");
        this.notificationSettings.put("inAppEnabled", "true");
        this.refundPercentageOverride = -1;
    }

    // Returns the active cancellation policy instance based on configured type.
    public CancellationPolicy getCancellationPolicy() {
        switch (cancellationPolicyType.toUpperCase()) {
            case "STRICT":
                return new StrictCancellationPolicy();
            case "NO_CANCELLATION":
                return new NoCancellationPolicy();
            case "FLEXIBLE":
            default:
                return new FlexibleCancellationPolicy();
        }
    }

    // Getters & Setters

    public Map<String, String> getNotificationSettings() {
        return new HashMap<>(notificationSettings);
    }

    public void setCancellationPolicyType(String type) {
        this.cancellationPolicyType = type;
        System.out.println("[SystemPolicy] Cancellation policy updated to: " + type);
    }

    public void setDefaultPaymentMethod(String method) {
        this.defaultPaymentMethod = method;
        System.out.println("[SystemPolicy] Default payment method set to: " + method);
    }

    public void setNotificationSetting(String key, String value) {
        notificationSettings.put(key, value);
        System.out.println("[SystemPolicy] Notification setting '" + key + "' = '" + value + "'");
    }

    public void setRefundPercentageOverride(double pct) {
        this.refundPercentageOverride = pct;
        System.out.println("[SystemPolicy] Refund override set to: " + pct + "%");
    }

    public String getCancellationPolicyType() { 
        return cancellationPolicyType; 
    }

    public String getDefaultPaymentMethod() { 
        return defaultPaymentMethod; 
    }

    public double getRefundPercentageOverride() { 
        return refundPercentageOverride;
    }

    // toString Object representation
    @Override
    public String toString() {
        return  String.format("SystemPolicy[cancellation=%s, payment=%s, notifications=%s]",
                cancellationPolicyType, defaultPaymentMethod, notificationSettings);
    }
}
