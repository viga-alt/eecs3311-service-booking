package com.booking.controller;

import com.booking.domain.Consultant;
import com.booking.domain.Service;
import com.booking.domain.TimeSlot;
import com.booking.service.ConsultantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consultants")
public class ConsultantController {

    private final ConsultantService consultantService;

    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedConsultants() {
        List<Map<String, Object>> result = consultantService.getAllApprovedConsultants().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<?> getAvailableSlots(@PathVariable int id) {
        List<Map<String, Object>> result = consultantService.getAvailableSlots(id).stream()
                .map(this::slotToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/slots")
    public ResponseEntity<?> addSlot(@PathVariable int id, @RequestBody Map<String, Object> request) {
        int days = ((Number) request.get("daysFromToday")).intValue();
        int startHour = ((Number) request.get("startHour")).intValue();
        int duration = ((Number) request.get("durationHours")).intValue();

        int slotId = (int) (System.currentTimeMillis() % 100000);
        TimeSlot slot = new TimeSlot(slotId,
                LocalDateTime.now().plusDays(days).withHour(startHour).withMinute(0).withSecond(0),
                LocalDateTime.now().plusDays(days).withHour(startHour + duration).withMinute(0).withSecond(0));
        consultantService.addSlot(id, slot);
        return ResponseEntity.ok(slotToMap(slot));
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<?> getConsultantServices(@PathVariable int id) {
        Consultant consultant = consultantService.getConsultantById(id);
        if (consultant == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Consultant not found"));
        }
        List<Map<String, Object>> result = consultant.getOfferedServices().stream()
                .map(this::serviceToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toMap(Consultant c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("name", c.getName());
        map.put("email", c.getEmail());
        map.put("specialization", c.getSpecialization());
        map.put("approved", c.isApproved());
        return map;
    }

    private Map<String, Object> slotToMap(TimeSlot ts) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", ts.getId());
        map.put("startTime", ts.getStartTime().toString());
        map.put("endTime", ts.getEndTime().toString());
        map.put("available", ts.isAvailable());
        return map;
    }

    private Map<String, Object> serviceToMap(Service s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", s.getId());
        map.put("name", s.getName());
        map.put("durationMinutes", s.getDurationMinutes());
        map.put("basePrice", s.getBasePrice());
        map.put("description", s.getDescription());
        return map;
    }
}
