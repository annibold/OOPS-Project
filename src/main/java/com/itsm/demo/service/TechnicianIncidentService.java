package com.itsm.demo.service;

import com.itsm.demo.exception.InvalidIncidentException;
import com.itsm.demo.model.*;
import com.itsm.demo.observer.IncidentEventPublisher;
import com.itsm.demo.repository.IncidentNoteRepository;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.repository.TechnicianRepository;
import com.itsm.demo.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TechnicianIncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentNoteRepository noteRepository;
    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;
    private final IncidentEventPublisher events;

    public TechnicianIncidentService(IncidentRepository incidentRepository,
                                     IncidentNoteRepository noteRepository,
                                     TechnicianRepository technicianRepository,
                                     UserRepository userRepository,
                                     IncidentEventPublisher events) {
        this.incidentRepository = incidentRepository;
        this.noteRepository = noteRepository;
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
        this.events = events;
    }

    private Technician technician(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Unknown user"));
        if (user instanceof Technician t) {
            return t;
        }
        throw new AccessDeniedException("Only technicians can do this");
    }

    /** Loads the incident and makes sure it is assigned to this technician. */
    private Incident ownedIncident(Long id, String email) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new InvalidIncidentException("Incident " + id + " not found"));
        Technician assigned = incident.getAssignedTechnician();
        if (assigned == null || !assigned.getEmail().equals(email)) {
            throw new AccessDeniedException("This incident is not assigned to you");
        }
        return incident;
    }

    public List<Incident> findAssigned(String email) {
        return incidentRepository.findByAssignedTechnician(technician(email));
    }

    public int workload(String email) {
        return technician(email).getActiveIncidents();
    }

    public Incident getIncident(Long id, String email) {
        return ownedIncident(id, email);
    }

    public List<IncidentNote> getNotes(Incident incident) {
        return noteRepository.findByIncidentOrderByCreatedAtAsc(incident);
    }

    @Transactional
    public void acknowledge(Long id, String email) {
        Incident incident = ownedIncident(id, email);
        String previous = incident.getStatus().name();
        incident.changeStatus(IncidentStatus.ACKNOWLEDGED);
        incidentRepository.save(incident);
        events.publish(incident, email, "INCIDENT_ACKNOWLEDGED", previous, "ACKNOWLEDGED",
                "Technician " + incident.getAssignedTechnician().getName() + " acknowledged the incident");
    }

    @Transactional
    public void startWork(Long id, String email) {
        Incident incident = ownedIncident(id, email);
        String previous = incident.getStatus().name();
        incident.changeStatus(IncidentStatus.IN_PROGRESS);
        incidentRepository.save(incident);
        events.publish(incident, email, "INVESTIGATION_STARTED", previous, "IN_PROGRESS",
                "Investigation started");
    }

    @Transactional
    public void addNote(Long id, String email, String text) {
        Incident incident = ownedIncident(id, email);
        if (text == null || text.isBlank()) {
            throw new InvalidIncidentException("Note cannot be empty");
        }
        noteRepository.save(new IncidentNote(incident, email, text.trim()));
        String state = incident.getStatus().name();
        events.publish(incident, email, "NOTE_ADDED", state, state,
                "Investigation note added");
    }

    @Transactional
    public void resolve(Long id, String email, String resolution) {
        Incident incident = ownedIncident(id, email);
        if (resolution == null || resolution.isBlank()) {
            throw new InvalidIncidentException("Resolution details are required");
        }
        String previous = incident.getStatus().name();
        incident.changeStatus(IncidentStatus.RESOLVED);   // fails unless IN_PROGRESS
        incident.setResolution(resolution.trim());
        incidentRepository.save(incident);

        Technician tech = incident.getAssignedTechnician();
        tech.decrementLoad();
        technicianRepository.save(tech);

        events.publish(incident, email, "INCIDENT_RESOLVED", previous, "RESOLVED",
                "Incident resolved: " + resolution.trim());
    }
}
