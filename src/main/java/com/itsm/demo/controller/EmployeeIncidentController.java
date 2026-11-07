package com.itsm.demo.controller;

import com.itsm.demo.exception.InvalidIncidentException;
import com.itsm.demo.exception.TechnicianUnavailableException;
import com.itsm.demo.model.*;
import com.itsm.demo.repository.UserRepository;
import com.itsm.demo.service.AssignmentService;
import com.itsm.demo.service.IncidentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employee")
public class EmployeeIncidentController {

    private final IncidentService incidentService;
    private final AssignmentService assignmentService;
    private final UserRepository userRepository;

    public EmployeeIncidentController(IncidentService incidentService,
                                      AssignmentService assignmentService,
                                      UserRepository userRepository) {
        this.incidentService = incidentService;
        this.assignmentService = assignmentService;
        this.userRepository = userRepository;
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow();
    }

    @GetMapping("/report")
    public String reportForm(Model model) {
        model.addAttribute("categories", Category.values());
        return "employee/report";
    }

    @PostMapping("/report")
    public String report(@RequestParam String title,
                         @RequestParam String description,
                         @RequestParam Category category,
                         @RequestParam int impact,
                         @RequestParam int urgency,
                         @RequestParam int criticality,
                         Authentication auth, Model model) {
        try {
            Incident incident = incidentService.createIncident(
                    title, description, currentUser(auth), category,
                    impact, urgency, criticality);
            try {
                assignmentService.assign(incident);
            } catch (TechnicianUnavailableException e) {
                // stays CLASSIFIED; an admin can assign it manually later
            }
            return "redirect:/employee/incidents/" + incident.getId();
        } catch (InvalidIncidentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", Category.values());
            return "employee/report";
        }
    }

    @GetMapping("/incidents")
    public String myIncidents(Authentication auth, Model model) {
        model.addAttribute("incidents", incidentService.findByReporter(currentUser(auth)));
        return "employee/incidents";
    }

    @GetMapping("/incidents/{id}")
    public String details(@PathVariable Long id, Authentication auth, Model model) {
        Incident incident = incidentService.findById(id);
        if (!incident.getReporter().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("You can only view your own incidents");
        }
        model.addAttribute("incident", incident);
        return "employee/incident";
    }
}
