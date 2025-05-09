package com.souldev.security.controllers;

import com.souldev.security.payload.request.SemestreRequestDTO;
import com.souldev.security.payload.request.SemestreResponseDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.SemestreService;
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
@RequestMapping("/semestre")
@Validated
public class SemestreController {

    private static final Logger logger = LoggerFactory.getLogger(SemestreController.class);
    private final SemestreService semestreService;

    @Autowired
    public SemestreController(SemestreService semestreService) {
        this.semestreService = semestreService;
    }

    @Operation(summary = "Créer un semestre", description = "Crée un nouveau semestre associé à une année scolaire")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PostMapping
    public ResponseEntity<CustomApiResponse> createSemestre(@ModelAttribute SemestreRequestDTO requestDTO) {
        logger.info("Requête pour créer un semestre : {}", requestDTO.getNom());
        try {
            SemestreResponseDTO semestre = semestreService.createSemestre(requestDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Semestre créé avec succès", null, 200));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer tous les semestres", description = "Récupère la liste de tous les semestres")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN', 'ROLE_PROFESSOR', 'ROLE_STUDENT')")
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllSemestres() {
        logger.info("Requête pour récupérer tous les semestres");
        try {
            List<SemestreResponseDTO> semestres = semestreService.getAllSemestres();
            return ResponseEntity.ok(new CustomApiResponse(true, "Liste des semestres récupérée", semestres, 200));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer un semestre par ID", description = "Récupère les détails d'un semestre spécifique")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN', 'ROLE_PROFESSOR', 'ROLE_STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getSemestreById(@PathVariable String id) {
        logger.info("Requête pour récupérer le semestre avec l'ID : {}", id);
        try {
            SemestreResponseDTO semestre = semestreService.getSemestreById(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Semestre récupéré avec succès", semestre, 200));
        } catch (EntityNotFoundException e) {
            logger.warn("Semestre non trouvé : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Mettre à jour un semestre", description = "Met à jour les informations d'un semestre existant")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse> updateSemestre(@PathVariable String id, @ModelAttribute SemestreRequestDTO request) {
        logger.info("Requête pour mettre à jour le semestre avec l'ID : {}", id);
        try {
            SemestreResponseDTO semestre = semestreService.updateSemestre(id, request);
            return ResponseEntity.ok(new CustomApiResponse(true, "Semestre mis à jour avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.warn("Semestre ou année scolaire non trouvé : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Supprimer un semestre", description = "Supprime un semestre par son ID")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteSemestre(@PathVariable String id) {
        logger.info("Requête pour supprimer le semestre avec l'ID : {}", id);
        try {
            semestreService.deleteSemestre(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Semestre supprimé avec succès", null, 200));
        } catch (EntityNotFoundException e) {
            logger.warn("Semestre non trouvé : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }
}