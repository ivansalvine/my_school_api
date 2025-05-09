package com.souldev.security.controllers;

import com.souldev.security.entities.Cycle;
import com.souldev.security.entities.Filiere;
import com.souldev.security.payload.request.CycleDTO;
import com.souldev.security.payload.request.CycleResponseDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.CycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cycle")
public class CycleController {

    private static final Logger logger = LoggerFactory.getLogger(CycleController.class);
    private final CycleService cycleService;

    @Autowired
    public CycleController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @Operation(summary = "Créer un nouveau cycle", description = "Crée un cycle pour une école spécifiée")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PostMapping("/create")
    public ResponseEntity<CustomApiResponse> createCycle(@ModelAttribute CycleDTO cycleDTO) {
        logger.info("Requête pour créer un cycle : {}", cycleDTO.getNom());
        try {
            Cycle createdCycle = cycleService.createCycle(cycleDTO);
            if(createdCycle != null) {
                return ResponseEntity.ok(new CustomApiResponse(true, "Cycle créé avec succès", null, 200));
            };
            return ResponseEntity.ok(new CustomApiResponse(true, "Oups ! une erreur est survenue !", null, 200));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation : {}", e.getMessage());
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 400));
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la création : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer un cycle par ID", description = "Récupère les détails d'un cycle spécifique")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getCycleById(@PathVariable String id) {
        logger.info("Requête pour récupérer le cycle ID : {}", id);
        try {
            Optional<Cycle> cycle = cycleService.getCycleById(id);
            if (cycle.isPresent()) {
                return ResponseEntity.ok(new CustomApiResponse(true, "Cycle récupéré avec succès", cycle.get(), 200));
            } else {
                return ResponseEntity.status(404).body(new CustomApiResponse(false, "Cycle non trouvé", null, 404));
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer tous les cycles", description = "Récupère la liste de tous les cycles")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<CustomApiResponse> getAllCycles() {
        logger.info("Requête pour récupérer tous les cycles");
        try {
            List<CycleResponseDTO> cycles = cycleService.getAllCycles();
            return ResponseEntity.ok(new CustomApiResponse(true, "Liste des cycles récupérée", cycles, 200));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer les cycles d'une école", description = "Récupère les cycles associés à une école spécifique")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/ecole/{ecoleId}")
    public ResponseEntity<CustomApiResponse> getCyclesByEcole(@PathVariable String ecoleId) {
        logger.info("Requête pour récupérer les cycles de l'école ID : {}", ecoleId);
        try {
            List<Cycle> cycles = cycleService.getCyclesByEcole(ecoleId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Cycles de l'école récupérés", cycles, 200));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Récupérer les filières d'un cycle", description = "Récupère les filières associées à un cycle")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/{cycleId}/filieres")
    public ResponseEntity<CustomApiResponse> getFilieresByCycle(@PathVariable String cycleId) {
        logger.info("Requête pour récupérer les filières du cycle ID : {}", cycleId);
        try {
            List<Filiere> filieres = cycleService.getFilieresByCycle(cycleId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Filières du cycle récupérées", filieres, 200));
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Mettre à jour un cycle", description = "Met à jour les informations d'un cycle existant")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR', 'ROLE_SUPER_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse> updateCycle(
            @PathVariable String id,
            @ModelAttribute CycleDTO cycleDTO
    ) {
        logger.info("Requête pour mettre à jour le cycle ID : {}", id);
        try {
            Cycle updatedCycle = cycleService.updateCycle(id, cycleDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Cycle mis à jour avec succès", updatedCycle, 200));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation : {}", e.getMessage());
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 400));
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la mise à jour : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }

    @Operation(summary = "Supprimer un cycle", description = "Supprime un cycle s'il n'a pas de filières ou semestres associés")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteCycle(@PathVariable String id) {
        logger.info("Requête pour supprimer le cycle ID : {}", id);
        try {
            cycleService.deleteCycle(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Cycle supprimé avec succès", null, 200));
        } catch (IllegalStateException e) {
            logger.error("Contrainte de suppression : {}", e.getMessage());
            return ResponseEntity.status(409).body(new CustomApiResponse(false, e.getMessage(), null, 409));
        } catch (RuntimeException e) {
            logger.error("Cycle non trouvé : {}", e.getMessage());
            return ResponseEntity.status(404).body(new CustomApiResponse(false, e.getMessage(), null, 404));
        } catch (Exception e) {
            logger.error("Erreur serveur : {}", e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse(false, "Erreur serveur : " + e.getMessage(), null, 500));
        }
    }
}