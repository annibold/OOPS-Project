package com.itsm.demo.strategy;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.Priority;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedPriorityStrategy implements PriorityStrategy {

    @Override
    public Priority calculate(Incident i) {
        if (i.getImpact() == 3 && i.getBusinessCriticality() == 3) return Priority.P1;
        if (i.getImpact() == 3 || i.getUrgency() == 3)             return Priority.P2;
        if (i.getImpact() == 2 || i.getUrgency() == 2)             return Priority.P3;
        return Priority.P4;
    }
}
