package com.booking.observer;

/* Service responsible for sending notifications, used by all concrete observer implementations
 */
public class NotificationService {
    private String channel; // EMAIL, IN_APP, or BOTH

    public NotificationService(String channel) {
        this.channel = channel;
    }

    // Send a plain email notification
    public void sendEmail(String to, String message) {
        System.out.println("[EMAIL -> " + to + "]: " + message);
    }

    // Send an in‑app notification
    public void sendInApp(int userId, String message) {
        System.out.println("[IN-APP -> userId=" + userId + "]: " + message);
    }

    // Send a notification via the configured channel(s)
    public void send(String to, int userId, String message) {
        if ("EMAIL".equals(channel) || "BOTH".equals(channel)) {
            sendEmail(to, message);
        }
        if ("IN_APP".equals(channel) || "BOTH".equals(channel)) {
            sendInApp(userId, message);
        }
    }

    // Getters & Setters

    public String getChannel() { 
        return channel; 
    }

    public void setChannel(String channel) { 
        this.channel = channel; 
    }
}
