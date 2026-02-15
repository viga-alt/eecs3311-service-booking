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

    @Override
    public String toString() {

    }
}
