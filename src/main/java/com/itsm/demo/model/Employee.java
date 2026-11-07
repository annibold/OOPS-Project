package com.itsm.demo.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User {
    protected Employee() { }
    public Employee(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }
    @Override
    public Role getRole() { return Role.EMPLOYEE; }
}
