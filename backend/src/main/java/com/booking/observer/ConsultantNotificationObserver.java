package com.booking.observer;

import com.booking.domain.Booking;
import com.booking.domain.Consultant;

/* OBSERVER PATTERN - Concrete Observer for Consultant notifications
 * Notifies the consultant when booking events occur that affect them
 */
public class ConsultantNotificationObserver implements BookingObserver {
    private Consultant consultant;
    private NotificationService notificationService;

    public ConsultantNotificationObserver(Consultant consultant, NotificationService notificationService) {
        this.consultant = consultant;
        this.notificationService = notificationService;
    }

    // Called when a booking event occurs
    @Override
    public void update(Booking booking, String event) {
        String message = buildMessage(booking, event);
        if (message != null) {
            notificationService.send(consultant.getEmail(), consultant.getId(), message);
        }
    }

    // Create the test message
    private String buildMessage(Booking booking, String event) {
        switch (event) {
            case "BOOKING_REQUESTED":
                return "New booking request #" + booking.getId() + " from client. Please review.";
            case "PAYMENT_COMPLETED":
                return "Payment received for booking #" + booking.getId() + ". Session is scheduled.";
            case "BOOKING_CANCELLED":
            case "BOOKING_CANCELLED_AFTER_PAYMENT":
                return "Booking #" + booking.getId() + " has been cancelled.";
            case "BOOKING_COMPLETED":
                return "You have marked booking #" + booking.getId() + " as completed.";
            default:
                return null;    // No notifications for other events
        }
    }
}
