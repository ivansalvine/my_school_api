package com.souldev.security.services;

import com.souldev.security.entities.Classe;
import com.souldev.security.entities.Cycle;
import com.souldev.security.entities.Filiere;
import com.souldev.security.entities.Matiere;
import com.souldev.security.payload.request.CycleResponseDTO;
import com.souldev.security.payload.request.FiliereDTO;
import com.souldev.security.payload.request.FiliereResponseDTO;
import com.souldev.security.repositories.ClasseRepository;
import com.souldev.security.repositories.CycleRepository;
import com.souldev.security.repositories.FiliereRepository;
import com.souldev.security.repositories.MatiereRepository;
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
public class FiliereService {

    private static final Logger logger = LoggerFactory.getLogger(FiliereService.class);

    private final FiliereRepository filiereRepository;
    private final CycleRepository cycleRepository;
    private final MatiereRepository matiereRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public FiliereService(FiliereRepository filiereRepository, CycleRepository cycleRepository,
                          MatiereRepository matiereRepository, ClasseRepository classeRepository) {
        this.filiereRepository = filiereRepository;
        this.cycleRepository = cycleRepository;
        this.matiereRepository = matiereRepository;
        this.classeRepository = classeRepository;
    }

    // Créer une nouvelle filière
    @Transactional
    public boolean createFiliere(FiliereDTO filiereDTO) {
        logger.info("Tentative de création de la filière : {}", filiereDTO.getNom());

        // Vérifier si le cycle existe
        Cycle cycle = cycleRepository.findById(filiereDTO.getCycleId())
                .orElseThrow(() -> {
                    logger.error("Cycle non trouvé avec l'ID : {}", filiereDTO.getCycleId());
                    return new EntityNotFoundException("Cycle avec l'ID " + filiereDTO.getCycleId() + " non trouvé");
                });

        // Créer l'entité Filiere
        Filiere filiere = new Filiere();
        filiere.setNom(filiereDTO.getNom());
        filiere.setDescription(filiereDTO.getDescription());
        filiere.setCycle(cycle);

        Filiere savedFiliere = filiereRepository.save(filiere);
        logger.info("Filière créée avec succès : ID {}, Nom {}", savedFiliere.getId(), savedFiliere.getNom());

        return true;
    }

    // Mettre à jour une filière existante
    @Transactional
    public boolean updateFiliere(String id, FiliereDTO filiereDTO) {
        logger.info("Tentative de mise à jour de la filière ID : {}", id);

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", id);
                    return new EntityNotFoundException("Filière avec l'ID " + id + " non trouvée");
                });

        // Mise à jour des champs
        filiere.setNom(filiereDTO.getNom());
        filiere.setDescription(filiereDTO.getDescription());

        // Mettre à jour le cycle
        Cycle cycle = cycleRepository.findById(filiereDTO.getCycleId())
                .orElseThrow(() -> {
                    logger.error("Cycle non trouvé avec l'ID : {}", filiereDTO.getCycleId());
                    return new EntityNotFoundException("Cycle avec l'ID " + filiereDTO.getCycleId() + " non trouvé");
                });
        filiere.setCycle(cycle);

        Filiere updatedFiliere = filiereRepository.save(filiere);
        logger.info("Filière mise à jour avec succès : ID {}, Nom {}", updatedFiliere.getId(), updatedFiliere.getNom());

        return true;
    }

    // Récupérer une filière par ID
    public Optional<FiliereDTO> findById(String id) {
        logger.info("Recherche de la filière avec l'ID : {}", id);
        return filiereRepository.findById(id)
                .map(filiere -> {
                    logger.info("Filière trouvée : {}", filiere.getNom());
                    return new FiliereDTO(
                            filiere.getId(),
                            filiere.getNom(),
                            filiere.getDescription(),
                            filiere.getCycle().getId()
                    );
                });
    }

    public List<FiliereResponseDTO> findAll() {
        logger.info("Récupération de toutes les filières");
        List<Filiere> filieres = filiereRepository.findAll();
        logger.info("Nombre de filières récupérées : {}", filieres.size());
        return filieres.stream()
                .map(filiere -> new FiliereResponseDTO(
                        filiere.getId(),
                        filiere.getNom(),
                        filiere.getDescription(),
                        filiere.getCreatedAt(),
                        filiere.getUpdatedAt(),
                        filiere.getCreatedBy(),
                        filiere.getUpdatedBy(),
                        new CycleResponseDTO(
                                filiere.getCycle().getId(),
                                filiere.getCycle().getNom(),
                                filiere.getCycle().getDureeAnnees(),
                                filiere.getCycle().getDescription(),
                                filiere.getCycle().getEcole().getId()
                        )
                ))
                .collect(Collectors.toList());
    }

    // Supprimer une filière
    @Transactional
    public void deleteFiliere(String id) {
        logger.info("Tentative de suppression de la filière avec l'ID : {}", id);

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", id);
                    return new EntityNotFoundException("Filière avec l'ID " + id + " non trouvée");
                });

        // Vérifier les relations
        if (!filiere.getClasses().isEmpty()) {
            logger.error("Échec : La filière '{}' a {} classes associées", filiere.getNom(), filiere.getClasses().size());
            throw new IllegalStateException(
                    String.format("Impossible de supprimer la filière '%s' car elle a %d classe(s) associée(s)",
                            filiere.getNom(), filiere.getClasses().size())
            );
        }

        // Supprimer les relations avec les matières
        filiere.getMatieres().clear();

        filiereRepository.delete(filiere);
        logger.info("Filière supprimée avec succès : ID {}", id);
    }

    // Ajouter une matière à une filière
    public FiliereDTO addMatiereToFiliere(String filiereId, String matiereId) {
        logger.info("Ajout de la matière ID {} à la filière ID {}", matiereId, filiereId);

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", filiereId);
                    return new EntityNotFoundException("Filière avec l'ID " + filiereId + " non trouvée");
                });

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> {
                    logger.error("Matière non trouvée avec l'ID : {}", matiereId);
                    return new EntityNotFoundException("Matière avec l'ID " + matiereId + " non trouvée");
                });

        filiere.getMatieres().add(matiere);
        Filiere updatedFiliere = filiereRepository.save(filiere);
        logger.info("Matière ajoutée à la filière : {}", updatedFiliere.getNom());

        return new FiliereDTO(
                updatedFiliere.getId(),
                updatedFiliere.getNom(),
                updatedFiliere.getDescription(),
                updatedFiliere.getCycle().getId()
        );
    }

    // Supprimer une matière d'une filière
    public FiliereDTO removeMatiereFromFiliere(String filiereId, String matiereId) {
        logger.info("Suppression de la matière ID {} de la filière ID {}", matiereId, filiereId);

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", filiereId);
                    return new EntityNotFoundException("Filière avec l'ID " + filiereId + " non trouvée");
                });

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> {
                    logger.error("Matière non trouvée avec l'ID : {}", matiereId);
                    return new EntityNotFoundException("Matière avec l'ID " + matiereId + " non trouvée");
                });

        filiere.getMatieres().remove(matiere);
        Filiere updatedFiliere = filiereRepository.save(filiere);
        logger.info("Matière supprimée de la filière : {}", updatedFiliere.getNom());

        return new FiliereDTO(
                updatedFiliere.getId(),
                updatedFiliere.getNom(),
                updatedFiliere.getDescription(),
                updatedFiliere.getCycle().getId()
        );
    }

    // Ajouter une classe à une filière
    public boolean addClasseToFiliere(String filiereId, String classeId) {
        logger.info("Ajout de la classe ID {} à la filière ID {}", classeId, filiereId);

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", filiereId);
                    return new EntityNotFoundException("Filière avec l'ID " + filiereId + " non trouvée");
                });

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l'ID : {}", classeId);
                    return new EntityNotFoundException("Classe avec l'ID " + classeId + " non trouvée");
                });

        filiere.getClasses().add(classe);
        classe.setFiliere(filiere);
        Filiere updatedFiliere = filiereRepository.save(filiere);
        logger.info("Classe ajoutée à la filière : {}", updatedFiliere.getNom());

        return true;
    }

    // Récupérer toutes les matières d'une filière
    public List<Matiere> getMatieresByFiliere(String filiereId) {
        logger.info("Récupération des matières pour la filière ID : {}", filiereId);
        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", filiereId);
                    return new EntityNotFoundException("Filière avec l'ID " + filiereId + " non trouvée");
                });
        return filiere.getMatieres();
    }

    // Récupérer toutes les classes d'une filière
    public List<Classe> getClassesByFiliere(String filiereId) {
        logger.info("Récupération des classes pour la filière ID : {}", filiereId);
        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l'ID : {}", filiereId);
                    return new EntityNotFoundException("Filière avec l'ID " + filiereId + " non trouvée");
                });
        return filiere.getClasses();
    }
}