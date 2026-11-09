package com.itsm.demo.repository;

import com.itsm.demo.model.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {
    List<AuditRecord> findAllByOrderByTimestampDesc();
    List<AuditRecord> findByIncidentIdOrderByTimestampAsc(Long incidentId);
}
