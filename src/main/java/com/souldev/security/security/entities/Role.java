package com.souldev.security.security.entities;

import com.souldev.security.entities.superclasse.Auditable;
import com.souldev.security.security.enums.RoleList;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    // Constructeurs
    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public Role(RoleList roleList) {
        this.name = roleList.toString();
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}