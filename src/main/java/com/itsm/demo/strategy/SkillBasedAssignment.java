package com.itsm.demo.strategy;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.Technician;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component("skill")
public class SkillBasedAssignment implements AssignmentStrategy {

    @Override
    public Optional<Technician> select(Incident incident, List<Technician> technicians) {
        return technicians.stream()
                .filter(Technician::canAcceptWork)
                .filter(t -> t.hasSkill(incident.getCategory()))
                .max(Comparator.comparingInt(Technician::getExperienceYears));
    }
}
