package com.souldev.security.controllers;

import com.souldev.security.payload.request.DevoirRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.DevoirResponseDTO;
import com.souldev.security.services.DevoirService;
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
@RequestMapping("/devoirs")
@Tag(name = "Devoir", description = "API pour la gestion des devoirs")
@Validated
public class DevoirController {
    private static final Logger logger = LoggerFactory.getLogger(DevoirController.class);

    private final DevoirService devoirService;

    @Autowired
    public DevoirController(DevoirService devoirService) {
        this.devoirService = devoirService;
    }

    @Operation(summary = "Créer un nouveau devoir", description = "Crée un devoir pour une classe, un semestre, une matière et un professeur spécifiques.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Devoir créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Classe, semestre, matière ou professeur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> createDevoir(@RequestBody DevoirRequestDTO devoirDTO) {
        try {
            DevoirResponseDTO createdDevoir = devoirService.createDevoir(devoirDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Devoir créé avec succès !", null, HttpStatus.CREATED.value()));
        }
        catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la création du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        }
        catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la création du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        }
        catch (Exception e) {
            logger.error("Erreur serveur lors de la création du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création du devoir", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer un devoir par ID", description = "Retourne les détails d’un devoir spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devoir trouvé"),
            @ApiResponse(responseCode = "404", description = "Devoir non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getDevoirById(@PathVariable String id) {
        try {
            DevoirResponseDTO devoir = devoirService.getDevoir(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Devoir récupéré avec succès !", devoir, HttpStatus.OK.value()));
        }
        catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la récupération du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        }
        catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération du devoir", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer tous les devoirs", description = "Retourne une liste de tous les devoirs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des devoirs récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllDevoirs() {
        try {
            List<DevoirResponseDTO> devoirs = devoirService.getAllDevoirs();
            return ResponseEntity.ok(new CustomApiResponse(true, "Devoirs récupérés avec succès !", devoirs, HttpStatus.OK.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des devoirs : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des devoirs", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour un devoir", description = "Met à jour les informations d’un devoir existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devoir mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Devoir, classe, semestre, matière ou professeur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> updateDevoir(
            @PathVariable String id,
            @Valid @RequestBody DevoirRequestDTO devoirDTO) {
        try {
            DevoirResponseDTO updatedDevoir = devoirService.updateDevoir(id, devoirDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Devoir mis à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la mise à jour du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la mise à jour du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour du devoir", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer un devoir", description = "Supprime un devoir existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devoir supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Devoir non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteDevoir(@PathVariable String id) {
        try {
            devoirService.deleteDevoir(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Devoir supprimé avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la suppression du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la suppression du devoir : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression du devoir", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}