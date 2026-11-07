package com.itsm.demo.model;

import java.util.EnumSet;
import java.util.Set;

public enum IncidentStatus {
    NEW, CLASSIFIED, ASSIGNED, ACKNOWLEDGED, IN_PROGRESS, RESOLVED, VERIFIED, CLOSED;

    public Set<IncidentStatus> allowedNext() {
        return switch (this) {
            case NEW -> EnumSet.of(CLASSIFIED);
            case CLASSIFIED -> EnumSet.of(ASSIGNED);
            case ASSIGNED -> EnumSet.of(ACKNOWLEDGED);
            case ACKNOWLEDGED -> EnumSet.of(IN_PROGRESS);
            case IN_PROGRESS -> EnumSet.of(RESOLVED);
            case RESOLVED -> EnumSet.of(VERIFIED, IN_PROGRESS); // reopen if fix fails
            case VERIFIED -> EnumSet.of(CLOSED);
            case CLOSED -> EnumSet.noneOf(IncidentStatus.class);
        };
    }

    public boolean canMoveTo(IncidentStatus next) {
        return allowedNext().contains(next);
    }
}
