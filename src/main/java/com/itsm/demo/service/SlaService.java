package com.itsm.demo.service;

import com.itsm.demo.model.*;
import com.itsm.demo.observer.IncidentEventPublisher;
import com.itsm.demo.repository.AuditRecordRepository;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.repository.SlaEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class SlaService {

    /** Everything we know about one incident's SLA at a moment in time. */
    public record SlaInfo(LocalDateTime responseDeadline,
                          LocalDateTime resolutionDeadline,
                          LocalDateTime acknowledgedAt,
                          boolean responseViolated,
                          boolean resolutionViolated,
                          SlaStatus status) { }

    private static final double WARNING_FRACTION = 0.8;   // warn at 80% of the resolution time

    private final IncidentRepository incidentRepository;
    private final AuditRecordRepository auditRepository;
    private final SlaEventRepository slaEventRepository;
    private final IncidentEventPublisher events;

    public SlaService(IncidentRepository incidentRepository,
                      AuditRecordRepository auditRepository,
                      SlaEventRepository slaEventRepository,
                      IncidentEventPublisher events) {
        this.incidentRepository = incidentRepository;
        this.auditRepository = auditRepository;
        this.slaEventRepository = slaEventRepository;
        this.events = events;
    }

    /** Pure calculation: the SLA that applies is decided by the incident's priority. */
    public SlaInfo evaluate(Incident incident, LocalDateTime now) {
        Priority p = incident.getPriority();
        if (p == null) {
            return null;
        }
        LocalDateTime created = incident.getCreatedAt();
        LocalDateTime responseDeadline = created.plusMinutes(p.getResponseMinutes());
        LocalDateTime resolutionDeadline = created.plusMinutes(p.getResolutionMinutes());

        LocalDateTime acknowledgedAt = auditRepository
                .findByIncidentIdOrderByTimestampAsc(incident.getId()).stream()
                .filter(a -> "INCIDENT_ACKNOWLEDGED".equals(a.getAction()))
                .map(AuditRecord::getTimestamp)
                .findFirst().orElse(null);

        boolean responseViolated = acknowledgedAt != null
                ? acknowledgedAt.isAfter(responseDeadline)
                : now.isAfter(responseDeadline);

        LocalDateTime resolvedAt = incident.getResolvedAt();
        boolean resolutionViolated = resolvedAt != null
                ? resolvedAt.isAfter(resolutionDeadline)
                : now.isAfter(resolutionDeadline);

        SlaStatus status;
        if (responseViolated || resolutionViolated) {
            status = SlaStatus.VIOLATED;
        } else if (resolvedAt != null) {
            status = SlaStatus.MET;
        } else {
            long totalSeconds = Duration.between(created, resolutionDeadline).getSeconds();
            LocalDateTime warnAt = created.plusSeconds((long) (totalSeconds * WARNING_FRACTION));
            status = now.isAfter(warnAt) ? SlaStatus.AT_RISK : SlaStatus.ON_TRACK;
        }
        return new SlaInfo(responseDeadline, resolutionDeadline, acknowledgedAt,
                responseViolated, resolutionViolated, status);
    }

    /** Background monitoring: runs every minute and records new warnings and violations once. */
    @Scheduled(fixedRate = 60_000, initialDelay = 30_000)
    @Transactional
    public void checkAll() {
        LocalDateTime now = LocalDateTime.now();
        for (Incident incident : incidentRepository.findAll()) {
            if (incident.getStatus() == IncidentStatus.CLOSED) continue;
            SlaInfo info = evaluate(incident, now);
            if (info == null) continue;

            if (info.responseViolated()) {
                record(incident, "RESPONSE_VIOLATION",
                        "Response SLA violated (target " + incident.getPriority().getResponseMinutes() + " min)");
            }
            if (info.resolutionViolated()) {
                record(incident, "RESOLUTION_VIOLATION",
                        "Resolution SLA violated (target " + incident.getPriority().getResolutionMinutes() + " min)");
            }
            if (info.status() == SlaStatus.AT_RISK) {
                record(incident, "WARNING", "SLA deadline is approaching");
            }
        }
    }

    private void record(Incident incident, String type, String description) {
        if (slaEventRepository.existsByIncidentIdAndType(incident.getId(), type)) {
            return;   // already recorded
        }
        slaEventRepository.save(new SlaEvent(incident.getId(), type, description));
        String action = type.equals("WARNING") ? "SLA_WARNING" : "SLA_VIOLATED";
        String state = incident.getStatus().name();
        events.publish(incident, "SYSTEM", action, state, state, description);
    }
}
