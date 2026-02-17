package com.booking.domain;

// Represents a platform administrator
public class Admin extends User {

  public Admin(int id, String name, String email, String password) {
    super(id, name, email, password, "ADMIN");
  }

  @Override
  public String toString() {
    return String.format("[Admin] %s (id=%d, email=%s)", name, id, email);
  }
}
