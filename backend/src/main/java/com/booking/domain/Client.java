package com.booking.domain;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

// Represents a client
// Can book consulting services
public class Client extends User {

  // Stores payment methods for the client
  private List<PaymentMethod> paymentMethods;

  // Constructor
  public Client(int id, String name, String email, String password) {
    super(id, name, email, password, "CLIENT");

    // initialize payment methods list
    this.paymentMethods = new ArrayList<>();
  }

  // Adds a new payment method, the first one becomes the default
  public void addPaymentMethod(PaymentMethod method) {
    if (paymentMethods.isEmpty()) {
      method.setDefault(true); // First method is the default
    }
    paymentMethods.add(method);
    System.out.println("[Client] Payment method added: " + method);
  }

  // Removes a payment method by its id
  public boolean removePaymentMethod(int methodId) {
    boolean removed = paymentMethods.removeIf(pm -> pm.getId() == methodId);
    if (removed) {
      System.out.println("[Client] Payment method #" + methodId + " removed.");
      // Re‑assign default if the removed method was default
      if (!paymentMethods.isEmpty()
          && paymentMethods.stream().noneMatch(PaymentMethod::isDefault)) {
        paymentMethods.get(0).setDefault(true);
      }
    }
    return removed;
  }

  // Returns the payment method that matches the given id
  // Return null if not found
  public PaymentMethod getPaymentMethodById(int id) {
    return paymentMethods.stream().filter(pm -> pm.getId() == id).findFirst().orElse(null);
  }

  // Retrieves the currently default payment method
  // Return null if not found
  public PaymentMethod getDefaultPaymentMethod() {
    return paymentMethods.stream().filter(PaymentMethod::isDefault).findFirst().orElse(null);
  }

  // Sets the specified payment method as the default
  public void setDefaultPaymentMethod(int methodId) {
    paymentMethods.forEach(pm -> pm.setDefault(pm.getId() == methodId));
  }

  // Returns deep copy of payment methods list
  public List<PaymentMethod> getPaymentMethods() {
    return new ArrayList<>(paymentMethods);
  }

  @Override
  public String toString() {
    return String.format("[Client] %s (id=%d, email=%s)", id, name, email);
  }
}
