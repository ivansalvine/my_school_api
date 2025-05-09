package com.souldev.security.controllers;

import com.souldev.security.payload.request.EleveDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.EleveResponseDTO;
import com.souldev.security.services.EleveService;
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
import java.util.List;

@RestController
@RequestMapping("/eleve")
@Tag(name = "Élèves", description = "API pour la gestion des élèves")
@Validated
public class EleveController {

    private final EleveService eleveService;

    @Autowired
    public EleveController(EleveService eleveService) {
        this.eleveService = eleveService;
    }

    @Operation(summary = "Créer un nouvel élève", description = "Crée un élève avec un compte utilisateur et une photo optionnelle. photoUrl contient uniquement le nom du fichier, à préfixer par '/files/' pour l'accès.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Élève créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Classe ou rôle non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createEleve(
            @ModelAttribute EleveDTO eleveDTO,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile) {
        try {
            EleveResponseDTO createdEleve = eleveService.createEleve(eleveDTO, photoFile);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Élève créé avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de l’élève", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un élève par ID", description = "Retourne les détails d’un élève spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élève trouvé"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getEleveById(@PathVariable String id) {
        try {
            EleveResponseDTO eleve = eleveService.getEleveById(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Élève récupéré avec succès !", eleve, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de l’élève", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les élèves", description = "Retourne une liste de tous les élèves.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des élèves récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllEleves() {
        try {
            List<EleveResponseDTO> eleves = eleveService.getAllEleves();
            return ResponseEntity.ok(new CustomApiResponse(true, "Élèves récupérés avec succès !", eleves, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des élèves", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer les élèves par classe", description = "Retourne une liste des élèves d’une classe spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élèves trouvés"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/classe/{classeId}")
    public ResponseEntity<CustomApiResponse> getElevesByClasse(@PathVariable String classeId) {
        try {
            List<EleveResponseDTO> eleves = eleveService.getElevesByClasse(classeId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Élèves récupérés avec succès !", eleves, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des élèves", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un élève par ID utilisateur", description = "Retourne les détails d’un élève associé à un utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élève trouvé"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomApiResponse> getEleveByUserId(@PathVariable String userId) {
        try {
            EleveResponseDTO eleve = eleveService.getEleveByUserId(userId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Élève récupéré avec succès !", eleve, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de l’élève", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un élève par numéro d’étudiant", description = "Retourne les détails d’un élève par son numéro d’étudiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élève trouvé"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé")
    })
    @GetMapping("/numero/{numeroEtudiant}")
    public ResponseEntity<CustomApiResponse> getEleveByNumeroEtudiant(@PathVariable String numeroEtudiant) {
        try {
            EleveResponseDTO eleve = eleveService.getEleveByNumeroEtudiant(numeroEtudiant);
            return ResponseEntity.ok(new CustomApiResponse(true, "Élève récupéré avec succès !", eleve, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de l’élève", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un élève", description = "Met à jour les informations d’un élève existant avec une photo optionnelle. photoUrl contient uniquement le nom du fichier, à préfixer par '/files/' pour l'accès.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élève mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Élève ou classe non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateEleve(
            @PathVariable String id,
            @ModelAttribute EleveDTO eleveDTO,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile) {
        try {
            EleveResponseDTO updatedEleve = eleveService.updateEleve(id, eleveDTO, photoFile);
            return ResponseEntity.ok(new CustomApiResponse(true, "Élève mis à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de l’élève", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer un élève", description = "Supprime un élève, son compte utilisateur et sa photo associée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Élève supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteEleve(@PathVariable String id) {
        try {
            eleveService.deleteEleve(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Élève supprimé avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de l’élève", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}