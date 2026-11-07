package com.itsm.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class IncidentNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Incident incident;

    private String author;

    @Column(length = 2000)
    private String text;

    private LocalDateTime createdAt = LocalDateTime.now();

    protected IncidentNote() { }

    public IncidentNote(Incident incident, String author, String text) {
        this.incident = incident;
        this.author = author;
        this.text = text;
    }

    public Long getId() { return id; }
    public Incident getIncident() { return incident; }
    public String getAuthor() { return author; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
