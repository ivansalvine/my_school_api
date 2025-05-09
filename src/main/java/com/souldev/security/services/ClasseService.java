package com.souldev.security.services;

import com.souldev.security.entities.AnneeScolaire;
import com.souldev.security.entities.Classe;
import com.souldev.security.entities.EmploiDuTemps;
import com.souldev.security.entities.Filiere;
import com.souldev.security.payload.request.ClasseDTO;
import com.souldev.security.repositories.AnneeScolaireRepository;
import com.souldev.security.repositories.ClasseRepository;
import com.souldev.security.repositories.EmploiDuTempsRepository;
import com.souldev.security.repositories.FiliereRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClasseService {

    private final ClasseRepository classeRepository;
    private final FiliereRepository filiereRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;
    private final EmploiDuTempsRepository emploiDuTempsRepository;

    private static final Logger logger = LoggerFactory.getLogger(ClasseService.class);

    @Autowired
    public ClasseService(ClasseRepository classeRepository, FiliereRepository filiereRepository,
                         AnneeScolaireRepository anneeScolaireRepository, EmploiDuTempsRepository emploiDuTempsRepository) {
        this.classeRepository = classeRepository;
        this.filiereRepository = filiereRepository;
        this.anneeScolaireRepository = anneeScolaireRepository;
        this.emploiDuTempsRepository = emploiDuTempsRepository;
    }

    // Créer une classe
    @Transactional
    public ClasseDTO createClasse(ClasseDTO classeDTO) {
        logger.info("Tentative de création d'une classe : {}", classeDTO.getNom());

        // Récupération de la filière
        Filiere filiere = filiereRepository.findById(classeDTO.getFiliereId())
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", classeDTO.getFiliereId());
                    return new EntityNotFoundException("Filière non trouvée avec l'ID : " + classeDTO.getFiliereId());
                });

        // Récupération de l'année scolaire
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(classeDTO.getAnneeScolaireId())
                .orElseThrow(() -> {
                    logger.error("Année scolaire non trouvée avec l'ID : {}", classeDTO.getAnneeScolaireId());
                    return new EntityNotFoundException("Année scolaire non trouvée avec l'ID : " + classeDTO.getAnneeScolaireId());
                });

        // Vérification de l'unicité du nom par filière et année scolaire
        if (classeRepository.existsByNomAndFiliereAndAnneeScolaire(classeDTO.getNom(), filiere, anneeScolaire)) {
            logger.error("Échec : Une classe avec le nom {} existe déjà pour cette filière et année scolaire", classeDTO.getNom());
            throw new IllegalArgumentException("Une classe avec ce nom existe déjà pour cette filière et année scolaire");
        }

        // Création de l'entité Classe
        Classe classe = new Classe();
        classe.setNom(classeDTO.getNom());
        classe.setCapaciteMaximale(classeDTO.getCapaciteMaximale());
        classe.setFiliere(filiere);
        classe.setAnneeScolaire(anneeScolaire);

        // Gestion de l'EmploiDuTemps (optionnel)
        if (classeDTO.getEmploiDuTempsId() != null) {
            EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(classeDTO.getEmploiDuTempsId())
                    .orElseThrow(() -> {
                        logger.error("Emploi du temps non trouvé avec l'ID : {}", classeDTO.getEmploiDuTempsId());
                        return new EntityNotFoundException("Emploi du temps non trouvé avec l'ID : " + classeDTO.getEmploiDuTempsId());
                    });
            classe.setEmploiDuTemps(emploiDuTemps);
        }

        // Enregistrement
        Classe savedClasse = classeRepository.save(classe);
        logger.info("Classe créée avec succès : ID {}", savedClasse.getId());

        // Conversion en DTO pour la réponse
        return toClasseDTO(savedClasse);
    }

    // Récupérer une classe par ID
    public Optional<ClasseDTO> getClasseById(String id) {
        return classeRepository.findById(id)
                .map(this::toClasseDTO);
    }

    // Récupérer toutes les classes
    public List<ClasseDTO> getAllClasses() {
        return classeRepository.findAll()
                .stream()
                .map(this::toClasseDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les classes par année scolaire
    public List<ClasseDTO> getClassesByAnneeScolaire(String anneeScolaireId) {
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(anneeScolaireId)
                .orElseThrow(() -> {
                    logger.error("Année scolaire non trouvée avec l'ID : {}", anneeScolaireId);
                    return new EntityNotFoundException("Année scolaire non trouvée avec l'ID : " + anneeScolaireId);
                });
        return classeRepository.findByAnneeScolaire(anneeScolaire)
                .stream()
                .map(this::toClasseDTO)
                .collect(Collectors.toList());
    }

    // Mettre à jour une classe
    @Transactional
    public ClasseDTO updateClasse(String classeId, ClasseDTO classeDTO) {
        logger.info("Tentative de mise à jour de la classe avec ID : {}", classeId);

        // Récupération de la classe existante
        Classe existingClasse = classeRepository.findById(classeId)
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l'ID : {}", classeId);
                    return new EntityNotFoundException("Classe non trouvée avec l'ID : " + classeId);
                });

        // Récupération de la filière
        Filiere filiere = filiereRepository.findById(classeDTO.getFiliereId())
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", classeDTO.getFiliereId());
                    return new EntityNotFoundException("Filière non trouvée avec l'ID : " + classeDTO.getFiliereId());
                });

        // Récupération de l'année scolaire
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(classeDTO.getAnneeScolaireId())
                .orElseThrow(() -> {
                    logger.error("Année scolaire non trouvée avec l'ID : {}", classeDTO.getAnneeScolaireId());
                    return new EntityNotFoundException("Année scolaire non trouvée avec l'ID : " + classeDTO.getAnneeScolaireId());
                });

        // Vérification de l'unicité (sauf pour la classe actuelle)
        if (!classeDTO.getNom().equals(existingClasse.getNom()) ||
                !classeDTO.getFiliereId().equals(existingClasse.getFiliere().getId()) ||
                !classeDTO.getAnneeScolaireId().equals(existingClasse.getAnneeScolaire().getId())) {
            if (classeRepository.existsByNomAndFiliereAndAnneeScolaire(classeDTO.getNom(), filiere, anneeScolaire)) {
                logger.error("Échec : Une classe avec le nom {} existe déjà pour cette filière et année scolaire", classeDTO.getNom());
                throw new IllegalArgumentException("Une classe avec ce nom existe déjà pour cette filière et année scolaire");
            }
        }

        // Mise à jour des champs
        existingClasse.setNom(classeDTO.getNom());
        existingClasse.setCapaciteMaximale(classeDTO.getCapaciteMaximale());
        existingClasse.setFiliere(filiere);
        existingClasse.setAnneeScolaire(anneeScolaire);

        // Gestion de l'EmploiDuTemps (optionnel)
        if (classeDTO.getEmploiDuTempsId() != null) {
            EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(classeDTO.getEmploiDuTempsId())
                    .orElseThrow(() -> {
                        logger.error("Emploi du temps non trouvé avec l'ID : {}", classeDTO.getEmploiDuTempsId());
                        return new EntityNotFoundException("Emploi du temps non trouvé avec l'ID : " + classeDTO.getEmploiDuTempsId());
                    });
            existingClasse.setEmploiDuTemps(emploiDuTemps);
        } else {
            existingClasse.setEmploiDuTemps(null); // Permet de dissocier un EmploiDuTemps existant
        }

        // Enregistrement
        Classe updatedClasse = classeRepository.save(existingClasse);
        logger.info("Classe mise à jour avec succès : ID {}", updatedClasse.getId());

        // Conversion en DTO pour la réponse
        return toClasseDTO(updatedClasse);
    }

    // Supprimer une classe
    @Transactional
    public void deleteClasse(String id) {
        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l'ID : {}", id);
                    return new EntityNotFoundException("Classe non trouvée avec l'ID : " + id);
                });

        classeRepository.delete(classe);
        logger.info("Classe supprimée avec succès : ID {}", id);
    }

    // Conversion de Classe en ClasseDTO
    private ClasseDTO toClasseDTO(Classe classe) {
        return new ClasseDTO(
                classe.getId(),
                classe.getNom(),
                classe.getCapaciteMaximale(),
                classe.getFiliere() != null ? classe.getFiliere().getId() : null,
                classe.getAnneeScolaire() != null ? classe.getAnneeScolaire().getId() : null,
                classe.getEmploiDuTemps() != null ? classe.getEmploiDuTemps().getId() : null,
                classe.getCreatedAt(),
                classe.getUpdatedAt(),
                classe.getCreatedBy(),
                classe.getUpdatedBy()
        );
    }
}