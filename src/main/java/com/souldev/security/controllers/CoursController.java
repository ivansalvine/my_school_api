package com.souldev.security.controllers;

import com.souldev.security.payload.request.CoursDTO;
import com.souldev.security.payload.response.CoursResponseDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.CoursService;
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
@RequestMapping("/cours")
@Tag(name = "Cours", description = "API pour la gestion des cours")
@Validated
public class CoursController {

    private final CoursService coursService;

    @Autowired
    public CoursController(CoursService coursService) {
        this.coursService = coursService;
    }

    @Operation(summary = "Créer un nouveau cours", description = "Crée un cours avec les informations fournies dans le DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cours créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createCours(@Valid @ModelAttribute CoursDTO coursDTO) {
        try {
            CoursResponseDTO createdCours = coursService.createCours(coursDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Cours créé avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création du cours", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un cours par ID", description = "Retourne les détails d'un cours spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours trouvé"),
            @ApiResponse(responseCode = "404", description = "Cours non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getCoursById(@PathVariable String id) {
        try {
            CoursResponseDTO cours = coursService.getCoursById(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Cours récupéré avec succès !", cours, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération du cours", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les cours", description = "Retourne une liste de tous les cours.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des cours récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllCours() {
        try {
            List<CoursResponseDTO> cours = coursService.getAllCours();
            return ResponseEntity.ok(new CustomApiResponse(true, "Cours récupérés avec succès !", cours, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des cours : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer les cours par matière", description = "Retourne une liste des cours associés à une matière donnée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des cours récupérée"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<CustomApiResponse> getCoursByMatiere(@PathVariable String matiereId) {
        try {
            List<CoursResponseDTO> cours = coursService.getCoursByMatiere(matiereId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Cours récupérés avec succès pour la matière !", cours, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des cours : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un cours", description = "Met à jour les informations d'un cours existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Cours ou matière non trouvé")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateCours(
            @PathVariable String id,
            @ModelAttribute CoursDTO coursDTO
    ) {
        try {
            CoursResponseDTO updatedCours = coursService.updateCours(id, coursDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Cours mis à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour du cours", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer un cours", description = "Supprime un cours spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Cours non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteCours(@PathVariable String id) {
        try {
            coursService.deleteCours(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Cours supprimé avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression du cours", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}