package com.booking.controller;

import com.booking.domain.Service;
import com.booking.service.ConsultantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ConsultantService consultantService;

    public ServiceController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping
    public ResponseEntity<?> getAllServices() {
        List<Map<String, Object>> result = consultantService.getAllServices().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toMap(Service s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", s.getId());
        map.put("name", s.getName());
        map.put("durationMinutes", s.getDurationMinutes());
        map.put("basePrice", s.getBasePrice());
        map.put("description", s.getDescription());
        return map;
    }
}
