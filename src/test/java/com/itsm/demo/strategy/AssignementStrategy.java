package com.itsm.demo.strategy;

import com.itsm.demo.model.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class AssignmentStrategyTest {

    private Incident networkIncident() {
        Employee e = new Employee("Asha", "asha@test.com", "x");
        Incident i = new Incident("WiFi down", "No network", e, 2, 2, 2);
        i.setCategory(Category.NETWORK);
        return i;
    }

    private Technician tech(String name, Set<Category> skills, int years) {
        return new Technician(name, name + "@test.com", "x", skills, years);
    }

    @Test
    void skillBasedPicksMostExperiencedWithSkill() {
        Technician junior = tech("junior", Set.of(Category.NETWORK), 1);
        Technician senior = tech("senior", Set.of(Category.NETWORK), 8);
        Technician dbOnly = tech("db", Set.of(Category.DATABASE), 10);

        var result = new SkillBasedAssignment()
                .select(networkIncident(), List.of(junior, senior, dbOnly));

        assertEquals(senior, result.orElseThrow());
    }

    @Test
    void loadBasedPicksLeastBusy() {
        Technician busy = tech("busy", Set.of(Category.NETWORK), 5);
        Technician free = tech("free", Set.of(Category.NETWORK), 2);
        busy.incrementLoad();
        busy.incrementLoad();

        var result = new LoadBasedAssignment()
                .select(networkIncident(), List.of(busy, free));

        assertEquals(free, result.orElseThrow());
    }

    @Test
    void noSkilledTechnicianGivesEmpty() {
        Technician dbOnly = tech("db", Set.of(Category.DATABASE), 10);
        assertTrue(new SkillBasedAssignment()
                .select(networkIncident(), List.of(dbOnly)).isEmpty());
    }

    @Test
    void overloadedTechnicianIsSkipped() {
        Technician t = tech("t", Set.of(Category.NETWORK), 5);
        for (int k = 0; k < Technician.MAX_LOAD; k++) t.incrementLoad();
        assertTrue(new LoadBasedAssignment()
                .select(networkIncident(), List.of(t)).isEmpty());
    }
}
