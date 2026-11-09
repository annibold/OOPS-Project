package com.itsm.demo.controller;

import com.itsm.demo.exception.InvalidIncidentException;
import com.itsm.demo.exception.InvalidStateTransitionException;
import com.itsm.demo.model.*;
import com.itsm.demo.repository.AuditRecordRepository;
import com.itsm.demo.repository.IncidentNoteRepository;
import com.itsm.demo.repository.SlaEventRepository;
import com.itsm.demo.service.DuplicateDetectionService;
import com.itsm.demo.service.IncidentSearchService;
import com.itsm.demo.service.IncidentService;
import com.itsm.demo.service.SlaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class AdminIncidentController {

    private final IncidentSearchService searchService;
    private final SlaEventRepository slaEventRepository;
    private final IncidentService incidentService;
    private final IncidentNoteRepository noteRepository;
    private final AuditRecordRepository auditRepository;
    private final SlaService slaService;
    private final DuplicateDetectionService duplicateDetectionService;

    public AdminIncidentController(IncidentSearchService searchService,
                                   SlaEventRepository slaEventRepository,
                                   IncidentService incidentService,
                                   IncidentNoteRepository noteRepository,
                                   AuditRecordRepository auditRepository,
                                   SlaService slaService,
                                   DuplicateDetectionService duplicateDetectionService) {
        this.searchService = searchService;
        this.slaEventRepository = slaEventRepository;
        this.incidentService = incidentService;
        this.noteRepository = noteRepository;
        this.auditRepository = auditRepository;
        this.slaService = slaService;
        this.duplicateDetectionService = duplicateDetectionService;
    }

    @GetMapping("/admin/incidents")
    public String incidents(@RequestParam(required = false) Long id,
                            @RequestParam(required = false) IncidentStatus status,
                            @RequestParam(required = false) Priority priority,
                            @RequestParam(required = false) Category category,
                            @RequestParam(required = false) String technician,
                            @RequestParam(required = false) SlaStatus sla,
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                            Model model) {
        IncidentSearchService.Criteria c = new IncidentSearchService.Criteria(
                id, status, priority, category, technician, sla, from, to);
        model.addAttribute("c", c);
        model.addAttribute("rows", searchService.search(c));
        model.addAttribute("statuses", IncidentStatus.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("categories", Category.values());
        model.addAttribute("slaStatuses", SlaStatus.values());
        return "admin/incidents";
    }

    @GetMapping("/admin/incidents/{id}")
    public String details(@PathVariable Long id, Model model) {
        Incident incident = incidentService.findById(id);
        model.addAttribute("incident", incident);
        model.addAttribute("notes", noteRepository.findByIncidentOrderByCreatedAtAsc(incident));
        model.addAttribute("audits", auditRepository.findByIncidentIdOrderByTimestampAsc(id));
        model.addAttribute("sla", slaService.evaluate(incident, LocalDateTime.now()));
        model.addAttribute("duplicates", duplicateDetectionService.findDuplicates(incident));
        return "admin/incident";
    }

    @PostMapping("/admin/incidents/{id}/close")
    public String close(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            incidentService.close(id, auth.getName());
            ra.addFlashAttribute("message", "Incident #" + id + " has been formally CLOSED.");
        } catch (InvalidStateTransitionException | InvalidIncidentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/incidents/" + id;
    }

    @GetMapping("/admin/sla")
    public String slaEvents(Model model) {
        model.addAttribute("events", slaEventRepository.findAllByOrderByDetectedAtDesc());
        return "admin/sla";
    }
}
