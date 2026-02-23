package com.booking.domain;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/*
 * Represents a payment method stored on a client's account
 * Supports: CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
 */

public class PaymentMethod {
  private int id; // Unique identifier
  private String type; // CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
  private Map<String, String> details; // key/value pairs (e.g., cardNumber, expiry, cvv, email)
  private boolean isDefault; // Indicates if this is the client's default method

  public PaymentMethod(int id, String type) {
    this.id = id;
    this.type = type;
    this.details = new HashMap<>();
    this.isDefault = false;
  }

  // Add or update a detail entry
  public void addDetail(String key, String value) {
    details.put(key, value);
  }

  // Retrieve a detail, empty string if absent
  public String getDetail(String key) {
    return details.getOrDefault(key, "");
  }

  /* Getter, Setter Methods */

  public int getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public Map<String, String> getDetails() {
    return details;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  // toString Object representation
  @Override
  public String toString() {
    return String.format("PaymentMethod[id=%d, type=%s, default=%b]", id, type, isDefault);
  }
}
