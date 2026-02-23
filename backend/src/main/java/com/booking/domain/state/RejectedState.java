package com.booking.domain.state;

import com.booking.domain.Booking;

/* STATE PATTERN - RejectedState
 * Terminal state: consultant declined the booking
 * No further transitions allowed
 */
public class RejectedState implements BookingState {

    @Override
    public void confirm(Booking booking) {
        System.out.println("[State] Cannot confirm — booking #" + booking.getId() + " was already rejected.");
    }

    @Override
    public void reject(Booking booking) {
        System.out.println("[State] Booking #" + booking.getId() + " is already rejected.");
    }

    @Override
    public void markPendingPayment(Booking booking) {
        System.out.println("[State] Cannot process payment — booking was rejected.");
    }

    @Override
    public void markPaid(Booking booking) {
        System.out.println("[State] Cannot mark paid — booking was rejected.");
    }

    @Override
    public void cancel(Booking booking) {
        System.out.println("[State] Booking #" + booking.getId() + " is already rejected, cancellation not applicable.");
    }

    @Override
    public void complete(Booking booking) {
        System.out.println("[State] Cannot complete — booking was rejected.");
    }

    @Override
    public String getStateName() { 
        return "REJECTED"; 
    }
}
