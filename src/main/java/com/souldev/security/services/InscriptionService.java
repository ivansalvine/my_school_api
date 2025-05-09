package com.souldev.security.services;

import com.souldev.security.entities.*;
import com.souldev.security.payload.request.InscriptionRequestDTO;
import com.souldev.security.payload.response.InscriptionResponseDTO;
import com.souldev.security.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InscriptionService {
    private static final Logger logger = LoggerFactory.getLogger(InscriptionService.class);

    private final InscriptionRepository inscriptionRepository;
    private final EleveRepository eleveRepository;
    private final FiliereRepository filiereRepository;
    private final ClasseRepository classeRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;

    @Autowired
    public InscriptionService(InscriptionRepository inscriptionRepository, EleveRepository eleveRepository,
                              FiliereRepository filiereRepository, ClasseRepository classeRepository,
                              AnneeScolaireRepository anneeScolaireRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.eleveRepository = eleveRepository;
        this.filiereRepository = filiereRepository;
        this.classeRepository = classeRepository;
        this.anneeScolaireRepository = anneeScolaireRepository;
    }

    public InscriptionResponseDTO createInscription(InscriptionRequestDTO dto) {
        logger.info("Tentative de création d’une inscription pour l’élève ID : {}", dto.getEleveId());

        // Vérifier l'unicité
        if (inscriptionRepository.existsByEleveIdAndAnneeScolaireId(dto.getEleveId(), dto.getAnneeScolaireId())) {
            logger.error("Échec : L’élève est déjà inscrit pour cette année scolaire");
            throw new IllegalArgumentException("L’élève est déjà inscrit pour cette année scolaire");
        }

        // Récupérer les entités liées
        Eleve eleve = eleveRepository.findById(dto.getEleveId())
                .orElseThrow(() -> new EntityNotFoundException("Élève non trouvé avec l’ID : " + dto.getEleveId()));
        Filiere filiere = filiereRepository.findById(dto.getFiliereId())
                .orElseThrow(() -> new EntityNotFoundException("Filière non trouvée avec l’ID : " + dto.getFiliereId()));
        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> new EntityNotFoundException("Classe non trouvée avec l’ID : " + dto.getClasseId()));
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(dto.getAnneeScolaireId())
                .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée avec l’ID : " + dto.getAnneeScolaireId()));

        // Vérifier la cohérence
        if (!classe.getFiliere().getId().equals(filiere.getId()) ||
                !classe.getAnneeScolaire().getId().equals(anneeScolaire.getId())) {
            logger.error("Échec : La classe n’est pas cohérente avec la filière ou l’année scolaire");
            throw new IllegalArgumentException("La classe doit appartenir à la filière et à l’année scolaire spécifiées");
        }

        // Créer l’inscription
        Inscription inscription = new Inscription(
                LocalDate.now(),
                Inscription.StatutInscription.valueOf(dto.getStatut()),
                eleve,
                filiere,
                classe,
                anneeScolaire
        );

        Inscription savedInscription = inscriptionRepository.save(inscription);
        logger.info("Inscription créée avec succès : ID {}", savedInscription.getId());
        return mapToResponseDTO(savedInscription);
    }

    public InscriptionResponseDTO getInscription(String id) {
        logger.info("Recherche de l’inscription avec l’ID : {}", id);
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Inscription non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Inscription non trouvée avec l’ID : " + id);
                });
        return mapToResponseDTO(inscription);
    }

    public List<InscriptionResponseDTO> getAllInscriptions() {
        logger.info("Récupération de toutes les inscriptions");
        List<Inscription> inscriptions = inscriptionRepository.findAll();
        logger.info("Nombre d’inscriptions récupérées : {}", inscriptions.size());
        return inscriptions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public InscriptionResponseDTO updateInscription(String id, InscriptionRequestDTO dto) {
        logger.info("Tentative de mise à jour de l’inscription avec ID : {}", id);

        // Récupérer l’inscription existante
        Inscription existingInscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Inscription non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Inscription non trouvée avec l’ID : " + id);
                });

        // Vérifier l'unicité (si l’année scolaire change)
        if (!existingInscription.getAnneeScolaire().getId().equals(dto.getAnneeScolaireId()) &&
                inscriptionRepository.existsByEleveIdAndAnneeScolaireId(dto.getEleveId(), dto.getAnneeScolaireId())) {
            logger.error("Échec : L’élève est déjà inscrit pour cette année scolaire");
            throw new IllegalArgumentException("L’élève est déjà inscrit pour cette année scolaire");
        }

        // Récupérer les entités liées
        Eleve eleve = eleveRepository.findById(dto.getEleveId())
                .orElseThrow(() -> new EntityNotFoundException("Élève non trouvé avec l’ID : " + dto.getEleveId()));
        Filiere filiere = filiereRepository.findById(dto.getFiliereId())
                .orElseThrow(() -> new EntityNotFoundException("Filière non trouvée avec l’ID : " + dto.getFiliereId()));
        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> new EntityNotFoundException("Classe non trouvée avec l’ID : " + dto.getClasseId()));
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(dto.getAnneeScolaireId())
                .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée avec l’ID : " + dto.getAnneeScolaireId()));

        // Vérifier la cohérence
        if (!classe.getFiliere().getId().equals(filiere.getId()) ||
                !classe.getAnneeScolaire().getId().equals(anneeScolaire.getId())) {
            logger.error("Échec : La classe n’est pas cohérente avec la filière ou l’année scolaire");
            throw new IllegalArgumentException("La classe doit appartenir à la filière et à l’année scolaire spécifiées");
        }

        // Mettre à jour les champs
        existingInscription.setDateInscription(LocalDate.now());
        existingInscription.setStatut(Inscription.StatutInscription.valueOf(dto.getStatut()));
        existingInscription.setEleve(eleve);
        existingInscription.setFiliere(filiere);
        existingInscription.setClasse(classe);
        existingInscription.setAnneeScolaire(anneeScolaire);

        Inscription updatedInscription = inscriptionRepository.save(existingInscription);
        logger.info("Inscription mise à jour avec succès : ID {}", updatedInscription.getId());
        return mapToResponseDTO(updatedInscription);
    }

    public void deleteInscription(String id) {
        logger.info("Tentative de suppression de l’inscription ID : {}", id);
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Inscription non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Inscription non trouvée avec l’ID : " + id);
                });

        inscriptionRepository.delete(inscription);
        logger.info("Inscription supprimée avec succès : ID {}", id);
    }

    private InscriptionResponseDTO mapToResponseDTO(Inscription inscription) {
        return new InscriptionResponseDTO(
                inscription.getId(),
                inscription.getDateInscription(),
                inscription.getStatut().name(),
                inscription.getEleve().getId(),
                inscription.getFiliere().getId(),
                inscription.getClasse().getId(),
                inscription.getAnneeScolaire().getId(),
                inscription.getCreatedAt(),
                inscription.getUpdatedAt(),
                inscription.getCreatedBy(),
                inscription.getUpdatedBy()
        );
    }
}