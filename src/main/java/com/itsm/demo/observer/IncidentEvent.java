package com.itsm.demo.observer;

/** Something important that happened to an incident. Listeners (observers) react to it. */
public record IncidentEvent(Long incidentId,
                            String reporterEmail,
                            String technicianEmail,
                            String actor,
                            String action,
                            String previousState,
                            String newState,
                            String description) {
}
