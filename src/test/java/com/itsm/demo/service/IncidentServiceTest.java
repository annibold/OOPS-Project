package com.itsm.demo.service;

import com.itsm.demo.exception.InvalidIncidentException;
import com.itsm.demo.model.*;
import com.itsm.demo.observer.IncidentEventPublisher;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.repository.TechnicianRepository;
import com.itsm.demo.strategy.ScoreBasedPriorityStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncidentServiceTest {

    private IncidentRepository incidentRepository;
    private TechnicianRepository technicianRepository;
    private IncidentEventPublisher eventPublisher;
    private IncidentService incidentService;
    private Employee employee;

    @BeforeEach
    void setUp() {
        incidentRepository = Mockito.mock(IncidentRepository.class);
        technicianRepository = Mockito.mock(TechnicianRepository.class);
        eventPublisher = Mockito.mock(IncidentEventPublisher.class);
        incidentService = new IncidentService(
                incidentRepository,
                new ScoreBasedPriorityStrategy(),
                eventPublisher,
                technicianRepository
        );
        employee = new Employee("John Doe", "john@example.com", "pass");
    }

    @Test
    void createIncidentSuccessfullySetsPriorityAndClassified() {
        when(incidentRepository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        Incident incident = incidentService.createIncident(
                "VPN Connection Down", "Cannot connect to company VPN",
                employee, Category.NETWORK, 3, 2, 2);

        assertNotNull(incident);
        assertEquals(IncidentStatus.CLASSIFIED, incident.getStatus());
        assertEquals(Priority.P2, incident.getPriority());
        verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    void blankTitleThrowsException() {
        assertThrows(InvalidIncidentException.class, () ->
                incidentService.createIncident("   ", "Description", employee, Category.NETWORK, 1, 1, 1));
    }

    @Test
    void impactOutOfBoundsThrowsException() {
        assertThrows(InvalidIncidentException.class, () ->
                incidentService.createIncident("Valid Title", "Description", employee, Category.NETWORK, 0, 1, 1));

        assertThrows(InvalidIncidentException.class, () ->
                incidentService.createIncident("Valid Title", "Description", employee, Category.NETWORK, 4, 1, 1));
    }

    @Test
    void reopenIncidentIncrementsTechnicianLoad() {
        Incident incident = new Incident("Bug", "Details", employee, 2, 2, 2);
        Technician tech = new Technician("Ravi", "ravi@itsm.com", "pass", Set.of(Category.NETWORK), 5);
        incident.setAssignedTechnician(tech);
        incident.changeStatus(IncidentStatus.CLASSIFIED);
        incident.changeStatus(IncidentStatus.ASSIGNED);
        incident.changeStatus(IncidentStatus.ACKNOWLEDGED);
        incident.changeStatus(IncidentStatus.IN_PROGRESS);
        incident.changeStatus(IncidentStatus.RESOLVED);

        int loadBefore = tech.getActiveIncidents();

        incidentService.reopen(incident, "john@example.com");

        assertEquals(IncidentStatus.IN_PROGRESS, incident.getStatus());
        assertEquals(loadBefore + 1, tech.getActiveIncidents());
        verify(technicianRepository).save(tech);
    }

    @Test
    void verify_recordsFeedbackAndRating() {
        User reporter = new Employee("Employee User", "emp@itsm.com", "pass");
        Incident incident = new Incident("Test Issue", "Desc", reporter, 2, 2, 2);
        incident.changeStatus(IncidentStatus.CLASSIFIED);
        incident.changeStatus(IncidentStatus.ASSIGNED);
        incident.changeStatus(IncidentStatus.ACKNOWLEDGED);
        incident.changeStatus(IncidentStatus.IN_PROGRESS);
        incident.changeStatus(IncidentStatus.RESOLVED);

        incidentService.verify(incident, "emp@itsm.com", "Resolved quickly and effectively!", 5);

        assertEquals(IncidentStatus.VERIFIED, incident.getStatus());
        assertEquals("Resolved quickly and effectively!", incident.getFeedback());
        assertEquals(5, incident.getRating());
        verify(incidentRepository).save(incident);
    }
}