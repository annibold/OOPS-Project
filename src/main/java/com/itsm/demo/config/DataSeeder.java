package com.itsm.demo.config;

import com.itsm.demo.model.*;
import com.itsm.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;

        users.save(new Administrator("Admin", "admin@itsm.com", encoder.encode("admin123")));
        users.save(new Employee("Asha Employee", "employee@itsm.com", encoder.encode("emp123")));
        users.save(new Technician("Ravi Network", "ravi@itsm.com", encoder.encode("tech123"),
                Set.of(Category.NETWORK, Category.SECURITY), 8));
        users.save(new Technician("Meera Database", "meera@itsm.com", encoder.encode("tech123"),
                Set.of(Category.DATABASE, Category.STORAGE, Category.CLOUD), 5));
        users.save(new Technician("Karan Software", "karan@itsm.com", encoder.encode("tech123"),
                Set.of(Category.SOFTWARE, Category.APPLICATION, Category.AUTHENTICATION), 3));

        System.out.println(">>> Seeded users: " + users.count());
    }
}

