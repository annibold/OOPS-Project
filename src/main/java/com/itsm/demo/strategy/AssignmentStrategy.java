package com.itsm.demo.strategy;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.Technician;
import java.util.List;
import java.util.Optional;

public interface AssignmentStrategy {
    Optional<Technician> select(Incident incident, List<Technician> technicians);
}
