package com.souldev.security.controllers;


import com.souldev.security.payload.request.MatiereDTO;
import com.souldev.security.payload.request.MatiereResponseDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.MatiereService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/matiere")
@Tag(name = "Matieres", description = "API pour la gestion des matières")
public class MatiereController {

    private final MatiereService matiereService;

    @Autowired
    public MatiereController(MatiereService matiereService) {
        this.matiereService = matiereService;
    }

    @Operation(summary = "Créer une nouvelle matière", description = "Crée une matière avec les informations fournies dans le DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Matière créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Filière non trouvée")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createMatiere(@ModelAttribute MatiereDTO matiereDTO) {
        try {
            MatiereResponseDTO createdMatiere = matiereService.createMatiere(matiereDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Matière créée avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de la matière", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer une matière par ID", description = "Retourne les détails d'une matière spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière trouvée"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getMatiereById(@PathVariable String id) {
        try {
            return matiereService.getMatiereById(id)
                    .map(matiere -> ResponseEntity.ok(new CustomApiResponse(true, "Matière récupérée avec succès !", matiere, HttpStatus.OK.value())))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new CustomApiResponse(false, "Matière non trouvée avec l'ID : " + id, null, HttpStatus.NOT_FOUND.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de la matière", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer toutes les matières", description = "Retourne une liste de toutes les matières.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des matières récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération des matières")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllMatieres() {
        try {
            List<MatiereResponseDTO> matieres = matiereService.getAllMatieres();
            return ResponseEntity.ok(new CustomApiResponse(true, "Matières récupérées avec succès !", matieres, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des matières : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer les matières par classe", description = "Retourne une liste des matières associées à une classe donnée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des matières récupérée"),
            @ApiResponse(responseCode = "404", description = "Classe ou filière non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    @GetMapping("/classe/{classeId}")
    public ResponseEntity<CustomApiResponse> getMatieresByClasse(@PathVariable String classeId) {
        try {
            List<MatiereResponseDTO> matieres = matiereService.getMatieresByClasse(classeId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Matières récupérées avec succès pour la classe !", matieres, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des matières : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer les matières par filière", description = "Retourne une liste des matières associées à une filière donnée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des matières récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    @GetMapping("/filiere/{filiereId}")
    public ResponseEntity<CustomApiResponse> getMatieresByFiliere(@PathVariable String filiereId) {
        try {
            List<MatiereResponseDTO> matieres = matiereService.getMatieresByFiliere(filiereId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Matières récupérées avec succès pour la filière !", matieres, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des matières : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour une matière", description = "Met à jour les informations d'une matière existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Matière ou filière non trouvée")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateMatiere(@PathVariable String id, @ModelAttribute MatiereDTO matiereDTO) {
        try {
            MatiereResponseDTO updatedMatiere = matiereService.updateMatiere(id, matiereDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Matière mise à jour avec succès !", updatedMatiere, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de la matière", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Ajouter une filière à une matière", description = "Ajoute une filière à la liste des filières associées à une matière.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filière ajoutée avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière ou filière non trouvée"),
            @ApiResponse(responseCode = "400", description = "Filière déjà associée")
    })
    @PostMapping("/{id}/filieres/{filiereId}")
    public ResponseEntity<CustomApiResponse> addFiliereToMatiere(@PathVariable String id, @PathVariable String filiereId) {
        try {
            MatiereResponseDTO updatedMatiere = matiereService.addFiliereToMatiere(id, filiereId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Filière ajoutée à la matière avec succès !", updatedMatiere, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de l'ajout de la filière", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer une filière d'une matière", description = "Supprime une filière de la liste des filières associées à une matière.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filière supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière ou filière non trouvée"),
            @ApiResponse(responseCode = "400", description = "Filière non associée")
    })
    @DeleteMapping("/{id}/filieres/{filiereId}")
    public ResponseEntity<CustomApiResponse> removeFiliereFromMatiere(@PathVariable String id, @PathVariable String filiereId) {
        try {
            MatiereResponseDTO updatedMatiere = matiereService.removeFiliereFromMatiere(id, filiereId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Filière supprimée de la matière avec succès !", updatedMatiere, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de la filière", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
