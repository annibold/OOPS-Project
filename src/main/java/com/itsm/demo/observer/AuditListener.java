package com.itsm.demo.observer;

import com.itsm.demo.model.AuditRecord;
import com.itsm.demo.repository.AuditRecordRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Observer 1: records every event in the audit trail. */
@Component
public class AuditListener {

    private final AuditRecordRepository repository;

    public AuditListener(AuditRecordRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void on(IncidentEvent e) {
        repository.save(new AuditRecord(e.actor(), e.action(), e.incidentId(),
                e.previousState(), e.newState(), e.description()));
    }
}
