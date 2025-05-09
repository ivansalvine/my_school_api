package com.souldev.security.controllers;

import com.souldev.security.payload.request.CreneauRequestDTO;
import com.souldev.security.payload.response.CreneauResponseDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.CreneauService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/creneaux")
@Tag(name = "Créneaux", description = "API pour la gestion des créneaux horaires")
@Validated
public class CreneauController {

    private final CreneauService creneauService;

    @Autowired
    public CreneauController(CreneauService creneauService) {
        this.creneauService = creneauService;
    }

    @Operation(summary = "Créer un nouveau créneau", description = "Crée un créneau horaire pour un emploi du temps, une salle et un cours spécifiques.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Créneau créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Emploi du temps, salle ou cours non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> createCreneau(@Valid @RequestBody CreneauRequestDTO creneauDTO) {
        try {
            CreneauResponseDTO createdCreneau = creneauService.createCreneau(creneauDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Créneau créé avec succès !", createdCreneau, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création du créneau", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les créneaux", description = "Retourne une liste de tous les créneaux horaires.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des créneaux récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllCreneaux() {
        try {
            List<CreneauResponseDTO> creneaux = creneauService.getAllCreneaux();
            return ResponseEntity.ok(new CustomApiResponse(true, "Créneaux récupérés avec succès !", creneaux, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des créneaux", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un créneau par ID", description = "Retourne les détails d’un créneau spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneau trouvé"),
            @ApiResponse(responseCode = "404", description = "Créneau non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getCreneauById(@PathVariable String id) {
        try {
            CreneauResponseDTO creneau = creneauService.getCreneauById(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Créneau récupéré avec succès !", creneau, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération du créneau", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un créneau", description = "Met à jour les informations d’un créneau existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneau mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Créneau, emploi du temps, salle ou cours non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> updateCreneau(
            @PathVariable String id,
            @Valid @RequestBody CreneauRequestDTO creneauDTO) {
        try {
            CreneauResponseDTO updatedCreneau = creneauService.updateCreneau(id, creneauDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Créneau mis à jour avec succès !", updatedCreneau, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour du créneau", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer un créneau", description = "Supprime un créneau spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneau supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Créneau non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteCreneau(@PathVariable String id) {
        try {
            creneauService.deleteCreneau(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Créneau supprimé avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression du créneau", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}