package com.souldev.security.controllers;

import com.souldev.security.entities.TypeMatiere;
import com.souldev.security.payload.request.TypeMatiereRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.TypeMatiereService;
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
@RequestMapping("/type-matieres")
@Tag(name = "TypeMatiere", description = "API pour la gestion des types de matières")
@Validated
public class TypeMatiereController {
    private static final Logger logger = LoggerFactory.getLogger(TypeMatiereController.class);

    private final TypeMatiereService typeMatiereService;

    @Autowired
    public TypeMatiereController(TypeMatiereService typeMatiereService) {
        this.typeMatiereService = typeMatiereService;
    }

    @Operation(summary = "Créer un type de matière", description = "Crée un nouveau type de matière.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Type de matière créé"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> createTypeMatiere(@Valid @RequestBody TypeMatiereRequestDTO dto) {
        try {
            TypeMatiere typeMatiere = typeMatiereService.createTypeMatiere(dto.getName(), dto.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Type de matière créé avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la création du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la création du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un type de matière", description = "Récupère un type de matière par ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Type de matière trouvé"),
            @ApiResponse(responseCode = "404", description = "Type de matière non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getTypeMatiere(@PathVariable String id) {
        try {
            TypeMatiere typeMatiere = typeMatiereService.getTypeMatiere(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Type de matière récupéré avec succès !", typeMatiere, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la récupération du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les types de matières", description = "Récupère la liste de tous les types de matières.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllTypeMatiere() {
        try {
            List<TypeMatiere> types = typeMatiereService.getAllTypeMatiere();
            return ResponseEntity.ok(new CustomApiResponse(true, "Types de matières récupérés avec succès !", types, HttpStatus.OK.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des types de matières : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un type de matière", description = "Met à jour un type de matière existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Type de matière mis à jour"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Type de matière non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> updateTypeMatiere(
            @PathVariable String id, @Valid @RequestBody TypeMatiereRequestDTO dto) {
        try {
            TypeMatiere typeMatiere = typeMatiereService.updateTypeMatiere(id, dto.getName(), dto.getDescription());
            return ResponseEntity.ok(new CustomApiResponse(true, "Type de matière mis à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la mise à jour du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la mise à jour du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer un type de matière", description = "Supprime un type de matière existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Type de matière supprimé"),
            @ApiResponse(responseCode = "404", description = "Type de matière non trouvé"),
            @ApiResponse(responseCode = "400", description = "Type de matière associé à des matières"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteTypeMatiere(@PathVariable String id) {
        try {
            typeMatiereService.deleteTypeMatiere(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Type de matière supprimé avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la suppression du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (IllegalStateException e) {
            logger.error("Erreur lors de la suppression du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la suppression du type de matière : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}

