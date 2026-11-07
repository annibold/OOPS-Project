package com.itsm.demo.controller;

import com.itsm.demo.model.Employee;
import com.itsm.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String registerForm() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        if (name.isBlank() || email.isBlank() || password.length() < 6) {
            model.addAttribute("error", "All fields are required; password needs 6+ characters.");
            return "register";
        }
        if (users.findByEmail(email.trim().toLowerCase()).isPresent()) {
            model.addAttribute("error", "That email is already registered.");
            return "register";
        }
        users.save(new Employee(name.trim(), email.trim().toLowerCase(), encoder.encode(password)));
        return "redirect:/login?registered";
    }

    /** After login, send each role to its own page. */
    @GetMapping("/home")
    public String home(Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
            return "redirect:/admin/home";
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TECHNICIAN")))
            return "redirect:/technician/home";
        return "redirect:/employee/home";
    }
}

