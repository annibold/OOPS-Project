package com.itsm.demo.service;

import com.itsm.demo.model.*;
import com.itsm.demo.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class IncidentSearchService {

    public record Criteria(Long id, IncidentStatus status, Priority priority, Category category,
                           String technician, SlaStatus sla, LocalDate from, LocalDate to) { }

    public record Row(Incident incident, SlaService.SlaInfo sla) { }

    private final IncidentRepository incidentRepository;
    private final SlaService slaService;

    public IncidentSearchService(IncidentRepository incidentRepository, SlaService slaService) {
        this.incidentRepository = incidentRepository;
        this.slaService = slaService;
    }

    public List<Row> search(Criteria c) {
        LocalDateTime now = LocalDateTime.now();
        return incidentRepository.findAll().stream()
                .map(i -> new Row(i, slaService.evaluate(i, now)))
                .filter(r -> c.id() == null || c.id().equals(r.incident().getId()))
                .filter(r -> c.status() == null || c.status() == r.incident().getStatus())
                .filter(r -> c.priority() == null || c.priority() == r.incident().getPriority())
                .filter(r -> c.category() == null || c.category() == r.incident().getCategory())
                .filter(r -> matchesTechnician(r.incident(), c.technician()))
                .filter(r -> c.sla() == null || (r.sla() != null && r.sla().status() == c.sla()))
                .filter(r -> c.from() == null || !r.incident().getCreatedAt().toLocalDate().isBefore(c.from()))
                .filter(r -> c.to() == null || !r.incident().getCreatedAt().toLocalDate().isAfter(c.to()))
                .sorted(Comparator.comparing((Row r) -> r.incident().getId()).reversed())
                .toList();
    }

    private boolean matchesTechnician(Incident i, String text) {
        if (text == null || text.isBlank()) return true;
        Technician t = i.getAssignedTechnician();
        if (t == null) return false;
        String q = text.trim().toLowerCase();
        return t.getName().toLowerCase().contains(q) || t.getEmail().toLowerCase().contains(q);
    }
}
