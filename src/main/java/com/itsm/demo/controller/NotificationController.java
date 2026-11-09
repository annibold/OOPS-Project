package com.itsm.demo.controller;

import com.itsm.demo.repository.NotificationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {

    private final NotificationRepository repository;

    public NotificationController(NotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/notifications")
    public String mine(Authentication auth, Model model) {
        model.addAttribute("notifications",
                repository.findByRecipientEmailOrderByCreatedAtDesc(auth.getName()));
        return "notifications";
    }
}

