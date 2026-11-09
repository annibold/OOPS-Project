package com.itsm.demo.controller;

import com.itsm.demo.model.*;
import com.itsm.demo.repository.SlaEventRepository;
import com.itsm.demo.service.IncidentSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@Controller
public class AdminIncidentController {

    private final IncidentSearchService searchService;
    private final SlaEventRepository slaEventRepository;

    public AdminIncidentController(IncidentSearchService searchService,
                                   SlaEventRepository slaEventRepository) {
        this.searchService = searchService;
        this.slaEventRepository = slaEventRepository;
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

    @GetMapping("/admin/sla")
    public String slaEvents(Model model) {
        model.addAttribute("events", slaEventRepository.findAllByOrderByDetectedAtDesc());
        return "admin/sla";
    }
}
