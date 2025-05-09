package com.souldev.security.controllers;

import com.souldev.security.entities.AnneeScolaire;
import com.souldev.security.payload.request.AnneScolaireModel;
import com.souldev.security.payload.response.AnneeScolaireResponseDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.AnneeScolaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/annee-scolaire")
@Tag(name = "Année Scolaire", description = "API pour la gestion des années scolaires")
public class AnneeScolaireController {
    private static final Logger logger = LoggerFactory.getLogger(AnneeScolaireController.class);

    private final AnneeScolaireService anneeScolaireService;

    @Autowired
    public AnneeScolaireController(AnneeScolaireService anneeScolaireService) {
        this.anneeScolaireService = anneeScolaireService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Créer une nouvelle année scolaire",
            description = "Crée une année scolaire avec un nom au format 'YYYY-YYYY' (ex. '2024-2025')."
    )
    public ResponseEntity<CustomApiResponse> create(
            @ModelAttribute AnneScolaireModel anneScolaireModel
    ) {
        logger.info("Requête POST pour créer une année scolaire avec le nom : {}", anneScolaireModel.getNom());
        try {
            boolean isCreated = anneeScolaireService.createAnneeScolaire(anneScolaireModel);
            logger.info("Année scolaire créée avec succès");
            if(isCreated) return ResponseEntity.ok(new CustomApiResponse(true, "Année scolaire enregistrée avec succès !", null, 200));
            return ResponseEntity.ok(new CustomApiResponse(false, "Une erreur est survenue !", null, 200));
        } catch (IllegalArgumentException e) {
            logger.error("Échec de la création de l'année scolaire '{}': {}", anneScolaireModel.getNom(), e.getMessage());
            return ResponseEntity.ok(new CustomApiResponse(false, e.getMessage(), null, 400));
        }
    }

    @GetMapping
    @Operation(
            summary = "Récupérer toutes les années scolaires",
            description = "Retourne la liste de toutes les années scolaires enregistrées."
    )
    public ResponseEntity<CustomApiResponse> getAll() {
        logger.info("Requête GET pour récupérer toutes les années scolaires");
        List<AnneeScolaireResponseDTO> anneesScolaires = anneeScolaireService.getAllAnneeScolaires();
        logger.debug("Nombre d'années scolaires récupérées : {}", anneesScolaires.size());
        return ResponseEntity.ok(new CustomApiResponse(true, "Années scolaire recperées avec succès !", anneesScolaires, 200));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer une année scolaire par ID",
            description = "Retourne une année scolaire spécifique en fonction de son identifiant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Année scolaire trouvée",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnneeScolaire.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Année scolaire introuvable pour l'ID spécifié",
                    content = @Content
            )
    })
    public ResponseEntity<CustomApiResponse> getById(
            @PathVariable
            @Parameter(description = "ID de l'année scolaire à récupérer", required = true)
            String id
    ) {
        logger.info("Requête GET pour récupérer l'année scolaire avec l'ID : {}", id);
        try {
            AnneeScolaireResponseDTO anneeScolaire = anneeScolaireService.getAnneeScolaireById(id);
            logger.debug("Année scolaire récupérée : Nom = {}", anneeScolaire.getNom());
            return ResponseEntity.ok(new CustomApiResponse(true, "Année scolaire recuperée avec succès !", anneeScolaire, 200));
        } catch (IllegalArgumentException e) {
            logger.error("Échec de la récupération de l'année scolaire avec l'ID {}: {}", id, e.getMessage());
            return ResponseEntity.ok(new CustomApiResponse(false, e.getMessage(), null, 400));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Mettre à jour une année scolaire",
            description = "Met à jour le nom d'une année scolaire existante identifiée par son ID."
    )
    public ResponseEntity<CustomApiResponse> update(
            @PathVariable String id,
            @ModelAttribute AnneScolaireModel anneScolaireModel
    ) {
        logger.info("Requête PUT pour mettre à jour l'année scolaire avec l'ID : {}, Nouveau nom : {}", id, anneScolaireModel.getNom());
        try {
            AnneeScolaire updatedAnneeScolaire = anneeScolaireService.updateAnneeScolaire(id, anneScolaireModel);
            logger.info("Année scolaire mise à jour avec succès, ID : {}, Nom : {}", updatedAnneeScolaire.getId(), updatedAnneeScolaire.getNom());
            return ResponseEntity.ok(new CustomApiResponse(true, "Année scolaire modifiée avec succès !", null, 200));
        } catch (IllegalArgumentException e) {
            logger.error("Échec de la mise à jour de l'année scolaire avec l'ID {}: {}", id, e.getMessage());
            return ResponseEntity.ok(new CustomApiResponse(false, e.getMessage(), null, 400));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> delete(
            @PathVariable String id
    ) {
        logger.info("Requête DELETE pour supprimer l'année scolaire avec l'ID : {}", id);
        try {
            boolean isDeleted = anneeScolaireService.deleteAnneeScolaire(id);
            if(isDeleted){
                logger.info("Année scolaire supprimée avec succès, ID : {}", id);
                return ResponseEntity.ok(new CustomApiResponse(true, "Suppression effectuée avec succès !", null, 200));
            }

            return ResponseEntity.ok(new CustomApiResponse(false, "Une erreur s'est produite !", null, 400));
        } catch (IllegalArgumentException e) {
            logger.error("Échec de la suppression de l'année scolaire avec l'ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, 500));
        }
    }
}