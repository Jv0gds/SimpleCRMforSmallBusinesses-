package com.crm.controller;

import com.crm.dto.LeadDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    // Endpoint to get dashboard KPIs
    @GetMapping("/dashboard/kpis")
    public ResponseEntity<?> getDashboardKpis() {
        // Placeholder implementation
        Map<String, String> kpis = Map.of(
            "monthlySales", "$12,345",
            "newLeads", "67",
            "pendingTasks", "12"
        );
        return ResponseEntity.ok(kpis);
    }

    // Endpoint to get user's to-do items
    @GetMapping("/dashboard/todos")
    public ResponseEntity<?> getTodoItems() {
        // Placeholder implementation
        return ResponseEntity.ok(Collections.singletonList(Map.of("task", "Follow up with John Doe", "status", "pending")));
    }

    // Endpoint to get recent leads for the dashboard
    @GetMapping("/dashboard/leads")
    public ResponseEntity<?> getRecentLeads() {
        // Placeholder implementation
        return ResponseEntity.ok(Collections.singletonList(Map.of(
            "name", "Jane Smith",
            "company", "Acme Corp",
            "title", "CTO",
            "status", "New",
            "owner", "Sales Rep"
        )));
    }

    // Endpoint to create a new lead
    @PostMapping("/leads")
    public ResponseEntity<?> createLead(@Valid @RequestBody LeadDto leadDto) {
        // Placeholder implementation
        System.out.println("New lead created: " + leadDto);
        return ResponseEntity.ok(Map.of("message", "Lead created successfully", "lead", leadDto));
    }
}
