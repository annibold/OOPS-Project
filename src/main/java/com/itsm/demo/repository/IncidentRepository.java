package com.itsm.demo.repository;

import com.itsm.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findByPriority(Priority priority);
    List<Incident> findByCategory(Category category);
    List<Incident> findByAssignedTechnician(Technician technician);
    List<Incident> findByReporter(User reporter);
}
