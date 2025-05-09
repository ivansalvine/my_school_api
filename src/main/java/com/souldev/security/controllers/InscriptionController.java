package com.souldev.security.controllers;

import com.souldev.security.payload.request.InscriptionRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.InscriptionResponseDTO;
import com.souldev.security.services.InscriptionService;
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
@RequestMapping("/inscription")
@Tag(name = "Inscription", description = "API pour la gestion des inscriptions des élèves")
@Validated
public class InscriptionController {

    private final InscriptionService inscriptionService;

    @Autowired
    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @Operation(summary = "Créer une nouvelle inscription", description = "Crée une inscription pour un élève dans une classe, filière et année scolaire spécifiques.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscription créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Élève, filière, classe ou année scolaire non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createInscription(@ModelAttribute InscriptionRequestDTO inscriptionDTO) {
        try {
            InscriptionResponseDTO createdInscription = inscriptionService.createInscription(inscriptionDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Inscription créée avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de l’inscription", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer une inscription par ID", description = "Retourne les détails d’une inscription spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription trouvée"),
            @ApiResponse(responseCode = "404", description = "Inscription non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getInscriptionById(@PathVariable String id) {
        try {
            InscriptionResponseDTO inscription = inscriptionService.getInscription(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Inscription récupérée avec succès !", inscription, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de l’inscription", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer toutes les inscriptions", description = "Retourne une liste de toutes les inscriptions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des inscriptions récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllInscriptions() {
        try {
            List<InscriptionResponseDTO> inscriptions = inscriptionService.getAllInscriptions();
            return ResponseEntity.ok(new CustomApiResponse(true, "Inscriptions récupérées avec succès !", inscriptions, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des inscriptions", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour une inscription", description = "Met à jour les informations d’une inscription existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Inscription, élève, filière, classe ou année scolaire non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateInscription(
            @PathVariable String id,
            @ModelAttribute InscriptionRequestDTO inscriptionDTO) {
        try {
            InscriptionResponseDTO updatedInscription = inscriptionService.updateInscription(id, inscriptionDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Inscription mise à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de l’inscription", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer une inscription", description = "Supprime une inscription existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Inscription non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteInscription(@PathVariable String id) {
        try {
            inscriptionService.deleteInscription(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Inscription supprimée avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de l’inscription", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}