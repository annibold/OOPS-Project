package com.itsm.demo.service;

import com.itsm.demo.exception.InvalidIncidentException;
import com.itsm.demo.model.*;
import com.itsm.demo.observer.IncidentEventPublisher;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.repository.TechnicianRepository;
import com.itsm.demo.strategy.PriorityStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final PriorityStrategy priorityStrategy;
    private final IncidentEventPublisher events;
    private final TechnicianRepository technicianRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           PriorityStrategy priorityStrategy,
                           IncidentEventPublisher events,
                           TechnicianRepository technicianRepository) {
        this.incidentRepository = incidentRepository;
        this.priorityStrategy = priorityStrategy;
        this.events = events;
        this.technicianRepository = technicianRepository;
    }

    @Transactional
    public Incident createIncident(String title, String description, User reporter,
                                   Category category, int impact, int urgency,
                                   int businessCriticality) {
        if (title == null || title.isBlank()) {
            throw new InvalidIncidentException("Title is required");
        }
        validateRange("impact", impact);
        validateRange("urgency", urgency);
        validateRange("business criticality", businessCriticality);

        Incident incident = new Incident(title.trim(), description, reporter,
                                         impact, urgency, businessCriticality);
        incident.setCategory(category);
        incident.setPriority(priorityStrategy.calculate(incident));
        incident.changeStatus(IncidentStatus.CLASSIFIED);
        Incident saved = incidentRepository.save(incident);

        events.publish(saved, reporter.getEmail(), "INCIDENT_CREATED",
                null, "NEW", "Incident created by " + reporter.getName());
        events.publish(saved, "SYSTEM", "PRIORITY_CALCULATED",
                "NEW", "CLASSIFIED",
                "Classified as " + category + ", priority calculated as " + saved.getPriority());
        return saved;
    }

    @Transactional
    public Incident save(Incident incident) {
        return incidentRepository.save(incident);
    }

    @Transactional
    public void verify(Incident incident, String userEmail) {
        String previous = incident.getStatus().name();
        incident.changeStatus(IncidentStatus.VERIFIED);
        incidentRepository.save(incident);
        events.publish(incident, userEmail, "INCIDENT_VERIFIED", previous, "VERIFIED",
                "Resolution verified by employee");
    }

    @Transactional
    public void reopen(Incident incident, String userEmail) {
        String previous = incident.getStatus().name();
        incident.changeStatus(IncidentStatus.IN_PROGRESS);
        incidentRepository.save(incident);

        Technician tech = incident.getAssignedTechnician();
        if (tech != null) {
            tech.incrementLoad();
            technicianRepository.save(tech);
        }

        events.publish(incident, userEmail, "INCIDENT_REOPENED", previous, "IN_PROGRESS",
                "Resolution rejected by employee, investigation reopened");
    }

    @Transactional
    public void close(Long id, String adminEmail) {
        Incident incident = findById(id);
        String previous = incident.getStatus().name();
        incident.changeStatus(IncidentStatus.CLOSED);
        incidentRepository.save(incident);
        events.publish(incident, adminEmail, "INCIDENT_CLOSED", previous, "CLOSED",
                "Incident formally closed by admin " + adminEmail);
    }

    public List<Incident> findByReporter(User reporter) {
        return incidentRepository.findByReporter(reporter);
    }

    public Incident findById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new InvalidIncidentException("Incident " + id + " not found"));
    }

    private void validateRange(String field, int value) {
        if (value < 1 || value > 3) {
            throw new InvalidIncidentException(field + " must be between 1 and 3");
        }
    }
}
