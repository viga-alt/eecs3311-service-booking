package com.booking.observer;

import com.booking.domain.Booking;
import com.booking.domain.Client;

/* OBSERVER PATTERN - Concrete Observer for Client notifications
 * Notifies the client when booking events occur that affect them
 */
public class ClientNotificationObserver implements BookingObserver {
    private Client client;  // The Client to notify
    private NotificationService notificationService;    // Service used to send messages

    public ClientNotificationObserver(Client client, NotificationService notificationService) {
        this.client = client;
        this.notificationService = notificationService;
    }

    // Called when a booking event occurs
    @Override
    public void update(Booking booking, String event) {
        String message = buildMessage(booking, event);
        if (message != null) {
            notificationService.send(client.getEmail(), client.getId(), message);
        }
    }

    // Create the text message
    private String buildMessage(Booking booking, String event) {
        switch (event) {
            case "BOOKING_CONFIRMED":
                return "Your booking #" + booking.getId() + " has been confirmed! Please proceed with payment.";
            case "BOOKING_REJECTED":
                return "Your booking #" + booking.getId() + " was rejected by the consultant.";
            case "BOOKING_PENDING_PAYMENT":
                return "Booking #" + booking.getId() + " is awaiting your payment of $" + booking.getTotalPrice();
            case "PAYMENT_COMPLETED":
                return "Payment confirmed for booking #" + booking.getId() + ". See you at your session!";
            case "BOOKING_CANCELLED":
                return "Your booking #" + booking.getId() + " has been cancelled.";
            case "BOOKING_CANCELLED_AFTER_PAYMENT":
                return "Your booking #" + booking.getId() + " was cancelled. A refund will be processed.";
            case "BOOKING_COMPLETED":
                return "Your consulting session (booking #" + booking.getId() + ") is complete. Thank you!";
            default:
                return null;    // No notifications for other events
        }
    }
}
