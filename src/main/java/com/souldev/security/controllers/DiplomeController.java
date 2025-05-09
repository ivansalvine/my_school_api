package com.souldev.security.controllers;

import com.souldev.security.entities.Diplome;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.DiplomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/diplome")
@Tag(name = "Diplômes", description = "API de gestion des diplômes étudiants")
public class DiplomeController {

    private final DiplomeService diplomeService;

    @Autowired
    public DiplomeController(DiplomeService diplomeService) {
        this.diplomeService = diplomeService;
    }

    @PostMapping("/create")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Créer un diplôme", description = "Créer un nouveau diplôme pour un étudiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme créé avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<CustomApiResponse> createDiplome(@RequestBody Diplome diplome) {
        try {
            Diplome createdDiplome = diplomeService.createDiplome(diplome);
            return ResponseEntity.ok(new CustomApiResponse(true, "Diplôme créé avec succès", createdDiplome, 200));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 500));
        }
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Récupérer un diplôme", description = "Obtenir un diplôme par son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme trouvé"),
            @ApiResponse(responseCode = "500", description = "Diplôme non trouvé")
    })
    public ResponseEntity<CustomApiResponse> getDiplomeById(
            @Parameter(description = "ID du diplôme", required = true) @PathVariable String id) {
        return diplomeService.getDiplomeById(id)
                .map(diplome -> ResponseEntity.ok(new CustomApiResponse(true, "Diplôme trouvé", diplome, 200)))
                .orElseGet(() -> ResponseEntity.badRequest().body(new CustomApiResponse(false, "Diplôme non trouvé", null, 500)));
    }

    @GetMapping("/all")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Lister tous les diplômes", description = "Retourne tous les diplômes enregistrés.")
    public ResponseEntity<CustomApiResponse> getAllDiplomes() {
        List<Diplome> diplomes = diplomeService.getAllDiplomes();
        return ResponseEntity.ok(new CustomApiResponse(true, "Liste des diplômes", diplomes, 200));
    }

    @GetMapping("/etudiant/{etudiantId}")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Diplômes d’un étudiant", description = "Retourne tous les diplômes associés à un étudiant.")
    public ResponseEntity<CustomApiResponse> getDiplomesByEtudiant(
            @Parameter(description = "ID de l’étudiant", required = true) @PathVariable String etudiantId) {
        List<Diplome> diplomes = diplomeService.getDiplomesByEtudiant(etudiantId);
        return ResponseEntity.ok(new CustomApiResponse(true, "Diplômes de l’étudiant", diplomes, 200));
    }

    @GetMapping("/delivre/{delivre}")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Diplômes par statut", description = "Retourne les diplômes délivrés ou non.")
    public ResponseEntity<CustomApiResponse> getDiplomesByDelivre(
            @Parameter(description = "true pour délivrés, false pour non délivrés", required = true) @PathVariable boolean delivre) {
        List<Diplome> diplomes = diplomeService.getDiplomesByDelivre(delivre);
        return ResponseEntity.ok(new CustomApiResponse(true, "Diplômes " + (delivre ? "délivrés" : "non délivrés"), diplomes, 200));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Mettre à jour un diplôme", description = "Modifier les informations d’un diplôme existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme mis à jour avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour")
    })
    public ResponseEntity<CustomApiResponse> updateDiplome(
            @Parameter(description = "ID du diplôme", required = true) @PathVariable String id,
            @RequestBody Diplome updatedDiplome) {
        try {
            Diplome diplome = diplomeService.updateDiplome(id, updatedDiplome);
            return ResponseEntity.ok(new CustomApiResponse(true, "Diplôme mis à jour avec succès", diplome, 200));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 500));
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Supprimer un diplôme", description = "Supprimer un diplôme en utilisant son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme supprimé avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la suppression")
    })
    public ResponseEntity<CustomApiResponse> deleteDiplome(
            @Parameter(description = "ID du diplôme à supprimer", required = true) @PathVariable String id) {
        try {
            diplomeService.deleteDiplome(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Diplôme supprimé avec succès", null, 200));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 500));
        }
    }
}
