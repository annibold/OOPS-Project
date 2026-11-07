package com.itsm.demo.repository;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.IncidentNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentNoteRepository extends JpaRepository<IncidentNote, Long> {
    List<IncidentNote> findByIncidentOrderByCreatedAtAsc(Incident incident);
}
