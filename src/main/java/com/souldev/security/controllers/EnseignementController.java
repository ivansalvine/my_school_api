package com.souldev.security.controllers;

import com.souldev.security.payload.request.EnseignementRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.EnseignementResponseDTO;
import com.souldev.security.services.EnseignementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/enseignements")
@Tag(name = "Enseignement", description = "API pour la gestion des enseignements")
@Validated
public class EnseignementController {
    private static final Logger logger = LoggerFactory.getLogger(EnseignementController.class);

    private final EnseignementService enseignementService;

    @Autowired
    public EnseignementController(EnseignementService enseignementService) {
        this.enseignementService = enseignementService;
    }

    @Operation(summary = "Créer un nouvel enseignement", description = "Crée un enseignement associant un cours, une classe et un professeur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Enseignement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Cours, classe ou professeur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> createEnseignement(@Valid @RequestBody EnseignementRequestDTO enseignementDTO) {
        try {
            EnseignementResponseDTO createdEnseignement = enseignementService.createEnseignement(enseignementDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Enseignement créé avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la création de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la création de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la création de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de l’enseignement", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un enseignement par ID", description = "Retourne les détails d’un enseignement spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignement trouvé"),
            @ApiResponse(responseCode = "404", description = "Enseignement non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getEnseignementById(@PathVariable String id) {
        try {
            EnseignementResponseDTO enseignement = enseignementService.getEnseignement(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Enseignement récupéré avec succès !", enseignement, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la récupération de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de l’enseignement", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les enseignements", description = "Retourne une liste de tous les enseignements.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des enseignements récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllEnseignements() {
        try {
            List<EnseignementResponseDTO> enseignements = enseignementService.getAllEnseignements();
            return ResponseEntity.ok(new CustomApiResponse(true, "Enseignements récupérés avec succès !", enseignements, HttpStatus.OK.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des enseignements : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des enseignements", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un enseignement", description = "Met à jour les informations d’un enseignement existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignement mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Enseignement, cours, classe ou professeur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> updateEnseignement(
            @PathVariable String id,
            @Valid @RequestBody EnseignementRequestDTO enseignementDTO) {
        try {
            EnseignementResponseDTO updatedEnseignement = enseignementService.updateEnseignement(id, enseignementDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Enseignement mis à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la mise à jour de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la mise à jour de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de l’enseignement", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer un enseignement", description = "Supprime un enseignement existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignement supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignement non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteEnseignement(@PathVariable String id) {
        try {
            enseignementService.deleteEnseignement(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Enseignement supprimé avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la suppression de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la suppression de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de l’enseignement", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}