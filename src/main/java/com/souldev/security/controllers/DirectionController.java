package com.souldev.security.controllers;


import com.souldev.security.payload.request.DirectionRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.DirectionResponseDTO;
import com.souldev.security.services.DirectionService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/direction")
@Tag(name = "Direction", description = "API pour la gestion des agents de direction")
@Validated
public class DirectionController {

    private final DirectionService directionService;

    @Autowired
    public DirectionController(DirectionService directionService) {
        this.directionService = directionService;
    }

    @Operation(summary = "Créer un nouvel agent de direction", description = "Crée un agent de direction avec un compte utilisateur et une photo optionnelle. photoUrl contient uniquement le nom du fichier, à préfixer par '/files/' pour l'accès.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agent de direction créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "École, fonction ou rôle non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createDirection(
            @Valid @ModelAttribute DirectionRequestDTO directionDTO,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile) {
        try {
            DirectionResponseDTO createdDirection = directionService.createDirection(directionDTO, photoFile);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Agent de direction créé avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de l’agent de direction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un agent de direction par ID", description = "Retourne les détails d’un agent de direction spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agent de direction trouvé"),
            @ApiResponse(responseCode = "404", description = "Agent de direction non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getDirectionById(@PathVariable String id) {
        try {
            DirectionResponseDTO direction = directionService.getDirection(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Agent de direction récupéré avec succès !", direction, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de l’agent de direction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les agents de direction", description = "Retourne une liste de tous les agents de direction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des agents de direction récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllDirections() {
        try {
            List<DirectionResponseDTO> directions = directionService.getAllDirections();
            return ResponseEntity.ok(new CustomApiResponse(true, "Agents de direction récupérés avec succès !", directions, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des agents de direction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un agent de direction", description = "Met à jour les informations d’un agent de direction existant avec une photo optionnelle. photoUrl contient uniquement le nom du fichier, à préfixer par '/files/' pour l'accès.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agent de direction mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Agent de direction, école ou fonction non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateDirection(
            @PathVariable String id,
            @Valid @ModelAttribute DirectionRequestDTO directionDTO,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile) {
        try {
            DirectionResponseDTO updatedDirection = directionService.updateDirection(id, directionDTO, photoFile);
            return ResponseEntity.ok(new CustomApiResponse(true, "Agent de direction mis à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de l’agent de direction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer un agent de direction", description = "Supprime un agent de direction, son compte utilisateur et sa photo associée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agent de direction supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Agent de direction non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteDirection(@PathVariable String id) {
        try {
            directionService.deleteDirection(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Agent de direction supprimé avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de l’agent de direction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}