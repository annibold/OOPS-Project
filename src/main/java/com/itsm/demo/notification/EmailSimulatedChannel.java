package com.itsm.demo.notification;

import com.itsm.demo.model.Notification;
import com.itsm.demo.repository.NotificationRepository;
import org.springframework.stereotype.Component;

/** Simulated email: prints to the console and keeps a copy. A real mail sender could replace it. */
@Component
public class EmailSimulatedChannel implements NotificationChannel {

    private final NotificationRepository repository;

    public EmailSimulatedChannel(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public String name() { return "EMAIL"; }

    @Override
    public void send(String recipientEmail, String message, Long incidentId) {
        System.out.println("[EMAIL] To: " + recipientEmail + " | " + message);
        repository.save(new Notification(recipientEmail, name(), incidentId, message));
    }
}

