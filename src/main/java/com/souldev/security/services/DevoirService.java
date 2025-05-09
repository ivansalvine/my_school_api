package com.souldev.security.services;

import com.souldev.security.entities.*;
import com.souldev.security.payload.request.DevoirRequestDTO;
import com.souldev.security.payload.response.DevoirResponseDTO;
import com.souldev.security.repositories.*;
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
public class DevoirService {
    private static final Logger logger = LoggerFactory.getLogger(DevoirService.class);

    private final DevoirRepository devoirRepository;
    private final ClasseRepository classeRepository;
    private final SemestreRepository semestreRepository;
    private final MatiereRepository matiereRepository;
    private final ProfesseurRepository professeurRepository;

    @Autowired
    public DevoirService(DevoirRepository devoirRepository,
                         ClasseRepository classeRepository,
                         SemestreRepository semestreRepository,
                         MatiereRepository matiereRepository,
                         ProfesseurRepository professeurRepository) {
        this.devoirRepository = devoirRepository;
        this.classeRepository = classeRepository;
        this.semestreRepository = semestreRepository;
        this.matiereRepository = matiereRepository;
        this.professeurRepository = professeurRepository;
    }

    public DevoirResponseDTO createDevoir(DevoirRequestDTO dto) {
        logger.info("Tentative de création d’un devoir : {}", dto.getNom());

        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l’ID : {}", dto.getClasseId());
                    return new EntityNotFoundException("Classe non trouvée avec l’ID : " + dto.getClasseId());
                });

        Semestre semestre = semestreRepository.findById(dto.getSemestreId())
                .orElseThrow(() -> {
                    logger.error("Semestre non trouvé avec l’ID : {}", dto.getSemestreId());
                    return new EntityNotFoundException("Semestre non trouvé avec l’ID : " + dto.getSemestreId());
                });

        Matiere matiere = matiereRepository.findById(dto.getMatiereId())
                .orElseThrow(() -> {
                    logger.error("Matière non trouvée avec l’ID : {}", dto.getMatiereId());
                    return new EntityNotFoundException("Matière non trouvée avec l’ID : " + dto.getMatiereId());
                });

        Professeur professeur = professeurRepository.findById(dto.getProfesseurId())
                .orElseThrow(() -> {
                    logger.error("Professeur non trouvé avec l’ID : {}", dto.getProfesseurId());
                    return new EntityNotFoundException("Professeur non trouvé avec l’ID : " + dto.getProfesseurId());
                });

        // Validation : classe et semestre doivent appartenir à la même année scolaire
        if (!classe.getAnneeScolaire().getId().equals(semestre.getAnneeScolaire().getId())) {
            logger.error("Échec : La classe et le semestre ne sont pas dans la même année scolaire");
            throw new IllegalArgumentException("La classe et le semestre doivent appartenir à la même année scolaire");
        }

        // Validation : heureDebut doit être avant heureFin si les deux sont spécifiés
        if (dto.getHeureDebut() != null && dto.getHeureFin() != null &&
                !dto.getHeureDebut().isBefore(dto.getHeureFin())) {
            logger.error("Échec : L’heure de début doit être antérieure à l’heure de fin");
            throw new IllegalArgumentException("L’heure de début doit être antérieure à l’heure de fin");
        }

        Devoir devoir = new Devoir(
                dto.getNom(),
                dto.getType(),
                dto.getPonderation(),
                dto.getDateDevoir(),
                dto.getHeureDebut(),
                dto.getHeureFin(),
                classe,
                semestre,
                matiere,
                professeur
        );
        devoir.setDescription(dto.getDescription());

        Devoir savedDevoir = devoirRepository.save(devoir);
        logger.info("Devoir créé avec succès : ID {}", savedDevoir.getId());
        return mapToResponseDTO(savedDevoir);
    }

    public DevoirResponseDTO getDevoir(String id) {
        logger.info("Recherche du devoir avec l’ID : {}", id);
        Devoir devoir = devoirRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Devoir non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Devoir non trouvé avec l’ID : " + id);
                });
        return mapToResponseDTO(devoir);
    }

    public List<DevoirResponseDTO> getAllDevoirs() {
        logger.info("Récupération de tous les devoirs");
        List<Devoir> devoirs = devoirRepository.findAll();
        logger.info("Nombre de devoirs récupérés : {}", devoirs.size());
        return devoirs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public DevoirResponseDTO updateDevoir(String id, DevoirRequestDTO dto) {
        logger.info("Tentative de mise à jour du devoir avec ID : {}", id);

        Devoir existingDevoir = devoirRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Devoir non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Devoir non trouvé avec l’ID : " + id);
                });

        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l’ID : {}", dto.getClasseId());
                    return new EntityNotFoundException("Classe non trouvée avec l’ID : " + dto.getClasseId());
                });

        Semestre semestre = semestreRepository.findById(dto.getSemestreId())
                .orElseThrow(() -> {
                    logger.error("Semestre non trouvé avec l’ID : {}", dto.getSemestreId());
                    return new EntityNotFoundException("Semestre non trouvé avec l’ID : " + dto.getSemestreId());
                });

        Matiere matiere = matiereRepository.findById(dto.getMatiereId())
                .orElseThrow(() -> {
                    logger.error("Matière non trouvée avec l’ID : {}", dto.getMatiereId());
                    return new EntityNotFoundException("Matière non trouvée avec l’ID : " + dto.getMatiereId());
                });

        Professeur professeur = professeurRepository.findById(dto.getProfesseurId())
                .orElseThrow(() -> {
                    logger.error("Professeur non trouvé avec l’ID : {}", dto.getProfesseurId());
                    return new EntityNotFoundException("Professeur non trouvé avec l’ID : " + dto.getProfesseurId());
                });

        // Validation : classe et semestre doivent appartenir à la même année scolaire
        if (!classe.getAnneeScolaire().getId().equals(semestre.getAnneeScolaire().getId())) {
            logger.error("Échec : La classe et le semestre ne sont pas dans la même année scolaire");
            throw new IllegalArgumentException("La classe et le semestre doivent appartenir à la même année scolaire");
        }

        // Validation : heureDebut doit être avant heureFin si les deux sont spécifiés
        if (dto.getHeureDebut() != null && dto.getHeureFin() != null &&
                !dto.getHeureDebut().isBefore(dto.getHeureFin())) {
            logger.error("Échec : L’heure de début doit être antérieure à l’heure de fin");
            throw new IllegalArgumentException("L’heure de début doit être antérieure à l’heure de fin");
        }

        existingDevoir.setNom(dto.getNom());
        existingDevoir.setType(dto.getType());
        existingDevoir.setPonderation(dto.getPonderation());
        existingDevoir.setDateDevoir(dto.getDateDevoir());
        existingDevoir.setHeureDebut(dto.getHeureDebut());
        existingDevoir.setHeureFin(dto.getHeureFin());
        existingDevoir.setDescription(dto.getDescription());
        existingDevoir.setClasse(classe);
        existingDevoir.setSemestre(semestre);
        existingDevoir.setMatiere(matiere);
        existingDevoir.setProfesseur(professeur);

        Devoir updatedDevoir = devoirRepository.save(existingDevoir);
        logger.info("Devoir mis à jour avec succès : ID {}", updatedDevoir.getId());
        return mapToResponseDTO(updatedDevoir);
    }

    public void deleteDevoir(String id) {
        logger.info("Tentative de suppression du devoir avec ID : {}", id);
        Devoir devoir = devoirRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Devoir non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Devoir non trouvé avec l’ID : " + id);
                });

        devoirRepository.delete(devoir);
        logger.info("Devoir supprimé avec succès : ID {}", id);
    }

    private DevoirResponseDTO mapToResponseDTO(Devoir devoir) {
        return new DevoirResponseDTO(
                devoir.getId(),
                devoir.getNom(),
                devoir.getType(),
                devoir.getPonderation(),
                devoir.getDateDevoir(),
                devoir.getHeureDebut(),
                devoir.getHeureFin(),
                devoir.getDescription(),
                devoir.getClasse().getId(),
                devoir.getSemestre().getId(),
                devoir.getMatiere().getId(),
                devoir.getProfesseur().getId(),
                devoir.getCreatedAt(),
                devoir.getUpdatedAt(),
                devoir.getCreatedBy(),
                devoir.getUpdatedBy()
        );
    }
}