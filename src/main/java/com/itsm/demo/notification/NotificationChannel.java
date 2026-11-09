package com.itsm.demo.notification;

public interface NotificationChannel {
    String name();
    void send(String recipientEmail, String message, Long incidentId);
}
