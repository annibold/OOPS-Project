package com.itsm.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp = LocalDateTime.now();
    private String userEmail;
    private String action;
    private Long incidentId;
    private String previousState;
    private String newState;

    @Column(length = 2000)
    private String description;

    protected AuditRecord() { }

    public AuditRecord(String userEmail, String action, Long incidentId,
                       String previousState, String newState, String description) {
        this.userEmail = userEmail;
        this.action = action;
        this.incidentId = incidentId;
        this.previousState = previousState;
        this.newState = newState;
        this.description = description;
    }

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getUserEmail() { return userEmail; }
    public String getAction() { return action; }
    public Long getIncidentId() { return incidentId; }
    public String getPreviousState() { return previousState; }
    public String getNewState() { return newState; }
    public String getDescription() { return description; }
}

