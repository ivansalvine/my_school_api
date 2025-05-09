package com.souldev.security.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ecole")
public class Ecole extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Le nom de l'école est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    @Column(name = "adresse")
    private String adresse;

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    @Column(name = "email", length = 100)
    private String email;

    private String domaineName;

    @OneToMany(mappedBy = "ecole", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Autorise la sérialisation des cycles
    private List<Cycle> cycles = new ArrayList<>();

    @OneToMany(mappedBy = "ecole", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Salle> salles = new ArrayList<>();

    private String logoUrl;

    // Constructeurs
    public Ecole() {}

    public Ecole(String nom, String adresse, String telephone, String email, String domaineName, String logoUrl) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.domaineName = domaineName;
        this.logoUrl = logoUrl;
    }

    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDomaineName() { return domaineName; }
    public void setDomaineName(String domaineName) { this.domaineName = domaineName; }
    public List<Cycle> getCycles() { return cycles; }
    public void setCycles(List<Cycle> cycles) { this.cycles = cycles; }
    public List<Salle> getSalles() { return salles; }
    public void setSalles(List<Salle> salles) { this.salles = salles; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}