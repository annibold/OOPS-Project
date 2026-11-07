package com.itsm.demo.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("TECHNICIAN")
public class Technician extends User {

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Category> skills = new HashSet<>();

    private int experienceYears;
    private int activeIncidents;
    private boolean available = true;

    protected Technician() { }

    public Technician(String name, String email, String passwordHash,
                      Set<Category> skills, int experienceYears) {
        super(name, email, passwordHash);
        this.skills = skills;
        this.experienceYears = experienceYears;
    }

    @Override
    public Role getRole() { return Role.TECHNICIAN; }

    public boolean hasSkill(Category c) { return skills.contains(c); }
    public void incrementLoad() { activeIncidents++; }
    public void decrementLoad() { if (activeIncidents > 0) activeIncidents--; }

    public Set<Category> getSkills() { return skills; }
    public int getExperienceYears() { return experienceYears; }
    public int getActiveIncidents() { return activeIncidents; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public static final int MAX_LOAD = 5;

    public boolean canAcceptWork() {
        return available && activeIncidents < MAX_LOAD;
    }
}
