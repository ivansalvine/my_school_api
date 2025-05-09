package com.souldev.security.services;

import com.souldev.security.entities.Classe;
import com.souldev.security.entities.Cours;
import com.souldev.security.entities.Enseignement;
import com.souldev.security.entities.Professeur;
import com.souldev.security.payload.request.EnseignementRequestDTO;
import com.souldev.security.payload.response.EnseignementResponseDTO;
import com.souldev.security.repositories.ClasseRepository;
import com.souldev.security.repositories.CoursRepository;
import com.souldev.security.repositories.EnseignementRepository;
import com.souldev.security.repositories.ProfesseurRepository;
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
public class EnseignementService {
    private static final Logger logger = LoggerFactory.getLogger(EnseignementService.class);

    private final EnseignementRepository enseignementRepository;
    private final CoursRepository coursRepository;
    private final ClasseRepository classeRepository;
    private final ProfesseurRepository professeurRepository;

    @Autowired
    public EnseignementService(EnseignementRepository enseignementRepository,
                               CoursRepository coursRepository,
                               ClasseRepository classeRepository,
                               ProfesseurRepository professeurRepository) {
        this.enseignementRepository = enseignementRepository;
        this.coursRepository = coursRepository;
        this.classeRepository = classeRepository;
        this.professeurRepository = professeurRepository;
    }

    public EnseignementResponseDTO createEnseignement(EnseignementRequestDTO dto) {
        logger.info("Tentative de création d’un enseignement : Cours {}, Classe {}, Professeur {}",
                dto.getCoursId(), dto.getClasseId(), dto.getProfesseurId());

        Cours cours = coursRepository.findById(dto.getCoursId())
                .orElseThrow(() -> {
                    logger.error("Cours non trouvé avec l’ID : {}", dto.getCoursId());
                    return new EntityNotFoundException("Cours non trouvé avec l’ID : " + dto.getCoursId());
                });

        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l’ID : {}", dto.getClasseId());
                    return new EntityNotFoundException("Classe non trouvée avec l’ID : " + dto.getClasseId());
                });

        Professeur professeur = professeurRepository.findById(dto.getProfesseurId())
                .orElseThrow(() -> {
                    logger.error("Professeur non trouvé avec l’ID : {}", dto.getProfesseurId());
                    return new EntityNotFoundException("Professeur non trouvé avec l’ID : " + dto.getProfesseurId());
                });

        // Vérification de l'unicité (la contrainte est gérée par la base, mais vérifions ici pour un message clair)
        if (enseignementRepository.existsByCoursAndClasseAndProfesseur(cours, classe, professeur)) {
            logger.error("Un enseignement existe déjà pour ce cours, cette classe et ce professeur");
            throw new IllegalArgumentException("Un enseignement existe déjà pour ce cours, cette classe et ce professeur");
        }

        Enseignement enseignement = new Enseignement(cours, classe, professeur);
        Enseignement savedEnseignement = enseignementRepository.save(enseignement);
        logger.info("Enseignement créé avec succès : ID {}", savedEnseignement.getId());
        return mapToResponseDTO(savedEnseignement);
    }

    public EnseignementResponseDTO getEnseignement(String id) {
        logger.info("Recherche de l’enseignement avec l’ID : {}", id);
        Enseignement enseignement = enseignementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Enseignement non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Enseignement non trouvé avec l’ID : " + id);
                });
        return mapToResponseDTO(enseignement);
    }

    public List<EnseignementResponseDTO> getAllEnseignements() {
        logger.info("Récupération de tous les enseignements");
        List<Enseignement> enseignements = enseignementRepository.findAll();
        logger.info("Nombre d’enseignements récupérés : {}", enseignements.size());
        return enseignements.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public EnseignementResponseDTO updateEnseignement(String id, EnseignementRequestDTO dto) {
        logger.info("Tentative de mise à jour de l’enseignement avec ID : {}", id);

        Enseignement existingEnseignement = enseignementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Enseignement non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Enseignement non trouvé avec l’ID : " + id);
                });

        Cours cours = coursRepository.findById(dto.getCoursId())
                .orElseThrow(() -> {
                    logger.error("Cours non trouvé avec l’ID : {}", dto.getCoursId());
                    return new EntityNotFoundException("Cours non trouvé avec l’ID : " + dto.getCoursId());
                });

        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l’ID : {}", dto.getClasseId());
                    return new EntityNotFoundException("Classe non trouvée avec l’ID : " + dto.getClasseId());
                });

        Professeur professeur = professeurRepository.findById(dto.getProfesseurId())
                .orElseThrow(() -> {
                    logger.error("Professeur non trouvé avec l’ID : {}", dto.getProfesseurId());
                    return new EntityNotFoundException("Professeur non trouvé avec l’ID : " + dto.getProfesseurId());
                });

        // Vérification de l'unicité (ignorer l'enseignement actuel)
        Enseignement duplicate = enseignementRepository.findByCoursAndClasseAndProfesseur(cours, classe, professeur);
        if (duplicate != null && !duplicate.getId().equals(id)) {
            logger.error("Un enseignement existe déjà pour ce cours, cette classe et ce professeur");
            throw new IllegalArgumentException("Un enseignement existe déjà pour ce cours, cette classe et ce professeur");
        }

        existingEnseignement.setCours(cours);
        existingEnseignement.setClasse(classe);
        existingEnseignement.setProfesseur(professeur);

        Enseignement updatedEnseignement = enseignementRepository.save(existingEnseignement);
        logger.info("Enseignement mis à jour avec succès : ID {}", updatedEnseignement.getId());
        return mapToResponseDTO(updatedEnseignement);
    }

    public void deleteEnseignement(String id) {
        logger.info("Tentative de suppression de l’enseignement avec ID : {}", id);
        Enseignement enseignement = enseignementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Enseignement non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Enseignement non trouvé avec l’ID : " + id);
                });

        enseignementRepository.delete(enseignement);
        logger.info("Enseignement supprimé avec succès : ID {}", id);
    }

    private EnseignementResponseDTO mapToResponseDTO(Enseignement enseignement) {
        return new EnseignementResponseDTO(
                enseignement.getId(),
                enseignement.getCours().getId(),
                enseignement.getClasse().getId(),
                enseignement.getProfesseur().getId(),
                enseignement.getCreatedAt(),
                enseignement.getUpdatedAt(),
                enseignement.getCreatedBy(),
                enseignement.getUpdatedBy()
        );
    }
}