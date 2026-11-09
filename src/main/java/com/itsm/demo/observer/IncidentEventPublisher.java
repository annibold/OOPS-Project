package com.itsm.demo.observer;

import com.itsm.demo.model.Incident;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class IncidentEventPublisher {

    private final ApplicationEventPublisher publisher;

    public IncidentEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(Incident incident, String actor, String action,
                        String previousState, String newState, String description) {
        String techEmail = incident.getAssignedTechnician() == null
                ? null : incident.getAssignedTechnician().getEmail();
        publisher.publishEvent(new IncidentEvent(
                incident.getId(),
                incident.getReporter().getEmail(),
                techEmail,
                actor, action, previousState, newState, description));
    }
}
