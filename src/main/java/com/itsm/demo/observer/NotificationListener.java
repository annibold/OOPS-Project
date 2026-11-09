package com.itsm.demo.observer;

import com.itsm.demo.notification.NotificationChannel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Observer 2: tells the people involved, through every available channel. */
@Component
public class NotificationListener {

    private final List<NotificationChannel> channels;   // Spring injects ALL channel beans

    public NotificationListener(List<NotificationChannel> channels) {
        this.channels = channels;
    }

    @EventListener
    public void on(IncidentEvent e) {
        Set<String> recipients = new LinkedHashSet<>();
        recipients.add(e.reporterEmail());
        if (e.technicianEmail() != null) {
            recipients.add(e.technicianEmail());
        }
        recipients.remove(e.actor());   // do not notify the person who did the action

        String message = "[Incident #" + e.incidentId() + "] " + e.description();
        for (String recipient : recipients) {
            for (NotificationChannel channel : channels) {
                channel.send(recipient, message, e.incidentId());
            }
        }
    }
}
