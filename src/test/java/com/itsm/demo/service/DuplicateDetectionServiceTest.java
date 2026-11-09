package com.itsm.demo.service;

import com.itsm.demo.model.Category;
import com.itsm.demo.model.Employee;
import com.itsm.demo.model.Incident;
import com.itsm.demo.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DuplicateDetectionServiceTest {

    private IncidentRepository incidentRepository;
    private DuplicateDetectionService duplicateService;

    @BeforeEach
    void setUp() {
        incidentRepository = Mockito.mock(IncidentRepository.class);
        duplicateService = new DuplicateDetectionService(incidentRepository);
    }

    private Incident buildIncident(Long id, String title, String desc, Category cat) throws Exception {
        Employee emp = new Employee("User", "u@test.com", "pass");
        Incident inc = new Incident(title, desc, emp, 2, 2, 2);
        inc.setCategory(cat);
        Field idField = Incident.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(inc, id);
        return inc;
    }

    @Test
    void detectsDuplicatePaymentIncident() throws Exception {
        Incident existing = buildIncident(101L, "Payment gateway timeout", "Users cannot process payment transactions", Category.APPLICATION);
        Incident incoming = buildIncident(102L, "Payment transactions failing", "Unable to complete payment in checkout", Category.APPLICATION);

        when(incidentRepository.findAll()).thenReturn(List.of(existing));

        var duplicates = duplicateService.findDuplicates(incoming);

        assertFalse(duplicates.isEmpty());
        assertEquals(101L, duplicates.get(0).incident().getId());
        assertTrue(duplicates.get(0).similarityScore() >= 0.30);
    }

    @Test
    void distinctIncidentsAreNotFlagged() throws Exception {
        Incident printerInc = buildIncident(101L, "Office printer out of toner", "Floor 3 printer error", Category.HARDWARE);
        Incident paymentInc = buildIncident(102L, "Payment application unavailable", "Users unable to complete payment", Category.APPLICATION);

        when(incidentRepository.findAll()).thenReturn(List.of(printerInc));

        var duplicates = duplicateService.findDuplicates(paymentInc);

        assertTrue(duplicates.isEmpty());
    }
}
