package com.souldev.security.controllers;


import com.souldev.security.payload.request.ClasseDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.ClasseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classe")
@Tag(name = "Classes", description = "API pour la gestion des classes")
public class ClasseController {

    private final ClasseService classeService;

    @Autowired
    public ClasseController(ClasseService classeService) {
        this.classeService = classeService;
    }

    @Operation(summary = "Créer une nouvelle classe", description = "Crée une classe avec les informations fournies dans le DTO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Classe créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Filière ou année scolaire non trouvée")
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> createClasse(@ModelAttribute ClasseDTO classeDTO) {
        try {
            ClasseDTO createdClasse = classeService.createClasse(classeDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Classe créée avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de la classe", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer une classe par ID", description = "Retourne les détails d'une classe spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classe trouvée"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getClasseById(@PathVariable String id) {
        try {
            return classeService.getClasseById(id)
                    .map(classe -> ResponseEntity.ok(new CustomApiResponse(true, "Classe récupérée avec succès !", classe, HttpStatus.OK.value())))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new CustomApiResponse(false, "Classe non trouvée avec l'ID : " + id, null, HttpStatus.NOT_FOUND.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de la classe", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer toutes les classes", description = "Retourne une liste de toutes les classes, avec pagination optionnelle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des classes récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération des classes")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllClasses() {
        try {
            List<ClasseDTO> classePage = classeService.getAllClasses();
            return ResponseEntity.ok(new CustomApiResponse(true, "Classes récupérées avec succès !", classePage, HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des classes : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer les classes par année scolaire", description = "Retourne une liste des classes associées à une année scolaire donnée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des classes récupérée"),
            @ApiResponse(responseCode = "404", description = "Année scolaire non trouvée")
    })
    @GetMapping("/annee-scolaire/{anneeScolaireId}")
    public ResponseEntity<List<ClasseDTO>> getClassesByAnneeScolaire(@PathVariable String anneeScolaireId) {
        return ResponseEntity.ok(classeService.getClassesByAnneeScolaire(anneeScolaireId));
    }

    @Operation(summary = "Mettre à jour une classe", description = "Met à jour les informations d'une classe existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classe mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Classe, filière ou année scolaire non trouvée")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateClasse(@PathVariable String id, @ModelAttribute ClasseDTO classeDTO) {
        try {
            ClasseDTO updatedClasse = classeService.updateClasse(id, classeDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Mise à jour éffectuée avec succès !", null, 200));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de la classe : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

    }

    @Operation(summary = "Supprimer une classe", description = "Supprime une classe spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Classe supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Classe non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteClasse(@PathVariable String id) {
        try {
            classeService.deleteClasse(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Suppression éffectuée avec succès !", null, 200));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de la classe : " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
