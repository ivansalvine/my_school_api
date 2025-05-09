package com.souldev.security.controllers;

import com.souldev.security.entities.Fonction;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.FonctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/fonction")
@Tag(name = "Fonctions", description = "API pour la gestion des des fonction des direction")
@Validated
public class FonctionController {

    private final FonctionService fonctionService;

    public FonctionController(FonctionService fonctionService) {
        this.fonctionService = fonctionService;
    }

    @Operation(summary = "Créer une nouvelle fonction", description = "Crée une fonction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fonction créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Classe ou rôle non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse> createFonction(
            @RequestParam("name") String name,
            @RequestParam("description") String description) {
        try {
            Fonction createdFonction = fonctionService.createFonction(name, description);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Fonction créé avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de la fonction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer une fonction par ID", description = "Retourne les détails d’une fonction spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fonction trouvée"),
            @ApiResponse(responseCode = "404", description = "Fonction non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getFonctionById(@PathVariable String id) {
        try {
            Fonction fonction = fonctionService.getOne(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Fonction récupérée avec succès !", fonction, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de la fonction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les élèves", description = "Retourne une liste de tous les élèves.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des élèves récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllFonctions() {
        try {
            List<Fonction> fonctions = fonctionService.getAllFonction();
            return ResponseEntity.ok(new CustomApiResponse(true, "Fonctions récupérées avec succès !", fonctions, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des fonctions", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour une fonction", description = "Met à jour les informations d’une fonction existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fonction mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Fonction ou classe non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse> updateFonction(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description
    ) {
        try {
            Fonction updateFonction = fonctionService.updateFonction(name, description, id);
            //System.err.println("updateFonction --> "+updateFonction);
            if(updateFonction != null) {
                return ResponseEntity.ok(new CustomApiResponse(true, "Fonction mise à jour avec succès !", null, HttpStatus.OK.value()));
            }
            else return ResponseEntity.ok(new CustomApiResponse(false, "Une erreur est survenue lors de la mise à jour !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de la fonction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer une fonction", description = "Supprime une fonction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fonction supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Fonction non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteEleve(@PathVariable String id) {
        try {
            fonctionService.deleteFonction(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Fonction supprimée avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de la fonction", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
