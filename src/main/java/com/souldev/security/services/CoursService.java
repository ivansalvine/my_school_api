package com.souldev.security.services;

import com.souldev.security.entities.Cours;
import com.souldev.security.entities.Matiere;
import com.souldev.security.payload.request.CoursDTO;
import com.souldev.security.payload.response.CoursResponseDTO;
import com.souldev.security.repositories.CoursRepository;
import com.souldev.security.repositories.MatiereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoursService {

    private final CoursRepository coursRepository;
    private final MatiereRepository matiereRepository;

    @Autowired
    public CoursService(CoursRepository coursRepository, MatiereRepository matiereRepository) {
        this.coursRepository = coursRepository;
        this.matiereRepository = matiereRepository;
    }

    /**
     * Récupère tous les cours.
     */
    public List<CoursResponseDTO> getAllCours() {
        return coursRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un cours par son ID.
     */
    public CoursResponseDTO getCoursById(String id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours avec ID " + id + " non trouvé"));
        return mapToResponseDTO(cours);
    }

    /**
     * Crée un nouveau cours.
     */
    @Transactional
    public CoursResponseDTO createCours(CoursDTO coursDTO) {
        validateCoursDTO(coursDTO);
        Matiere matiere = matiereRepository.findById(coursDTO.getMatiereId())
                .orElseThrow(() -> new EntityNotFoundException("Matière avec ID " + coursDTO.getMatiereId() + " non trouvée"));

        Cours cours = new Cours();
        cours.setNom(coursDTO.getNom());
        cours.setVolumeHoraire(coursDTO.getVolumeHoraire());
        cours.setCreditsEcts(coursDTO.getCreditsEcts());
        cours.setDescription(coursDTO.getDescription());
        cours.setMatiere(matiere);

        Cours savedCours = coursRepository.save(cours);
        return mapToResponseDTO(savedCours);
    }

    /**
     * Met à jour un cours existant.
     */
    @Transactional
    public CoursResponseDTO updateCours(String id, CoursDTO coursDTO) {
        Cours existingCours = coursRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours avec ID " + id + " non trouvé"));
        validateCoursDTO(coursDTO);

        existingCours.setNom(coursDTO.getNom());
        existingCours.setVolumeHoraire(coursDTO.getVolumeHoraire());
        existingCours.setCreditsEcts(coursDTO.getCreditsEcts());
        existingCours.setDescription(coursDTO.getDescription());

        if (coursDTO.getMatiereId() != null) {
            Matiere matiere = matiereRepository.findById(coursDTO.getMatiereId())
                    .orElseThrow(() -> new EntityNotFoundException("Matière avec ID " + coursDTO.getMatiereId() + " non trouvée"));
            existingCours.setMatiere(matiere);
        }

        Cours updatedCours = coursRepository.save(existingCours);
        return mapToResponseDTO(updatedCours);
    }

    /**
     * Supprime un cours par son ID.
     */
    @Transactional
    public void deleteCours(String id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours avec ID " + id + " non trouvé"));
        coursRepository.delete(cours);
    }

    /**
     * Récupère les cours par matière.
     */
    public List<CoursResponseDTO> getCoursByMatiere(String matiereId) {
        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new EntityNotFoundException("Matière avec ID " + matiereId + " non trouvée"));
        return coursRepository.findByMatiere(matiere).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Valide les champs du DTO.
     */
    private void validateCoursDTO(CoursDTO coursDTO) {
        if (coursDTO.getNom() == null || coursDTO.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du cours est requis");
        }
        if (coursDTO.getMatiereId() == null) {
            throw new IllegalArgumentException("L'ID de la matière est requis");
        }
    }

    /**
     * Convertit une entité Cours en CoursResponseDTO.
     */
    private CoursResponseDTO mapToResponseDTO(Cours cours) {
        return new CoursResponseDTO(
                cours.getId(),
                cours.getNom(),
                cours.getVolumeHoraire(),
                cours.getCreditsEcts(),
                cours.getDescription(),
                cours.getMatiere().getId(),
                cours.getMatiere().getNom(),
                cours.getCreatedAt(),
                cours.getUpdatedAt(),
                cours.getCreatedBy(),
                cours.getUpdatedBy()
        );
    }
}