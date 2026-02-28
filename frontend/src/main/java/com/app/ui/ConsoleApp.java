package com.app.ui;

import java.util.*;

public class ConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static Object currentUser = null; // typed later

    public static void main(String[] args) {
        System.out.println("--- Service Booking & Consulting Platform ---");

        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                // role dispatch placeholder
                System.out.println("Logged in. Role menus coming soon.");
                currentUser = null; // force logout for now
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\nLOGIN");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (email.equals("0")) { System.out.println("Goodbye!"); System.exit(0); }
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        // TODO: wire to DatabaseManager once services are initialized
        System.out.println("Login not yet implemented.");
    }

    private static int readInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}