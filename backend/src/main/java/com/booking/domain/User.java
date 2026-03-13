package com.booking.domain;

/*
 * Abstract base class for all users in the system
 * Represents the common attributes and behaviors shared by Client, Consultant, and Admin
 */
public abstract class User {
  protected int id; // Unique identifier
  protected String name; // User full name
  protected String email; // Login email address (acts as username)
  protected String password; // User password
  protected String role; // User role (CLIENT, CONSULTANT, ADMIN)

  public User(int id, String name, String email, String password, String role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  // Authenticate user (verify login credentials)
  public boolean login(String email, String password) {
    return this.email.equals(email) && this.password.equals(password);
  }

  // Log out notification
  public void logout() {
    System.out.println(name + " has logged out.");
  }

  /* Getter Methods */

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getRole() {
    return role;
  }

  @Override
  public String toString() {
    return String.format("[%s] %s (id=%d, email=%s)", role, name, id, email);
  }
}
