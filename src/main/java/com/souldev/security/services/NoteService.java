package com.souldev.security.services;

import com.souldev.security.entities.Devoir;
import com.souldev.security.entities.Eleve;
import com.souldev.security.entities.Lecon;
import com.souldev.security.entities.Note;
import com.souldev.security.payload.request.NoteRequestDTO;
import com.souldev.security.payload.response.NoteResponseDTO;
import com.souldev.security.repositories.DevoirRepository;
import com.souldev.security.repositories.EleveRepository;
import com.souldev.security.repositories.LeconRepository;
import com.souldev.security.repositories.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteService {
    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;
    private final EleveRepository eleveRepository;
    private final DevoirRepository devoirRepository;
    private final LeconRepository leconRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository,
                       EleveRepository eleveRepository,
                       DevoirRepository devoirRepository,
                       LeconRepository leconRepository) {
        this.noteRepository = noteRepository;
        this.eleveRepository = eleveRepository;
        this.devoirRepository = devoirRepository;
        this.leconRepository = leconRepository;
    }

    public NoteResponseDTO createNote(NoteRequestDTO dto) {
        logger.info("Tentative de création d’une note pour l’élève ID : {} et devoir ID : {}", dto.getEleveId(), dto.getDevoirId());

        // Vérifier si une note existe déjà pour cet élève et ce devoir
        if (noteRepository.existsByEleveIdAndDevoirId(dto.getEleveId(), dto.getDevoirId())) {
            logger.error("Une note existe déjà pour l’élève ID : {} et devoir ID : {}", dto.getEleveId(), dto.getDevoirId());
            throw new IllegalArgumentException("Une note existe déjà pour cet élève et ce devoir");
        }

        Eleve eleve = eleveRepository.findById(dto.getEleveId())
                .orElseThrow(() -> {
                    logger.error("Élève non trouvé avec l’ID : {}", dto.getEleveId());
                    return new EntityNotFoundException("Élève non trouvé avec l’ID : " + dto.getEleveId());
                });

        Devoir devoir = devoirRepository.findById(dto.getDevoirId())
                .orElseThrow(() -> {
                    logger.error("Devoir non trouvé avec l’ID : {}", dto.getDevoirId());
                    return new EntityNotFoundException("Devoir non trouvé avec l’ID : " + dto.getDevoirId());
                });

        Lecon lecon = null;
        if (dto.getLeconId() != null) {
            lecon = leconRepository.findById(dto.getLeconId())
                    .orElseThrow(() -> {
                        logger.error("Leçon non trouvée avec l’ID : {}", dto.getLeconId());
                        return new EntityNotFoundException("Leçon non trouvée avec l’ID : " + dto.getLeconId());
                    });
        }

        Note note = new Note(dto.getValeur(), dto.getCommentaire(), eleve, devoir, lecon);
        Note savedNote = noteRepository.save(note);
        logger.info("Note créée avec succès : ID {}", savedNote.getId());
        return mapToResponseDTO(savedNote);
    }

    public NoteResponseDTO getNote(String id) {
        logger.info("Recherche de la note avec l’ID : {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Note non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Note non trouvée avec l’ID : " + id);
                });
        return mapToResponseDTO(note);
    }

    public List<NoteResponseDTO> getAllNotes() {
        logger.info("Récupération de toutes les notes");
        List<Note> notes = noteRepository.findAll();
        logger.info("Nombre de notes récupérées : {}", notes.size());
        return notes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<NoteResponseDTO> getNotesByEleve(String eleveId) {
        logger.info("Récupération des notes pour l’élève ID : {}", eleveId);
        if (!eleveRepository.existsById(eleveId)) {
            logger.error("Élève non trouvé avec l’ID : {}", eleveId);
            throw new EntityNotFoundException("Élève non trouvé avec l’ID : " + eleveId);
        }
        List<Note> notes = noteRepository.findByEleveId(eleveId);
        logger.info("Nombre de notes récupérées pour l’élève : {}", notes.size());
        return notes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<NoteResponseDTO> getNotesByDevoir(String devoirId) {
        logger.info("Récupération des notes pour le devoir ID : {}", devoirId);
        if (!devoirRepository.existsById(devoirId)) {
            logger.error("Devoir non trouvé avec l’ID : {}", devoirId);
            throw new EntityNotFoundException("Devoir non trouvé avec l’ID : " + devoirId);
        }
        List<Note> notes = noteRepository.findByDevoirId(devoirId);
        logger.info("Nombre de notes récupérées pour le devoir : {}", notes.size());
        return notes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public NoteResponseDTO updateNote(String id, NoteRequestDTO dto) {
        logger.info("Tentative de mise à jour de la note avec ID : {}", id);

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Note non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Note non trouvée avec l’ID : " + id);
                });

        Eleve eleve = eleveRepository.findById(dto.getEleveId())
                .orElseThrow(() -> {
                    logger.error("Élève non trouvé avec l’ID : {}", dto.getEleveId());
                    return new EntityNotFoundException("Élève non trouvé avec l’ID : " + dto.getEleveId());
                });

        Devoir devoir = devoirRepository.findById(dto.getDevoirId())
                .orElseThrow(() -> {
                    logger.error("Devoir non trouvé avec l’ID : {}", dto.getDevoirId());
                    return new EntityNotFoundException("Devoir non trouvé avec l’ID : " + dto.getDevoirId());
                });

        Lecon lecon = null;
        if (dto.getLeconId() != null) {
            lecon = leconRepository.findById(dto.getLeconId())
                    .orElseThrow(() -> {
                        logger.error("Leçon non trouvée avec l’ID : {}", dto.getLeconId());
                        return new EntityNotFoundException("Leçon non trouvée avec l’ID : " + dto.getLeconId());
                    });
        }

        // Vérifier si la mise à jour change eleveId ou devoirId et s’assurer qu’aucune note n’existe pour la nouvelle combinaison
        if (!existingNote.getEleve().getId().equals(dto.getEleveId()) || !existingNote.getDevoir().getId().equals(dto.getDevoirId())) {
            if (noteRepository.existsByEleveIdAndDevoirId(dto.getEleveId(), dto.getDevoirId())) {
                logger.error("Une note existe déjà pour l’élève ID : {} et devoir ID : {}", dto.getEleveId(), dto.getDevoirId());
                throw new IllegalArgumentException("Une note existe déjà pour cet élève et ce devoir");
            }
        }

        existingNote.setValeur(dto.getValeur());
        existingNote.setCommentaire(dto.getCommentaire());
        existingNote.setEleve(eleve);
        existingNote.setDevoir(devoir);
        existingNote.setLecon(lecon);

        Note updatedNote = noteRepository.save(existingNote);
        logger.info("Note mise à jour avec succès : ID {}", updatedNote.getId());
        return mapToResponseDTO(updatedNote);
    }

    public void deleteNote(String id) {
        logger.info("Tentative de suppression de la note avec ID : {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Note non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Note non trouvée avec l’ID : " + id);
                });

        noteRepository.delete(note);
        logger.info("Note supprimée avec succès : ID {}", id);
    }

    private NoteResponseDTO mapToResponseDTO(Note note) {
        return new NoteResponseDTO(
                note.getId(),
                note.getValeur(),
                note.getCommentaire(),
                note.getEleve().getId(),
                note.getDevoir().getId(),
                note.getLecon() != null ? note.getLecon().getId() : null,
                note.getCreatedAt(),
                note.getUpdatedAt(),
                note.getCreatedBy(),
                note.getUpdatedBy()
        );
    }
}