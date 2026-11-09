package com.itsm.demo.controller;

import com.itsm.demo.exception.InvalidIncidentException;
import com.itsm.demo.exception.InvalidStateTransitionException;
import com.itsm.demo.model.Incident;
import com.itsm.demo.service.DuplicateDetectionService;
import com.itsm.demo.service.ResolutionSuggestionService;
import com.itsm.demo.service.TechnicianIncidentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/technician")
public class TechnicianIncidentController {

    private final TechnicianIncidentService service;
    private final DuplicateDetectionService duplicateDetectionService;
    private final ResolutionSuggestionService resolutionSuggestionService;

    public TechnicianIncidentController(TechnicianIncidentService service,
                                        DuplicateDetectionService duplicateDetectionService,
                                        ResolutionSuggestionService resolutionSuggestionService) {
        this.service = service;
        this.duplicateDetectionService = duplicateDetectionService;
        this.resolutionSuggestionService = resolutionSuggestionService;
    }

    @GetMapping("/incidents")
    public String list(Authentication auth, Model model) {
        model.addAttribute("incidents", service.findAssigned(auth.getName()));
        model.addAttribute("workload", service.workload(auth.getName()));
        return "technician/incidents";
    }

    @GetMapping("/incidents/{id}")
    public String details(@PathVariable Long id, Authentication auth, Model model) {
        Incident incident = service.getIncident(id, auth.getName());
        model.addAttribute("incident", incident);
        model.addAttribute("notes", service.getNotes(incident));
        model.addAttribute("duplicates", duplicateDetectionService.findDuplicates(incident));
        model.addAttribute("suggestions", resolutionSuggestionService.suggestResolutions(incident));
        return "technician/incident";
    }

    @PostMapping("/incidents/{id}/acknowledge")
    public String acknowledge(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        return run(id, ra, () -> service.acknowledge(id, auth.getName()));
    }

    @PostMapping("/incidents/{id}/start")
    public String start(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        return run(id, ra, () -> service.startWork(id, auth.getName()));
    }

    @PostMapping("/incidents/{id}/note")
    public String note(@PathVariable Long id, @RequestParam String text,
                       Authentication auth, RedirectAttributes ra) {
        return run(id, ra, () -> service.addNote(id, auth.getName(), text));
    }

    @PostMapping("/incidents/{id}/resolve")
    public String resolve(@PathVariable Long id, @RequestParam String resolution,
                          Authentication auth, RedirectAttributes ra) {
        return run(id, ra, () -> service.resolve(id, auth.getName(), resolution));
    }

    /** Runs an action; business-rule errors are shown on the page instead of crashing. */
    private String run(Long id, RedirectAttributes ra, Runnable action) {
        try {
            action.run();
        } catch (InvalidStateTransitionException | InvalidIncidentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/technician/incidents/" + id;
    }
}
