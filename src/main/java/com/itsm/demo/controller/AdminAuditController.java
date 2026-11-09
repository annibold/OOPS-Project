package com.itsm.demo.controller;

import com.itsm.demo.repository.AuditRecordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminAuditController {

    private final AuditRecordRepository repository;

    public AdminAuditController(AuditRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/admin/audit")
    public String audit(Model model) {
        model.addAttribute("records", repository.findAllByOrderByTimestampDesc());
        return "admin/audit";
    }
}

