package com.itsm.demo.config;

import com.itsm.demo.model.*;
import com.itsm.demo.repository.AuditRecordRepository;
import com.itsm.demo.repository.IncidentNoteRepository;
import com.itsm.demo.repository.IncidentRepository;
import com.itsm.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final IncidentRepository incidents;
    private final IncidentNoteRepository notes;
    private final AuditRecordRepository audit;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users,
                      IncidentRepository incidents,
                      IncidentNoteRepository notes,
                      AuditRecordRepository audit,
                      PasswordEncoder encoder) {
        this.users = users;
        this.incidents = incidents;
        this.notes = notes;
        this.audit = audit;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;

        // 1. Seed Users
        Administrator admin = new Administrator("Admin IT Manager", "admin@itsm.com", encoder.encode("admin123"));
        Employee asha = new Employee("Asha Employee", "employee@itsm.com", encoder.encode("emp123"));

        Technician ravi = new Technician("Ravi Network", "ravi@itsm.com", encoder.encode("tech123"),
                Set.of(Category.NETWORK, Category.SECURITY), 8);
        Technician meera = new Technician("Meera Database", "meera@itsm.com", encoder.encode("tech123"),
                Set.of(Category.DATABASE, Category.STORAGE, Category.CLOUD), 5);
        Technician karan = new Technician("Karan Software", "karan@itsm.com", encoder.encode("tech123"),
                Set.of(Category.SOFTWARE, Category.APPLICATION, Category.AUTHENTICATION), 3);

        users.save(admin);
        users.save(asha);
        users.save(ravi);
        users.save(meera);
        users.save(karan);

        // 2. Seed Demo Incident 1: Historical Resolved Payment Problem (Enables Resolution Suggestion)
        Incident inc1 = new Incident("Payment application unavailable",
                "Users encounter timeout when processing credit card transactions at checkout.",
                asha, 3, 3, 3);
        inc1.setCategory(Category.APPLICATION);
        inc1.setPriority(Priority.P1);
        inc1.setAssignedTechnician(karan);
        inc1.changeStatus(IncidentStatus.CLASSIFIED);
        inc1.changeStatus(IncidentStatus.ASSIGNED);
        inc1.changeStatus(IncidentStatus.ACKNOWLEDGED);
        inc1.changeStatus(IncidentStatus.IN_PROGRESS);
        inc1.changeStatus(IncidentStatus.RESOLVED);
        inc1.setResolution("Restarted payment gateway container, purged stale Redis connection pool, and recycled thread pools.");
        incidents.save(inc1);

        notes.save(new IncidentNote(inc1, "karan@itsm.com", "Identified memory leak in connection pool, restarting service."));
        audit.save(new AuditRecord("employee@itsm.com", "INCIDENT_CREATED", inc1.getId(), null, "NEW", "Reported by Asha"));
        audit.save(new AuditRecord("SYSTEM", "INCIDENT_ASSIGNED", inc1.getId(), "CLASSIFIED", "ASSIGNED", "Assigned to Karan"));
        audit.save(new AuditRecord("karan@itsm.com", "INCIDENT_RESOLVED", inc1.getId(), "IN_PROGRESS", "RESOLVED", "Resolution provided"));

        // 3. Seed Demo Incident 2: Active Database Issue (Assigned to Meera)
        Incident inc2 = new Incident("Database replica replication lag",
                "Secondary reporting database is running 45 minutes behind primary node.",
                asha, 2, 2, 3);
        inc2.setCategory(Category.DATABASE);
        inc2.setPriority(Priority.P2);
        inc2.setAssignedTechnician(meera);
        meera.incrementLoad();
        inc2.changeStatus(IncidentStatus.CLASSIFIED);
        inc2.changeStatus(IncidentStatus.ASSIGNED);
        inc2.changeStatus(IncidentStatus.ACKNOWLEDGED);
        inc2.changeStatus(IncidentStatus.IN_PROGRESS);
        incidents.save(inc2);
        users.save(meera);

        notes.save(new IncidentNote(inc2, "meera@itsm.com", "Currently analyzing WAL file transmission over internal network."));
        audit.save(new AuditRecord("employee@itsm.com", "INCIDENT_CREATED", inc2.getId(), null, "NEW", "Reported by Asha"));
        audit.save(new AuditRecord("SYSTEM", "INCIDENT_ASSIGNED", inc2.getId(), "CLASSIFIED", "ASSIGNED", "Assigned to Meera"));
        audit.save(new AuditRecord("meera@itsm.com", "INVESTIGATION_STARTED", inc2.getId(), "ACKNOWLEDGED", "IN_PROGRESS", "Analyzing replication lag"));

        // 4. Seed Demo Incident 3: Core Network Switch Issue (Assigned to Ravi)
        Incident inc3 = new Incident("Core Switch 02 intermittent packet drop",
                "Packet loss of 15% observed on VLAN 20 gateway interface.",
                asha, 3, 2, 2);
        inc3.setCategory(Category.NETWORK);
        inc3.setPriority(Priority.P2);
        inc3.setAssignedTechnician(ravi);
        ravi.incrementLoad();
        inc3.changeStatus(IncidentStatus.CLASSIFIED);
        inc3.changeStatus(IncidentStatus.ASSIGNED);
        incidents.save(inc3);
        users.save(ravi);

        audit.save(new AuditRecord("employee@itsm.com", "INCIDENT_CREATED", inc3.getId(), null, "NEW", "Reported by Asha"));
        audit.save(new AuditRecord("SYSTEM", "INCIDENT_ASSIGNED", inc3.getId(), "CLASSIFIED", "ASSIGNED", "Assigned to Ravi"));

        System.out.println(">>> Seeded users: " + users.count() + ", incidents: " + incidents.count());
    }
}