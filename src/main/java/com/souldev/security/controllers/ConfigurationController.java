package com.souldev.security.controllers;

import com.souldev.security.entities.Configuration;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.services.ConfigurationService;
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
@RequestMapping("/configuration")
@Tag(name = "Configurations", description = "API de gestion des configurations dynamiques de l'application")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostMapping("/save")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Créer ou mettre à jour une configuration", description = "Crée une nouvelle configuration ou met à jour une existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration enregistrée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l’enregistrement de la configuration")
    })
    public ResponseEntity<CustomApiResponse> saveConfiguration(@RequestBody Configuration configuration) {
        try {
            Configuration savedConfig = configurationService.saveConfiguration(configuration);
            return ResponseEntity.ok(new CustomApiResponse(true, "Configuration enregistrée avec succès", savedConfig, 200));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 500));
        }
    }

    @GetMapping("/{key}")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Récupérer une configuration par clé", description = "Retourne une configuration spécifique à partir de sa clé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration trouvée"),
            @ApiResponse(responseCode = "400", description = "Configuration non trouvée")
    })
    public ResponseEntity<CustomApiResponse> getConfigurationByKey(
            @Parameter(description = "Clé de la configuration", required = true) @PathVariable String key) {
        return configurationService.getConfigurationByKey(key)
                .map(config -> ResponseEntity.ok(new CustomApiResponse(true, "Configuration trouvée", config, 200)))
                .orElseGet(() -> ResponseEntity.badRequest().body(new CustomApiResponse(false, "Configuration non trouvée", null, 400)));
    }

    @GetMapping("/all")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Lister toutes les configurations", description = "Retourne la liste complète des configurations existantes.")
    public ResponseEntity<CustomApiResponse> getAllConfigurations() {
        List<Configuration> configurations = configurationService.getAllConfigurations();
        return ResponseEntity.ok(new CustomApiResponse(true, "Liste des configurations", configurations, 200));
    }

    @PutMapping("/{key}")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Mettre à jour une configuration", description = "Modifie la valeur d'une configuration existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la mise à jour")
    })
    public ResponseEntity<CustomApiResponse> updateConfiguration(
            @Parameter(description = "Clé de la configuration", required = true) @PathVariable String key,
            @RequestBody String newValue) {
        try {
            Configuration updatedConfig = configurationService.updateConfiguration(key, newValue);
            return ResponseEntity.ok(new CustomApiResponse(true, "Configuration mise à jour avec succès", updatedConfig, 200));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 500));
        }
    }

    @DeleteMapping("/{key}")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Supprimer une configuration", description = "Supprime une configuration spécifique à partir de sa clé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration supprimée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la suppression")
    })
    public ResponseEntity<CustomApiResponse> deleteConfiguration(
            @Parameter(description = "Clé de la configuration", required = true) @PathVariable String key) {
        try {
            configurationService.deleteConfiguration(key);
            return ResponseEntity.ok(new CustomApiResponse(true, "Configuration supprimée avec succès", null, 200));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(false, e.getMessage(), null, 500));
        }
    }

    @GetMapping("/{key}/value")
    @SecurityRequirement(name = "bearerAuth") // Indique que cet endpoint nécessite un token JWT
    @Operation(summary = "Récupérer la valeur d’une configuration avec valeur par défaut", description = "Retourne la valeur de la configuration, ou la valeur par défaut si non trouvée.")
    public ResponseEntity<CustomApiResponse> getValueOrDefault(
            @Parameter(description = "Clé de la configuration", required = true) @PathVariable String key,
            @RequestParam(defaultValue = "") @Parameter(description = "Valeur par défaut si la clé est absente") String defaultValue) {
        String value = configurationService.getValueOrDefault(key, defaultValue);
        return ResponseEntity.ok(new CustomApiResponse(true, "Valeur de la configuration", value, 200));
    }
}
