package com.souldev.security.security.dtos;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class NewUser {

    @NotBlank
    private String userName;
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
    private Set<String> roles = new HashSet<>();

    public NewUser() {
    }
    public NewUser(@NotBlank String userName, @Email String email, @NotBlank String password, @NotBlank String fullName,
            Set<String> roles) {

        this.userName = userName;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.fullName = fullName;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Set<String> getRoles() {
        return roles;
    }
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
