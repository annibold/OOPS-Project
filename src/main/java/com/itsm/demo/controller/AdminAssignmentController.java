package com.itsm.demo.controller;

import com.itsm.demo.exception.TechnicianUnavailableException;
import com.itsm.demo.model.IncidentStatus;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.service.AssignmentService;
import com.itsm.demo.service.IncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/assignment")
public class AdminAssignmentController {

    private final AssignmentService assignmentService;
    private final IncidentService incidentService;
    private final IncidentRepository incidentRepository;

    public AdminAssignmentController(AssignmentService assignmentService,
                                     IncidentService incidentService,
                                     IncidentRepository incidentRepository) {
        this.assignmentService = assignmentService;
        this.incidentService = incidentService;
        this.incidentRepository = incidentRepository;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("strategy", assignmentService.getActiveStrategy());
        model.addAttribute("unassigned", incidentRepository.findByStatus(IncidentStatus.CLASSIFIED));
        return "admin/assignment";
    }

    @PostMapping("/strategy")
    public String changeStrategy(@RequestParam String name, RedirectAttributes ra) {
        try {
            assignmentService.setActiveStrategy(name);
            ra.addFlashAttribute("message", "Assignment strategy is now: " + name);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/assignment";
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable Long id, RedirectAttributes ra) {
        try {
            assignmentService.assign(incidentService.findById(id));
            ra.addFlashAttribute("message", "Incident " + id + " assigned.");
        } catch (TechnicianUnavailableException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/assignment";
    }
}
