package com.booking.domain;

import com.booking.domain.state.BookingState;
import com.booking.domain.state.RequestedState;
import com.booking.observer.BookingObserver;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/*
 * Context (State) + Subject (Observer)
 * STATE PATTERN, Context class
 * Booking holds the currentState and delegates all lifecycle transitions to it
 *
 * OBSERVER PATTERN, Subject class
 * Booking maintains a list of observers and notifies them on every state change event
 *
 * Lifecycle: REQUESTED -> CONFIRMED -> PENDING_PAYMENT -> PAID -> COMPLETED
 *            REQUESTED -> REJECTED
 *            Any active state -> CANCELLED (per policy)
 */
public class Booking {
  private int id;
  private Client client;
  private Consultant consultant;
  private Service service;
  private TimeSlot timeSlot;
  private Payment payment;
  private LocalDateTime createdAt;

  // Current concrete state of the booking
  private BookingState currentState;

  // Observers interested in state changes
  private final List<BookingObserver> observers = new ArrayList<>();

  public Booking(int id, Client client, Consultant consultant, Service service, TimeSlot timeSlot) {
    this.id = id;
    this.client = client;
    this.consultant = consultant;
    this.service = service;
    this.timeSlot = timeSlot;
    this.createdAt = LocalDateTime.now();
    this.currentState = new RequestedState(); // Initial state
  }

  // State delegation, the following methods delegate to the current state implementation

  public void confirm() {
    currentState.confirm(this);
  }

  public void reject() {
    currentState.reject(this);
  }

  public void markPendingPayment() {
    currentState.markPendingPayment(this);
  }

  public void markPaid() {
    currentState.markPaid(this);
  }

  public void cancel() {
    currentState.cancel(this);
  }

  public void complete() {
    currentState.complete(this);
  }

  // Called by concrete state objects to transition to the next state
  public void setState(BookingState newState) {
    this.currentState = newState;
  }

  public String getCurrentStateName() {
    return currentState.getStateName();
  }

  // Observer management
  public void addObserver(BookingObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(BookingObserver observer) {
    observers.remove(observer);
  }

  // Notify all observers of a state‑change event
  public void notifyObservers(String event) {
    for (BookingObserver observer : observers) {
      observer.update(this, event);
    }
  }

  // Getters, setters
  public double getTotalPrice() {
    return service != null ? service.getBasePrice() : 0.0;
  }

  public int getId() {
    return id;
  }

  public Client getClient() {
    return client;
  }

  public Consultant getConsultant() {
    return consultant;
  }

  public Service getService() {
    return service;
  }

  public TimeSlot getTimeSlot() {
    return timeSlot;
  }

  public Payment getPayment() {
    return payment;
  }

  public void setPayment(Payment payment) {
    this.payment = payment;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public BookingState getCurrentState() {
    return currentState;
  }

  // toString Object representation
  @Override
  public String toString() {
    return String.format(
        "Booking[id=%d, client=%s, consultant=%s, service=%s, state=%s]",
        id, client.getName(), consultant.getName(), service.getName(), currentState.getStateName());
  }
}
