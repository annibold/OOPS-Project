package com.itsm.demo.service;

import com.itsm.demo.model.*;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.repository.SlaEventRepository;
import com.itsm.demo.repository.TechnicianRepository;
import org.springframework.stereotype.Service;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class ReportService {

    public record Overview(long total, long open, long critical, long resolved, long slaViolations,
                           Map<String, Long> byCategory, Map<String, Long> byPriority,
                           List<Technician> technicians) { }

    private static final Set<IncidentStatus> DONE =
            EnumSet.of(IncidentStatus.RESOLVED, IncidentStatus.VERIFIED, IncidentStatus.CLOSED);

    private final IncidentRepository incidentRepository;
    private final TechnicianRepository technicianRepository;
    private final SlaEventRepository slaEventRepository;

    public ReportService(IncidentRepository incidentRepository,
                         TechnicianRepository technicianRepository,
                         SlaEventRepository slaEventRepository) {
        this.incidentRepository = incidentRepository;
        this.technicianRepository = technicianRepository;
        this.slaEventRepository = slaEventRepository;
    }

    public Overview overview() {
        List<Incident> all = incidentRepository.findAll();
        long resolved = all.stream().filter(i -> DONE.contains(i.getStatus())).count();
        long critical = all.stream().filter(i -> i.getPriority() == Priority.P1).count();
        long violations = slaEventRepository.findAll().stream()
                .filter(e -> !"WARNING".equals(e.getType()))
                .map(SlaEvent::getIncidentId).distinct().count();

        Map<String, Long> byCategory = all.stream()
                .filter(i -> i.getCategory() != null)
                .collect(Collectors.groupingBy(i -> i.getCategory().name(), TreeMap::new, Collectors.counting()));
        Map<String, Long> byPriority = all.stream()
                .filter(i -> i.getPriority() != null)
                .collect(Collectors.groupingBy(i -> i.getPriority().name(), TreeMap::new, Collectors.counting()));

        return new Overview(all.size(), all.size() - resolved, critical, resolved, violations,
                byCategory, byPriority, technicianRepository.findAll());
    }
}
