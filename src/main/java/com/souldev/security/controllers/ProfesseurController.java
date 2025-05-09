package com.souldev.security.controllers;

import com.souldev.security.payload.request.ProfesseurDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.ProfesseurResponseDTO;
import com.souldev.security.services.ProfesseurService;
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
@RequestMapping("/professeur")
@Tag(name = "Professeurs", description = "API pour la gestion des professeurs")
@Validated
public class ProfesseurController {

    private final ProfesseurService professeurService;

    @Autowired
    public ProfesseurController(ProfesseurService professeurService) {
        this.professeurService = professeurService;
    }

    @Operation(summary = "Créer un nouveau professeur", description = "Crée un professeur avec un compte utilisateur et des fichiers optionnels (image, licence).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Professeur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "École ou rôle non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createProfesseur(
            @Valid @ModelAttribute ProfesseurDTO professeurDTO,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "licence", required = false) MultipartFile licenceFile
    ) {
        try {
            ProfesseurResponseDTO createdProfesseur = professeurService.createProfesseur(professeurDTO, imageFile, licenceFile);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Professeur créé avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création du professeur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un professeur par ID", description = "Retourne les détails d'un professeur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professeur trouvé"),
            @ApiResponse(responseCode = "404", description = "Professeur non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getProfesseurById(@PathVariable String id) {
        try {
            ProfesseurResponseDTO professeur = professeurService.getProfesseurById(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Professeur récupéré avec succès !", professeur, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération du professeur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les professeurs", description = "Retourne une liste de tous les professeurs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des professeurs récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllProfesseurs() {
        try {
            List<ProfesseurResponseDTO> professeurs = professeurService.getAllProfesseurs();
            return ResponseEntity.ok(new CustomApiResponse(true, "Professeurs récupérés avec succès !", professeurs, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des professeurs : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un professeur par ID utilisateur", description = "Retourne les détails d'un professeur associé à un utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professeur trouvé"),
            @ApiResponse(responseCode = "404", description = "Professeur non trouvé")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomApiResponse> getProfesseurByUserId(@PathVariable String userId) {
        try {
            ProfesseurResponseDTO professeur = professeurService.getProfesseurByUserId(userId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Professeur récupéré avec succès !", professeur, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération du professeur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un professeur", description = "Met à jour les informations d'un professeur existant avec des fichiers optionnels.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professeur mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Professeur ou école non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateProfesseur(
            @PathVariable String id,
            @Valid @ModelAttribute ProfesseurDTO professeurDTO,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "licence", required = false) MultipartFile licenceFile) {
        try {
            ProfesseurResponseDTO updatedProfesseur = professeurService.updateProfesseur(id, professeurDTO, imageFile, licenceFile);
            return ResponseEntity.ok(new CustomApiResponse(true, "Professeur mis à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour du professeur", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}