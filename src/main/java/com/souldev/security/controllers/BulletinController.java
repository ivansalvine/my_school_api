package com.souldev.security.controllers;

import com.souldev.security.payload.request.BulletinGenerateRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.PdfGenerationResponseDTO;
import com.souldev.security.services.BulletinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JRException;
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
@RequestMapping("/bulletins")
@Tag(name = "Bulletin", description = "API pour la gestion des bulletins semestriels")
@Validated
public class BulletinController {
    private static final Logger logger = LoggerFactory.getLogger(BulletinController.class);

    private final BulletinService bulletinService;

    @Autowired
    public BulletinController(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

//    @Operation(summary = "Générer un bulletin pour un étudiant", description = "Génère un bulletin pour un étudiant donné et retourne le PDF.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "PDF généré avec succès"),
//            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
//            @ApiResponse(responseCode = "404", description = "Élève, semestre ou classe non trouvé"),
//            @ApiResponse(responseCode = "500", description = "Erreur serveur")
//    })
//    @PostMapping(value = "/generate/student/{eleveId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
//    public ResponseEntity<byte[]> generateBulletinForStudent(
//            @PathVariable String eleveId,
//            @Valid @RequestBody BulletinGenerateRequestDTO dto) {
//        try {
//            byte[] pdf = bulletinService.generateBulletinForStudent(eleveId, dto);
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", String.format("attachment; filename=bulletin_%s_%s.pdf", eleveId, dto.getSemestreId()))
//                    .body(pdf);
//        } catch (EntityNotFoundException e) {
//            logger.error("Erreur lors de la génération du bulletin : {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        } catch (JRException e) {
//            logger.error("Erreur lors de la génération du PDF : {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        } catch (Exception e) {
//            logger.error("Erreur serveur lors de la génération du bulletin : {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @Operation(summary = "Générer les bulletins pour une classe", description = "Génère les bulletins pour tous les étudiants d’une classe et retourne une liste de noms de fichiers PDF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bulletins générés avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Classe ou semestre non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/generate/classe/{classeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> generateBulletinsForClasse(
            @PathVariable String classeId,
            @Valid @RequestBody BulletinGenerateRequestDTO dto) {
        try {
            List<PdfGenerationResponseDTO> results = bulletinService.generateBulletinsForClasse(classeId, dto);
            return ResponseEntity.ok(new CustomApiResponse(
                    true,
                    "Bulletins générés avec succès pour la classe !",
                    results,
                    HttpStatus.OK.value()
            ));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la génération des bulletins : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (JRException e) {
            logger.error("Erreur lors de la génération des PDFs : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur lors de la génération des PDFs", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la génération des bulletins : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la génération des bulletins", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

//    @Operation(summary = "Télécharger un fichier PDF", description = "Télécharge un fichier PDF généré pour un bulletin.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "PDF téléchargé avec succès"),
//            @ApiResponse(responseCode = "404", description = "Fichier PDF non trouvé"),
//            @ApiResponse(responseCode = "500", description = "Erreur serveur")
//    })
//    @GetMapping(value = "/download/{fileName:.+}", produces = MediaType.APPLICATION_PDF_VALUE)
//    public ResponseEntity<byte[]> downloadBulletinPdf(@PathVariable String fileName) {
//        try {
//            byte[] pdf = bulletinService.getBulletinPdf(fileName);
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", "attachment; filename=" + fileName)
//                    .body(pdf);
//        } catch (IOException e) {
//            logger.error("Erreur lors du téléchargement du PDF : {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        } catch (Exception e) {
//            logger.error("Erreur serveur lors du téléchargement du PDF : {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

}