package com.itsm.demo.model;

import com.itsm.demo.exception.InvalidStateTransitionException;
import com.itsm.demo.strategy.ScoreBasedPriorityStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IncidentTest {

    private Incident newIncident(int impact, int urgency, int crit) {
        Employee e = new Employee("Asha", "asha@test.com", "x");
        return new Incident("Payment app down", "Users cannot pay", e, impact, urgency, crit);
    }

    @Test
    void newCannotJumpToClosed() {
        Incident i = newIncident(3, 3, 3);
        assertThrows(InvalidStateTransitionException.class,
                () -> i.changeStatus(IncidentStatus.CLOSED));
    }

    @Test
    void validTransitionWorks() {
        Incident i = newIncident(3, 3, 3);
        i.changeStatus(IncidentStatus.CLASSIFIED);
        assertEquals(IncidentStatus.CLASSIFIED, i.getStatus());
    }

    @Test
    void maxScoreIsP1() {
        assertEquals(Priority.P1,
                new ScoreBasedPriorityStrategy().calculate(newIncident(3, 3, 3)));
    }

    @Test
    void minScoreIsP4() {
        assertEquals(Priority.P4,
                new ScoreBasedPriorityStrategy().calculate(newIncident(1, 1, 1)));
    }
}
