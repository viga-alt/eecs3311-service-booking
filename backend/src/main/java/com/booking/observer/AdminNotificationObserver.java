package com.booking.observer;

import com.booking.domain.Admin;
import com.booking.domain.Booking;

/* OBSERVER PATTERN - Concrete Observer for Admin notifications
 * Notifies the admin about platform-level events (e.g. cancellations after payment)
 */
public class AdminNotificationObserver implements BookingObserver {
    private Admin admin;    // The Admin to notify
    private NotificationService notificationService;    // Service used to send messages

    public AdminNotificationObserver(Admin admin, NotificationService notificationService) {
        this.admin = admin;
        this.notificationService = notificationService;
    }

    // Called when a booking event occurs
    @Override
    public void update(Booking booking, String event) {
        String message = buildMessage(booking, event);
        if (message != null) {
            notificationService.send(admin.getEmail(), admin.getId(), message);
        }
    }

    // Create the text message
    private String buildMessage(Booking booking, String event) {
        switch (event) {
            case "BOOKING_CANCELLED_AFTER_PAYMENT":
                return "[ADMIN ALERT] Booking #" + booking.getId() + " cancelled after payment. Refund required.";
            case "BOOKING_COMPLETED":
                return "[ADMIN INFO] Booking #" + booking.getId() + " completed successfully.";
            default:
                return null;    // No notifications for other events
        }
    }
}
