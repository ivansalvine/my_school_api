package com.souldev.security.controllers;

import com.souldev.security.payload.request.CreneauRequestDTO;
import com.souldev.security.payload.request.EmploiDuTempsRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.EmploiDuTempsResponseDTO;
import com.souldev.security.services.EmploiDuTempsService;
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
import java.util.List;

@RestController
@RequestMapping("/emploi-du-temps")
@Tag(name = "Emploi du Temps", description = "API pour la gestion des emplois du temps")
@Validated
public class EmploiDuTempsController {

    private final EmploiDuTempsService emploiDuTempsService;

    @Autowired
    public EmploiDuTempsController(EmploiDuTempsService emploiDuTempsService) {
        this.emploiDuTempsService = emploiDuTempsService;
    }

    @Operation(summary = "Créer un nouvel emploi du temps", description = "Crée un emploi du temps pour une classe et un semestre spécifiques.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Emploi du temps créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Classe ou semestre non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createEmploiDuTemps(@ModelAttribute EmploiDuTempsRequestDTO emploiDuTempsDTO) {
        try {
            EmploiDuTempsResponseDTO createdEmploiDuTemps = emploiDuTempsService.createEmploiDuTemps(emploiDuTempsDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Emploi du temps créé avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de l’emploi du temps", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un emploi du temps par ID", description = "Retourne les détails d’un emploi du temps spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emploi du temps trouvé"),
            @ApiResponse(responseCode = "404", description = "Emploi du temps non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getEmploiDuTempsById(@PathVariable String id) {
        try {
            EmploiDuTempsResponseDTO emploiDuTemps = emploiDuTempsService.getEmploiDuTemps(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Emploi du temps récupéré avec succès !", emploiDuTemps, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de l’emploi du temps", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les emplois du temps", description = "Retourne une liste de tous les emplois du temps.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des emplois du temps récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllEmploisDuTemps() {
        try {
            List<EmploiDuTempsResponseDTO> emploisDuTemps = emploiDuTempsService.getAllEmploisDuTemps();
            return ResponseEntity.ok(new CustomApiResponse(true, "Emplois du temps récupérés avec succès !", emploisDuTemps, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des emplois du temps", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un emploi du temps", description = "Met à jour les informations d’un emploi du temps existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emploi du temps mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Emploi du temps, classe ou semestre non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateEmploiDuTemps(
            @PathVariable String id,
            @ModelAttribute EmploiDuTempsRequestDTO emploiDuTempsDTO) {
        try {
            EmploiDuTempsResponseDTO updatedEmploiDuTemps = emploiDuTempsService.updateEmploiDuTemps(id, emploiDuTempsDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Emploi du temps mis à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de l’emploi du temps", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Ajouter un créneau à un emploi du temps", description = "Ajoute un créneau horaire à un emploi du temps existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneau ajouté avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Emploi du temps, salle ou cours non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}/creneau", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> addCreneau(
            @PathVariable String id,
            @RequestBody CreneauRequestDTO creneauDTO
    ) {
        try {
            EmploiDuTempsResponseDTO updatedEmploiDuTemps = emploiDuTempsService.addCreneau(id, creneauDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Créneau ajouté avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de l’ajout du créneau", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer un emploi du temps", description = "Supprime un emploi du temps et ses créneaux associés.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emploi du temps supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Emploi du temps non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteEmploiDuTemps(@PathVariable String id) {
        try {
            emploiDuTempsService.deleteEmploiDuTemps(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Emploi du temps supprimé avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de l’emploi du temps", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}