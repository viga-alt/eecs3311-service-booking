package com.booking.observer;

import com.booking.domain.Booking;

/* OBSERVER PATTERN - Observer interface
 * All notification observers implement this interface
 * Booking (subject) calls notifyObservers(event) which triggers update() on each registered observer
 */
public interface BookingObserver {
    void update(Booking booking, String event);
}
