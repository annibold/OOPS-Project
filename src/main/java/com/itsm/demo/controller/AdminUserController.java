package com.itsm.demo.controller;

import com.itsm.demo.model.Category;
import com.itsm.demo.model.Technician;
import com.itsm.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AdminUserController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", users.findAll());
        model.addAttribute("categories", Category.values());
        return "admin/users";
    }

    @PostMapping("/technician")
    public String addTechnician(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam(required = false) List<Category> skills,
                                @RequestParam(defaultValue = "0") int experience,
                                RedirectAttributes ra) {
        String mail = email.trim().toLowerCase();
        if (name.isBlank() || mail.isBlank() || password.length() < 6 || experience < 0) {
            ra.addFlashAttribute("error", "Name, email, password (6+ chars) and valid experience are required.");
        } else if (users.findByEmail(mail).isPresent()) {
            ra.addFlashAttribute("error", "That email is already registered.");
        } else {
            HashSet<Category> skillSet = skills == null ? new HashSet<>() : new HashSet<>(skills);
            users.save(new Technician(name.trim(), mail, encoder.encode(password), skillSet, experience));
            ra.addFlashAttribute("message", "Technician " + name.trim() + " created.");
        }
        return "redirect:/admin/users";
    }
}
