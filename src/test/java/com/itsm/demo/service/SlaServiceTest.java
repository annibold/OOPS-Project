package com.itsm.demo.service;

import com.itsm.demo.model.*;
import com.itsm.demo.observer.IncidentEventPublisher;
import com.itsm.demo.repository.AuditRecordRepository;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.repository.SlaEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SlaServiceTest {

    private IncidentRepository incidentRepository;
    private AuditRecordRepository auditRepository;
    private SlaEventRepository slaEventRepository;
    private IncidentEventPublisher eventPublisher;
    private SlaService slaService;

    @BeforeEach
    void setUp() {
        incidentRepository = Mockito.mock(IncidentRepository.class);
        auditRepository = Mockito.mock(AuditRecordRepository.class);
        slaEventRepository = Mockito.mock(SlaEventRepository.class);
        eventPublisher = Mockito.mock(IncidentEventPublisher.class);

        slaService = new SlaService(incidentRepository, auditRepository, slaEventRepository, eventPublisher);
    }

    private Incident createIncident(Priority priority) {
        Employee emp = new Employee("Test User", "test@example.com", "hash");
        Incident incident = new Incident("Database down", "Connection timeout", emp, 3, 3, 3);
        incident.setPriority(priority);
        return incident;
    }

    @Test
    void p1IncidentDeadlinesAreCorrect() {
        Incident incident = createIncident(Priority.P1);
        when(auditRepository.findByIncidentIdOrderByTimestampAsc(incident.getId()))
                .thenReturn(Collections.emptyList());

        SlaService.SlaInfo info = slaService.evaluate(incident, incident.getCreatedAt());

        assertNotNull(info);
        assertEquals(incident.getCreatedAt().plusMinutes(5), info.responseDeadline());
        assertEquals(incident.getCreatedAt().plusMinutes(30), info.resolutionDeadline());
        assertEquals(SlaStatus.ON_TRACK, info.status());
    }

    @Test
    void evaluatesToAtRiskWhenEightyPercentTimeElapsed() {
        Incident incident = createIncident(Priority.P1);

        // Simulate incident acknowledged in 2 minutes (response deadline met)
        AuditRecord ackAudit = Mockito.mock(AuditRecord.class);
        when(ackAudit.getAction()).thenReturn("INCIDENT_ACKNOWLEDGED");
        when(ackAudit.getTimestamp()).thenReturn(incident.getCreatedAt().plusMinutes(2));

        when(auditRepository.findByIncidentIdOrderByTimestampAsc(incident.getId()))
                .thenReturn(List.of(ackAudit));

        // 80% of 30 mins is 24 mins. Evaluate at 25 mins later:
        LocalDateTime atRiskTime = incident.getCreatedAt().plusMinutes(25);
        SlaService.SlaInfo info = slaService.evaluate(incident, atRiskTime);

        assertEquals(SlaStatus.AT_RISK, info.status());
        assertFalse(info.responseViolated());
        assertFalse(info.resolutionViolated());
    }

    @Test
    void evaluatesToViolatedWhenResolutionDeadlinePassed() {
        Incident incident = createIncident(Priority.P1);
        when(auditRepository.findByIncidentIdOrderByTimestampAsc(incident.getId()))
                .thenReturn(Collections.emptyList());

        // Past 30 mins:
        LocalDateTime violatedTime = incident.getCreatedAt().plusMinutes(31);
        SlaService.SlaInfo info = slaService.evaluate(incident, violatedTime);

        assertEquals(SlaStatus.VIOLATED, info.status());
        assertTrue(info.resolutionViolated());
    }
}