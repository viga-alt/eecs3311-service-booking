package com.booking.controller;

import com.booking.domain.*;
import com.booking.service.BookingService;
import com.booking.service.ConsultantService;
import com.booking.singleton.DatabaseManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ConsultantService consultantService;
    private final DatabaseManager db = DatabaseManager.getInstance();

    public BookingController(BookingService bookingService, ConsultantService consultantService) {
        this.bookingService = bookingService;
        this.consultantService = consultantService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Integer> request) {
        int clientId = request.get("clientId");
        int consultantId = request.get("consultantId");
        int slotId = request.get("slotId");
        int serviceId = request.get("serviceId");

        User user = db.findUserById(clientId);
        if (!(user instanceof Client)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid client"));
        }
        Client client = (Client) user;

        Consultant consultant = consultantService.getConsultantById(consultantId);
        if (consultant == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Consultant not found"));
        }

        TimeSlot slot = consultant.findSlotById(slotId);
        if (slot == null || !slot.isAvailable()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Slot not available"));
        }

        Service service = consultant.getOfferedServices().stream()
                .filter(s -> s.getId() == serviceId)
                .findFirst().orElse(null);
        if (service == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Service not found for this consultant"));
        }

        Booking booking = bookingService.createBooking(client, consultant, service, slot);
        if (booking == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Could not create booking"));
        }
        return ResponseEntity.ok(toMap(booking));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getClientBookings(@PathVariable int clientId) {
        List<Map<String, Object>> result = bookingService.getBookingHistory(clientId).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/client/{clientId}/pending-payment")
    public ResponseEntity<?> getPendingPaymentBookings(@PathVariable int clientId) {
        List<Map<String, Object>> result = bookingService.getBookingHistory(clientId).stream()
                .filter(b -> "PENDING_PAYMENT".equals(b.getCurrentStateName()))
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{bookingId}/client/{clientId}")
    public ResponseEntity<?> cancelBooking(@PathVariable int bookingId, @PathVariable int clientId) {
        double refund = bookingService.cancelBooking(bookingId, clientId);
        return ResponseEntity.ok(Map.of("refund", refund, "message", String.format("Booking cancelled. Refund: $%.2f", refund)));
    }

    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<?> getConsultantBookings(@PathVariable int consultantId) {
        List<Map<String, Object>> result = bookingService.getBookingsForConsultant(consultantId).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/consultant/{consultantId}/pending")
    public ResponseEntity<?> getPendingBookings(@PathVariable int consultantId) {
        List<Map<String, Object>> result = bookingService.getPendingBookingsForConsultant(consultantId).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{bookingId}/accept")
    public ResponseEntity<?> acceptBooking(@PathVariable int bookingId, @RequestBody Map<String, Integer> request) {
        int consultantId = request.get("consultantId");
        bookingService.acceptBooking(bookingId, consultantId);
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Booking not found"));
        }
        return ResponseEntity.ok(toMap(booking));
    }

    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable int bookingId, @RequestBody Map<String, Integer> request) {
        int consultantId = request.get("consultantId");
        bookingService.rejectBooking(bookingId, consultantId);
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Booking not found"));
        }
        return ResponseEntity.ok(toMap(booking));
    }

    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable int bookingId, @RequestBody Map<String, Integer> request) {
        int consultantId = request.get("consultantId");
        bookingService.completeBooking(bookingId, consultantId);
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Booking not found"));
        }
        return ResponseEntity.ok(toMap(booking));
    }

    private Map<String, Object> toMap(Booking b) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", b.getId());
        map.put("clientId", b.getClient().getId());
        map.put("clientName", b.getClient().getName());
        map.put("consultantId", b.getConsultant().getId());
        map.put("consultantName", b.getConsultant().getName());
        map.put("serviceName", b.getService().getName());
        map.put("serviceId", b.getService().getId());
        map.put("state", b.getCurrentStateName());
        map.put("totalPrice", b.getTotalPrice());
        map.put("createdAt", b.getCreatedAt().toString());
        if (b.getTimeSlot() != null) {
            map.put("timeSlotStart", b.getTimeSlot().getStartTime().toString());
            map.put("timeSlotEnd", b.getTimeSlot().getEndTime().toString());
        }
        return map;
    }
}
