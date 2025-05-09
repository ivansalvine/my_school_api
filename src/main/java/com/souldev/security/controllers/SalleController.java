package com.souldev.security.controllers;

import com.souldev.security.payload.request.SalleRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.SalleResponseDTO;
import com.souldev.security.services.SalleService;
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

@RestController
@RequestMapping("/salle")
@Validated
public class SalleController {

    private static final Logger logger = LoggerFactory.getLogger(SalleController.class);
    private final SalleService salleService;

    @Autowired
    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @Operation(summary = "Créer une salle", description = "Crée une nouvelle salle associée à une école")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createSalle(@ModelAttribute SalleRequestDTO request) {
        logger.info("Requête pour créer une salle : {}", request.getNom());
        try {
            SalleResponseDTO salle = salleService.createSalle(request);
            return ResponseEntity.ok(new CustomApiResponse(true, "Salle créée avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.warn("Entité non trouvée : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer toutes les salles", description = "Récupère la liste de toutes les salles")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN', 'ROLE_PROFESSOR', 'ROLE_STUDENT')")
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllSalles() {
        logger.info("Requête pour récupérer toutes les salles");
        try {
            List<SalleResponseDTO> salles = salleService.getAllSalles();
            return ResponseEntity.ok(new CustomApiResponse(true, "Liste des salles récupérée", salles, 200));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer une salle par ID", description = "Récupère les détails d'une salle spécifique")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN', 'ROLE_PROFESSOR', 'ROLE_STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getSalleById(@PathVariable String id) {
        logger.info("Requête pour récupérer la salle avec l'ID : {}", id);
        try {
            SalleResponseDTO salle = salleService.getSalleById(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Salle récupérée avec succès", salle, 200));
        } catch (EntityNotFoundException e) {
            logger.warn("Salle non trouvée : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Mettre à jour une salle", description = "Met à jour les informations d'une salle existante")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateSalle(@PathVariable String id, @ModelAttribute SalleRequestDTO request) {
        logger.info("Requête pour mettre à jour la salle avec l'ID : {}", id);
        try {
            SalleResponseDTO salle = salleService.updateSalle(id, request);
            return ResponseEntity.ok(new CustomApiResponse(true, "Salle mise à jour avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.warn("Salle ou entité liée non trouvée : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Supprimer une salle", description = "Supprime une salle par son ID")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteSalle(@PathVariable String id) {
        logger.info("Requête pour supprimer la salle avec l'ID : {}", id);
        try {
            salleService.deleteSalle(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Salle supprimée avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.warn("Salle non trouvée : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }
}
