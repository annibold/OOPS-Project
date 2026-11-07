package com.itsm.demo.strategy;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.Technician;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component("load")
public class LoadBasedAssignment implements AssignmentStrategy {

    @Override
    public Optional<Technician> select(Incident incident, List<Technician> technicians) {
        return technicians.stream()
                .filter(Technician::canAcceptWork)
                .min(Comparator.comparingInt(Technician::getActiveIncidents));
    }
}
