package com.itsm.demo.service;

import com.itsm.demo.exception.TechnicianUnavailableException;
import com.itsm.demo.model.*;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.repository.TechnicianRepository;
import com.itsm.demo.strategy.AssignmentStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
public class AssignmentService {

    private final Map<String, AssignmentStrategy> strategies; // "skill", "load"
    private final TechnicianRepository technicianRepository;
    private final IncidentRepository incidentRepository;
    private String activeStrategy = "skill";   // admin can change this

    public AssignmentService(Map<String, AssignmentStrategy> strategies,
                             TechnicianRepository technicianRepository,
                             IncidentRepository incidentRepository) {
        this.strategies = strategies;
        this.technicianRepository = technicianRepository;
        this.incidentRepository = incidentRepository;
    }

    public void setActiveStrategy(String name) {
        if (!strategies.containsKey(name)) {
            throw new IllegalArgumentException("Unknown strategy: " + name);
        }
        this.activeStrategy = name;
    }

    public String getActiveStrategy() { return activeStrategy; }

    @Transactional
    public Incident assign(Incident incident) {
        AssignmentStrategy strategy = strategies.get(activeStrategy);
        Technician tech = strategy
                .select(incident, technicianRepository.findAll())
                .orElseThrow(() -> new TechnicianUnavailableException(
                        "No suitable technician available for incident " + incident.getId()));

        incident.setAssignedTechnician(tech);
        incident.changeStatus(IncidentStatus.ASSIGNED);
        tech.incrementLoad();
        technicianRepository.save(tech);
        return incidentRepository.save(incident);
    }
}
