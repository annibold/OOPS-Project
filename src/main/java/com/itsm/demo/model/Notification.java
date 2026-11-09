package com.itsm.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientEmail;
    private String channel;
    private Long incidentId;

    @Column(length = 2000)
    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();

    protected Notification() { }

    public Notification(String recipientEmail, String channel, Long incidentId, String message) {
        this.recipientEmail = recipientEmail;
        this.channel = channel;
        this.incidentId = incidentId;
        this.message = message;
    }

    public Long getId() { return id; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getChannel() { return channel; }
    public Long getIncidentId() { return incidentId; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
