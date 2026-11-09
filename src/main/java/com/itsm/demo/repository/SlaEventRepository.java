package com.itsm.demo.repository;

import com.itsm.demo.model.SlaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SlaEventRepository extends JpaRepository<SlaEvent, Long> {
    boolean existsByIncidentIdAndType(Long incidentId, String type);
    List<SlaEvent> findAllByOrderByDetectedAtDesc();
}
