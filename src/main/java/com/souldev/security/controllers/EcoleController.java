package com.souldev.security.controllers;

import com.souldev.security.entities.Ecole;
import com.souldev.security.payload.request.EcoleCreateDTO;
import com.souldev.security.payload.request.EcoleDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.EcoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ecole")
public class EcoleController {
    private static final Logger logger = LoggerFactory.getLogger(EcoleController.class);
    private final EcoleService ecoleService;

    public EcoleController(EcoleService ecoleService) {
        this.ecoleService = ecoleService;
    }

    @Operation(summary = "Créer une nouvelle école")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse> createEcole(
            @RequestParam("nom") String nom,
            @RequestParam("adresse") String adresse,
            @RequestParam("telephone") String telephone,
            @RequestParam("email") String email,
            @RequestParam("domaineName") String domaineName,
            @RequestParam("logo") String logo
    ) {
        try {
            // Valider le fichier logo (si fourni)
            EcoleCreateDTO ecoleCreateDTO = new EcoleCreateDTO(
                    nom,
                    adresse,
                    telephone,
                    email,
                    domaineName,
                    logo
            );
            Ecole createdEcole = ecoleService.createEcole(ecoleCreateDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "École créée avec succès", createdEcole, 200));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation : {}", e.getMessage());
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 400));
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la création : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Méthode utilitaire pour valider le type de fichier image
    private boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return true; // Fichier facultatif
        }
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg")
        );
    }

//    @Operation(summary = "Mettre à jour une école")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
//    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    //@PutMapping("")
//    public ResponseEntity<CustomApiResponse> updateEcole(
//            @PathVariable String id,
//            @RequestBody EcoleCreateDTO ecoleDetails
//    ) {
//        logger.info("Requête pour mettre à jour l’école avec ID : {}", id);
//        try {
//            Ecole updatedEcole = ecoleService.updateEcoleLogo(id, ecoleDetails);
//            return ResponseEntity.ok(new CustomApiResponse(true, "École mise à jour avec succès", updatedEcole, 200));
//        } catch (IllegalArgumentException e) {
//            logger.error("Erreur de validation : {}", e.getMessage());
//            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 400));
//        } catch (RuntimeException e) {
//            logger.error("Erreur lors de la mise à jour : {}", e.getMessage());
//            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
//        }
//    }

    @Operation(summary = "Récupérer toutes les écoles")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<CustomApiResponse> getAllEcoles() {
        logger.info("Récupération de la liste complète des écoles");

        try {
            List<EcoleDTO> ecoles = ecoleService.getAllEcoles();
            return ResponseEntity.ok(
                    new CustomApiResponse(
                            true,
                            "Liste complète des écoles récupérée",
                            ecoles,
                            200
                    )
            );
        } catch (Exception e) {
            logger.error("Erreur: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                    new CustomApiResponse(
                            false,
                            "Erreur lors de la récupération",
                            null,
                            500
                    )
            );
        }
    }

//    @Operation(summary = "Mettre à jour le logo d'une école")
//    @SecurityRequirement(name = "bearerAuth")
//    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
//    @PostMapping(value = "/update-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<CustomApiResponse> updateLogo(
//            @ModelAttribute EcoleLogoUpdateDTO logoUpdateDTO) {
//
//        logger.info("Mise à jour du logo pour l'école ID: {}", logoUpdateDTO.getEcoleId());
//
//        try {
//            Ecole updatedEcole = ecoleService.updateEcoleLogo(
//                    logoUpdateDTO.getEcoleId(),
//                    logoUpdateDTO.getLogoFile()
//            );
//
//            return ResponseEntity.ok(
//                    new CustomApiResponse(
//                            true,
//                            "Logo mis à jour avec succès",
//                            updatedEcole,
//                            200
//                    )
//            );
//        } catch (IllegalArgumentException e) {
//            logger.error("Erreur de validation: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(
//                    new CustomApiResponse(false, e.getMessage(), null, 400)
//            );
//        } catch (Exception e) {
//            logger.error("Erreur serveur: {}", e.getMessage());
//            return ResponseEntity.status(500).body(
//                    new CustomApiResponse(false, "Erreur serveur", null, 500)
//            );
//        }
//    }

    @Operation(summary = "Supprimer une école")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteEcole(@PathVariable String id) {
        logger.info("Tentative de suppression de l'école ID: {}", id);

        try {
            ecoleService.deleteEcole(id);
            return ResponseEntity.ok(
                    new CustomApiResponse(
                            true,
                            "École supprimée avec succès",
                            null,
                            200
                    )
            );
        } catch (EntityNotFoundException e) {
            logger.error("École non trouvée: {}", e.getMessage());
            return ResponseEntity.status(404).body(
                    new CustomApiResponse(false, e.getMessage(), null, 404)
            );
        } catch (IllegalStateException e) {
            logger.error("Contrainte de suppression: {}", e.getMessage());
            return ResponseEntity.status(409).body(
                    new CustomApiResponse(false, e.getMessage(), null, 409)
            );
        } catch (Exception e) {
            logger.error("Erreur serveur: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                    new CustomApiResponse(false, "Erreur lors de la suppression", null, 500)
            );
        }
    }
}