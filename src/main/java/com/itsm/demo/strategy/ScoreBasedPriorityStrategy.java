package com.itsm.demo.strategy;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.Priority;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary   // the strategy Spring uses by default
public class ScoreBasedPriorityStrategy implements PriorityStrategy {

    @Override
    public Priority calculate(Incident i) {
        int score = i.getImpact() * i.getUrgency() * i.getBusinessCriticality();
        if (score >= 18) return Priority.P1;
        if (score >= 8)  return Priority.P2;
        if (score >= 3)  return Priority.P3;
        return Priority.P4;
    }
}