package com.itsm.demo.model;

import com.itsm.demo.exception.InvalidStateTransitionException;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(optional = false)
    private User reporter;

    @ManyToOne
    private Technician assignedTechnician;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status = IncidentStatus.NEW;

    private int impact;               // 1-3
    private int urgency;              // 1-3
    private int businessCriticality;  // 1-3

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;

    @Column(length = 2000)
    private String resolution;

    @Column(length = 2000)
    private String feedback;

    private Integer rating;  // 1-5 stars

    protected Incident() { }

    public Incident(String title, String description, User reporter,
                    int impact, int urgency, int businessCriticality) {
        this.title = title;
        this.description = description;
        this.reporter = reporter;
        this.impact = impact;
        this.urgency = urgency;
        this.businessCriticality = businessCriticality;
    }

    /** The only way to change status: invalid jumps are rejected. */
    public void changeStatus(IncidentStatus next) {
        if (!status.canMoveTo(next)) {
           throw new InvalidStateTransitionException(
                    "Cannot move incident from " + status + " to " + next);
        }
        this.status = next;
        if (next == IncidentStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public User getReporter() { return reporter; }
    public Technician getAssignedTechnician() { return assignedTechnician; }
    public void setAssignedTechnician(Technician t) { this.assignedTechnician = t; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public IncidentStatus getStatus() { return status; }
    public int getImpact() { return impact; }
    public int getUrgency() { return urgency; }
    public int getBusinessCriticality() { return businessCriticality; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
