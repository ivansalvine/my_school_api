package com.souldev.security.security.dtos;

import java.util.Date;
import java.util.Set;

public class UserDTO {
    private Date createdAt;
    private Date updatedAt;
    private String updatedBy;
    private String createdBy;
    private String id;
    private String userName;
    private String email;
    private String fullName;
    private String resetCode;
    private Set<String> roles; // Changement ici : Set au lieu de String

    public UserDTO(Date createdAt,
                   Date updatedAt,
                   String updatedBy,
                   String createdBy,
                   String id,
                   String userName,
                   String email,
                   String fullName,
                   String resetCode,
                   Set<String> roles) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.createdBy = createdBy;
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.fullName = fullName;
        this.resetCode = resetCode;
        this.roles = roles;
    }

    // Getters et setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}