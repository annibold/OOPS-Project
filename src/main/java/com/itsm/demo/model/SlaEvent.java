package com.itsm.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** A recorded SLA warning or violation. type = RESPONSE_VIOLATION, RESOLUTION_VIOLATION or WARNING. */
@Entity
public class SlaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long incidentId;
    private String type;

    @Column(length = 1000)
    private String description;

    private LocalDateTime detectedAt = LocalDateTime.now();

    protected SlaEvent() { }

    public SlaEvent(Long incidentId, String type, String description) {
        this.incidentId = incidentId;
        this.type = type;
        this.description = description;
    }

    public Long getId() { return id; }
    public Long getIncidentId() { return incidentId; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
}
