package com.souldev.security.controllers;

import com.souldev.security.payload.request.LeconRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.LeconResponseDTO;
import com.souldev.security.services.LeconService;
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
@RequestMapping("/lecons")
@Tag(name = "Leçon", description = "API pour la gestion des leçons associées à un enseignement")
@Validated
public class LeconController {
    private static final Logger logger = LoggerFactory.getLogger(LeconController.class);

    private final LeconService leconService;

    @Autowired
    public LeconController(LeconService leconService) {
        this.leconService = leconService;
    }

    @Operation(summary = "Créer une nouvelle leçon", description = "Crée une leçon pour un enseignement spécifique. Le champ 'ordre' indique la position séquentielle de la leçon (optionnel).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Leçon créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Enseignement non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> createLecon(@Valid @RequestBody LeconRequestDTO leconDTO) {
        try {
            LeconResponseDTO createdLecon = leconService.createLecon(leconDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Leçon créée avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la création de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la création de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la création de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de la leçon", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer une leçon par ID", description = "Retourne les détails d’une leçon spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leçon trouvée"),
            @ApiResponse(responseCode = "404", description = "Leçon non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getLeconById(@PathVariable String id) {
        try {
            LeconResponseDTO lecon = leconService.getLecon(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Leçon récupérée avec succès !", lecon, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la récupération de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de la leçon", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer toutes les leçons", description = "Retourne une liste de toutes les leçons.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des leçons récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllLecons() {
        try {
            List<LeconResponseDTO> lecons = leconService.getAllLecons();
            return ResponseEntity.ok(new CustomApiResponse(true, "Leçons récupérées avec succès !", lecons, HttpStatus.OK.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des leçons : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des leçons", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer les leçons d’un enseignement", description = "Retourne une liste des leçons associées à un enseignement spécifique, triées par ordre croissant du champ 'ordre'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des leçons récupérée"),
            @ApiResponse(responseCode = "404", description = "Enseignement non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/enseignement/{enseignementId}")
    public ResponseEntity<CustomApiResponse> getLeconsByEnseignement(@PathVariable String enseignementId) {
        try {
            List<LeconResponseDTO> lecons = leconService.getLeconsByEnseignement(enseignementId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Leçons de l’enseignement récupérées avec succès !", lecons, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la récupération des leçons de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des leçons de l’enseignement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des leçons de l’enseignement", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour une leçon", description = "Met à jour les informations d’une leçon existante. Le champ 'ordre' peut être modifié pour réorganiser la séquence des leçons.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leçon mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Leçon ou enseignement non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> updateLecon(
            @PathVariable String id,
            @Valid @RequestBody LeconRequestDTO leconDTO) {
        try {
            LeconResponseDTO updatedLecon = leconService.updateLecon(id, leconDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Leçon mise à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la mise à jour de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la mise à jour de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de la leçon", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer une leçon", description = "Supprime une leçon existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leçon supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Leçon non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteLecon(@PathVariable String id) {
        try {
            leconService.deleteLecon(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Leçon supprimée avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la suppression de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la suppression de la leçon : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de la leçon", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}