package com.itsm.demo.strategy;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.Priority;

public interface PriorityStrategy {
    Priority calculate(Incident incident);
}