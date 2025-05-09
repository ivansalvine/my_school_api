package com.souldev.security.controllers;

import com.souldev.security.payload.request.NoteRequestDTO;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.NoteResponseDTO;
import com.souldev.security.services.NoteService;
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
@RequestMapping("/notes")
@Tag(name = "Note", description = "API pour la gestion des notes associées à un élève et un devoir")
@Validated
public class NoteController {
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Operation(summary = "Créer une nouvelle note", description = "Crée une note pour un élève, un devoir, et optionnellement une leçon. Une seule note par élève et devoir est autorisée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides ou note existante"),
            @ApiResponse(responseCode = "404", description = "Élève, devoir ou leçon non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> createNote(@Valid @RequestBody NoteRequestDTO noteDTO) {
        try {
            NoteResponseDTO createdNote = noteService.createNote(noteDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomApiResponse(true, "Note créée avec succès !", null, HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la création de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la création de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la création de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la création de la note", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer une note par ID", description = "Retourne les détails d’une note spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note trouvée"),
            @ApiResponse(responseCode = "404", description = "Note non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse> getNoteById(@PathVariable String id) {
        try {
            NoteResponseDTO note = noteService.getNote(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Note récupérée avec succès !", note, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la récupération de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération de la note", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer toutes les notes", description = "Retourne une liste de toutes les notes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des notes récupérée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<CustomApiResponse> getAllNotes() {
        try {
            List<NoteResponseDTO> notes = noteService.getAllNotes();
            return ResponseEntity.ok(new CustomApiResponse(true, "Notes récupérées avec succès !", notes, HttpStatus.OK.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des notes : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des notes", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer les notes d’un élève", description = "Retourne toutes les notes associées à un élève spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes récupérées"),
            @ApiResponse(responseCode = "404", description = "Élève non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/eleve/{eleveId}")
    public ResponseEntity<CustomApiResponse> getNotesByEleve(@PathVariable String eleveId) {
        try {
            List<NoteResponseDTO> notes = noteService.getNotesByEleve(eleveId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Notes de l’élève récupérées avec succès !", notes, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la récupération des notes : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des notes : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des notes", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Récupérer les notes d’un devoir", description = "Retourne toutes les notes associées à un devoir spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes récupérées"),
            @ApiResponse(responseCode = "404", description = "Devoir non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/devoir/{devoirId}")
    public ResponseEntity<CustomApiResponse> getNotesByDevoir(@PathVariable String devoirId) {
        try {
            List<NoteResponseDTO> notes = noteService.getNotesByDevoir(devoirId);
            return ResponseEntity.ok(new CustomApiResponse(true, "Notes du devoir récupérées avec succès !", notes, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la récupération des notes : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des notes : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la récupération des notes", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Mettre à jour une note", description = "Met à jour les informations d’une note existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d’entrée invalides"),
            @ApiResponse(responseCode = "404", description = "Note, élève, devoir ou leçon non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomApiResponse> updateNote(
            @PathVariable String id,
            @Valid @RequestBody NoteRequestDTO noteDTO) {
        try {
            NoteResponseDTO updatedNote = noteService.updateNote(id, noteDTO);
            return ResponseEntity.ok(new CustomApiResponse(true, "Note mise à jour avec succès !", null, HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la mise à jour de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la mise à jour de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la mise à jour de la note", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Operation(summary = "Supprimer une note", description = "Supprime une note existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Note non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deleteNote(@PathVariable String id) {
        try {
            noteService.deleteNote(id);
            return ResponseEntity.ok(new CustomApiResponse(true, "Note supprimée avec succès !", null, HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            logger.error("Erreur lors de la suppression de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(false, e.getMessage(), null, HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la suppression de la note : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(false, "Erreur serveur lors de la suppression de la note", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}