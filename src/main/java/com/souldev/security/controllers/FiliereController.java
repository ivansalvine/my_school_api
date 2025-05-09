package com.souldev.security.controllers;

import com.souldev.security.entities.Classe;
import com.souldev.security.entities.Matiere;
import com.souldev.security.payload.request.FiliereDTO;
import com.souldev.security.payload.request.FiliereResponseDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.FiliereService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/filiere")
@Validated
public class FiliereController {

    private static final Logger logger = LoggerFactory.getLogger(FiliereController.class);
    private final FiliereService filiereService;

    @Autowired
    public FiliereController(FiliereService filiereService) {
        this.filiereService = filiereService;
    }

    @Operation(summary = "Créer une nouvelle filière", description = "Crée une filière avec les données fournies via multipart/form-data")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createFiliere(
            @ModelAttribute FiliereDTO filiereDTO
    ) {
        logger.info("Requête pour créer une filière : {}", filiereDTO.getNom());
        try {
            boolean isCreated = filiereService.createFiliere(filiereDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Filière créée avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation : {}", e.getMessage());
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 400));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Mettre à jour une filière", description = "Met à jour une filière existante avec les données fournies via multipart/form-data")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateFiliere(
            @PathVariable String id,
            @ModelAttribute FiliereDTO filiereDTO) {
        logger.info("Requête pour mettre à jour la filière ID : {}", id);
        try {
            boolean isUpadated = filiereService.updateFiliere(id, filiereDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Filière mise à jour avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation : {}", e.getMessage());
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 400));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer une filière par ID", description = "Récupère les détails d'une filière spécifique")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getFiliereById(@PathVariable String id) {
        logger.info("Requête pour récupérer la filière ID : {}", id);
        try {
            Optional<FiliereDTO> filiere = filiereService.findById(id);
            return filiere.map(filiereDTO -> ResponseEntity.ok(new CustomApiResponse(true, "Filière récupérée avec succès", filiereDTO, 200))).orElseGet(() -> ResponseEntity.status(404).body(new CustomApiResponse(false, "Filière non trouvée", null, 404)));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer toutes les filières", description = "Récupère la liste de toutes les filières")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<CustomApiResponse> getAllFilieres() {
        logger.info("Requête pour récupérer toutes les filières");
        try {
            List<FiliereResponseDTO> filieres = filiereService.findAll();
            return ResponseEntity.ok(new CustomApiResponse(true, "Liste des filières récupérée", filieres, 200));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Supprimer une filière", description = "Supprime une filière si elle n'a pas de classes associées")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteFiliere(@PathVariable String id) {
        logger.info("Requête pour supprimer la filière ID : {}", id);
        try {
            filiereService.deleteFiliere(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Filière supprimée avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.error("Filière non trouvée : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (IllegalStateException e) {
            logger.error("Contrainte de suppression : {}", e.getMessage());
            return ResponseEntity.status(409).body(new CustomApiResponse(false, e.getMessage(), null, 409));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Ajouter une matière à une filière", description = "Ajoute une matière existante à une filière")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/{filiereId}/matiere/{matiereId}")
    public ResponseEntity<CustomApiResponse> addMatiereToFiliere(
            @PathVariable String filiereId,
            @PathVariable String matiereId) {
        logger.info("Requête pour ajouter la matière ID {} à la filière ID {}", matiereId, filiereId);
        try {
            FiliereDTO updatedFiliere = filiereService.addMatiereToFiliere(filiereId, matiereId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Matière ajoutée avec succès", updatedFiliere, 200));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Supprimer une matière d'une filière", description = "Retire une matière d'une filière")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{filiereId}/matiere/{matiereId}")
    public ResponseEntity<CustomApiResponse> removeMatiereFromFiliere(
            @PathVariable String filiereId,
            @PathVariable String matiereId) {
        logger.info("Requête pour supprimer la matière ID {} de la filière ID {}", matiereId, filiereId);
        try {
            FiliereDTO updatedFiliere = filiereService.removeMatiereFromFiliere(filiereId, matiereId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Matière supprimée avec succès", updatedFiliere, 200));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Ajouter une classe à une filière", description = "Ajoute une classe existante à une filière")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/{filiereId}/classe/{classeId}")
    public ResponseEntity<CustomApiResponse> addClasseToFiliere(
            @PathVariable String filiereId,
            @PathVariable String classeId) {
        logger.info("Requête pour ajouter la classe ID {} à la filière ID {}", classeId, filiereId);
        try {
            boolean isAdded = filiereService.addClasseToFiliere(filiereId, classeId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Classe ajoutée avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer les matières d'une filière", description = "Récupère toutes les matières associées à une filière")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/{filiereId}/matieres")
    public ResponseEntity<CustomApiResponse> getMatieresByFiliere(@PathVariable String filiereId) {
        logger.info("Requête pour récupérer les matières de la filière ID : {}", filiereId);
        try {
            List<Matiere> matieres = filiereService.getMatieresByFiliere(filiereId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Matières récupérées avec succès", matieres, 200));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer les classes d'une filière", description = "Récupère toutes les classes associées à une filière")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/{filiereId}/classes")
    public ResponseEntity<CustomApiResponse> getClassesByFiliere(@PathVariable String filiereId) {
        logger.info("Requête pour récupérer les classes de la filière ID : {}", filiereId);
        try {
            List<Classe> classes = filiereService.getClassesByFiliere(filiereId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Classes récupérées avec succès", classes, 200));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }
}