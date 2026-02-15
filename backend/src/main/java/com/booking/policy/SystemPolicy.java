package com.booking.policy;

/* Holds platform-wide system policies configured by the Admin
 * Controls: cancellation rules, default payment strategy, notification settings, refund policies
 */
public class SystemPolicy {
    private String cancellationPolicyType;   // FLEXIBLE, STRICT, NO_CANCELLATION
    private String defaultPaymentMethod;     // CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
    private Map<String, String> notificationSettings;
    private double refundPercentageOverride; // -1 means use policy default

    public SystemPolicy() {

    }

    // Returns the active cancellation policy instance based on configured type.
    public CancellationPolicy getCancellationPolicy() {

    }

    // Getters & Setters

    public Map<String, String> getNotificationSettings() {
        return new HashMap<>(notificationSettings);
    }

    public void setCancellationPolicyType(String type) {

    }

    public void setDefaultPaymentMethod(String method) {

    }

    public void setNotificationSetting(String key, String value) {

    }

    public void setRefundPercentageOverride(double pct) {

    }

    public String getCancellationPolicyType() { 

    }

    public String getDefaultPaymentMethod() { 

    }

    public double getRefundPercentageOverride() { 

    }

    @Override
    public String toString() {

    }
}
