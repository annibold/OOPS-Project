package com.itsm.demo.controller;

import com.itsm.demo.exception.InvalidIncidentException;
import com.itsm.demo.exception.InvalidStateTransitionException;
import com.itsm.demo.exception.TechnicianUnavailableException;
import com.itsm.demo.model.*;
import com.itsm.demo.repository.IncidentNoteRepository;
import com.itsm.demo.repository.UserRepository;
import com.itsm.demo.service.AssignmentService;
import com.itsm.demo.service.IncidentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
public class EmployeeIncidentController {

    private final IncidentService incidentService;
    private final AssignmentService assignmentService;
    private final UserRepository userRepository;
    private final IncidentNoteRepository noteRepository;

    public EmployeeIncidentController(IncidentService incidentService,
                                      AssignmentService assignmentService,
                                      UserRepository userRepository,
                                      IncidentNoteRepository noteRepository) {
        this.incidentService = incidentService;
        this.assignmentService = assignmentService;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow();
    }

    /** Verifies the incident belongs to this employee */
    private Incident ownedIncident(Long id, Authentication auth) {
        Incident incident = incidentService.findById(id);
        if (!incident.getReporter().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("You can only view your own incidents");
        }
        return incident;
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
                // stays CLASSIFIED; admin can assign manually
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

    /** Incident detail - includes technician notes */
    @GetMapping("/incidents/{id}")
    public String details(@PathVariable Long id, Authentication auth, Model model) {
        Incident incident = ownedIncident(id, auth);
        model.addAttribute("incident", incident);
        model.addAttribute("notes", noteRepository.findByIncidentOrderByCreatedAtAsc(incident));
        return "employee/incident";
    }

    /** Employee confirms the fix works: RESOLVED -> VERIFIED */
    @PostMapping("/incidents/{id}/verify")
    public String verify(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            Incident incident = ownedIncident(id, auth);
            incidentService.verify(incident, auth.getName());
            ra.addFlashAttribute("success", "Incident verified. Thank you!");
        } catch (InvalidStateTransitionException | InvalidIncidentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/incidents/" + id;
    }

    /** Employee says fix did not work: RESOLVED -> IN_PROGRESS (reopen) */
    @PostMapping("/incidents/{id}/reopen")
    public String reopen(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            Incident incident = ownedIncident(id, auth);
            incidentService.reopen(incident, auth.getName());
            ra.addFlashAttribute("error", "Incident reopened - technician will be notified.");
        } catch (InvalidStateTransitionException | InvalidIncidentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/incidents/" + id;
    }
}
