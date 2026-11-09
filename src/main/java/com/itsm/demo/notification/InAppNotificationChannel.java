package com.itsm.demo.notification;

import com.itsm.demo.model.Notification;
import com.itsm.demo.repository.NotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class InAppNotificationChannel implements NotificationChannel {

    private final NotificationRepository repository;

    public InAppNotificationChannel(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public String name() { return "IN_APP"; }

    @Override
    public void send(String recipientEmail, String message, Long incidentId) {
        repository.save(new Notification(recipientEmail, name(), incidentId, message));
    }
}

