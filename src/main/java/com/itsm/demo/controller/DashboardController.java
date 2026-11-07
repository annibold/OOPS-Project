package com.itsm.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/employee/home")
    public String employee(Authentication auth, Model model) {
        model.addAttribute("email", auth.getName());
        return "employee/home";
    }

    @GetMapping("/technician/home")
    public String technician(Authentication auth, Model model) {
        model.addAttribute("email", auth.getName());
        return "technician/home";
    }

    @GetMapping("/admin/home")
    public String admin(Authentication auth, Model model) {
        model.addAttribute("email", auth.getName());
        return "admin/home";
    }
}

