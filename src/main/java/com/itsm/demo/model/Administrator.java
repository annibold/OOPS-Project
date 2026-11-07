package com.itsm.demo.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Administrator extends User {
    protected Administrator() { }
    public Administrator(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }
    @Override
    public Role getRole() { return Role.ADMIN; }
}
