package com.souldev.security.services;

import com.souldev.security.entities.Cycle;
import com.souldev.security.entities.Ecole;
import com.souldev.security.entities.Filiere;
import com.souldev.security.payload.request.CycleDTO;
import com.souldev.security.payload.request.CycleResponseDTO;
import com.souldev.security.repositories.CycleRepository;
import com.souldev.security.repositories.EcoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CycleService {

    private static final Logger logger = LoggerFactory.getLogger(CycleService.class);

    private final CycleRepository cycleRepository;
    private final EcoleRepository ecoleRepository;

    @Autowired
    public CycleService(CycleRepository cycleRepository, EcoleRepository ecoleRepository) {
        this.cycleRepository = cycleRepository;
        this.ecoleRepository = ecoleRepository;
    }

    @Transactional
    public Cycle createCycle(CycleDTO cycleDTO) {
        logger.info("Tentative de création d'un cycle : {}", cycleDTO.getNom());
        System.out.println("Nom "+cycleDTO.getNom());
        System.out.println("duree "+cycleDTO.getDureeAnnees());
        System.out.println("ecole "+cycleDTO.getEcoleId());
        System.out.println("description "+cycleDTO.getDescription());

        // Validation des champs
        if (cycleDTO.getNom() == null || cycleDTO.getNom().trim().isEmpty()) {
            logger.error("Échec : Le nom du cycle est requis");
            throw new IllegalArgumentException("Le nom du cycle est requis");
        }
        if (cycleDTO.getDureeAnnees() < 1) {
            logger.error("Échec : La durée doit être d'au moins 1 an");
            throw new IllegalArgumentException("La durée doit être d'au moins 1 an");
        }

        Optional<Ecole> optionalEcole = ecoleRepository.findById(cycleDTO.getEcoleId());

        if (optionalEcole.isEmpty()) {
            logger.error("Échec : Une école doit être associée au cycle");
            throw new IllegalArgumentException("Une école doit être associée au cycle");
        }


        // Vérification de l'unicité du nom au sein de l'école
        if (cycleRepository.findByNomAndEcoleId(cycleDTO.getNom(), cycleDTO.getEcoleId()).isPresent()) {
            logger.error("Échec : Un cycle avec le nom '{}' existe déjà pour cette école", cycleDTO.getNom());
            throw new IllegalArgumentException("Un cycle avec ce nom existe déjà pour cette école");
        }

        Cycle cycle = new Cycle();
        cycle.setDescription(cycleDTO.getDescription());
        cycle.setEcole(optionalEcole.get());
        cycle.setNom(cycleDTO.getNom());
        cycle.setDureeAnnees(cycleDTO.getDureeAnnees());
        // Enregistrement
        Cycle savedCycle = cycleRepository.save(cycle);
        logger.info("Cycle créé avec succès : ID {}, Nom {}", savedCycle.getId(), savedCycle.getNom());
        return savedCycle;
    }

    public Optional<Cycle> getCycleById(String id) {
        logger.info("Recherche du cycle avec l'ID : {}", id);
        Optional<Cycle> cycle = cycleRepository.findById(id);
        if (cycle.isPresent()) {
            logger.info("Cycle trouvé : {}", cycle.get().getNom());
        } else {
            logger.warn("Aucun cycle trouvé avec l'ID : {}", id);
        }
        return cycle;
    }

    public List<CycleResponseDTO> getAllCycles() {
        logger.info("Récupération de tous les cycles");
        List<Cycle> cycles = cycleRepository.findAll();
        logger.info("Nombre de cycles récupérés : {}", cycles.size());
        return cycles.stream()
                .map(cycle -> new CycleResponseDTO(
                        cycle.getId(),
                        cycle.getNom(),
                        cycle.getDureeAnnees(),
                        cycle.getDescription(),
                        cycle.getEcole().getId(),
                        cycle.getCreatedAt(),
                        cycle.getUpdatedAt(),
                        cycle.getCreatedBy(),
                        cycle.getUpdatedBy()
                ))
                .collect(Collectors.toList());
    }

    public List<Cycle> getCyclesByEcole(String ecoleId) {
        logger.info("Récupération des cycles pour l'école ID : {}", ecoleId);
        List<Cycle> cycles = cycleRepository.findByEcoleId(ecoleId);
        logger.info("Nombre de cycles trouvés : {}", cycles.size());
        return cycles;
    }

    public List<Filiere> getFilieresByCycle(String cycleId) {
        logger.info("Récupération des filières pour le cycle ID : {}", cycleId);
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> {
                    logger.error("Cycle non trouvé avec l'ID : {}", cycleId);
                    return new RuntimeException("Cycle non trouvé avec l'ID : " + cycleId);
                });
        List<Filiere> filieres = cycle.getFilieres();
        logger.info("Nombre de filières trouvées : {}", filieres.size());
        return filieres;
    }

    @Transactional
    public Cycle updateCycle(String cycleId, CycleDTO cycleDTO) {
        logger.info("Tentative de mise à jour du cycle avec l'ID : {}", cycleId);

        // Récupération du cycle existant
        Cycle existingCycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> {
                    logger.error("Cycle non trouvé avec l'ID : {}", cycleId);
                    return new RuntimeException("Cycle non trouvé avec l'ID : " + cycleId);
                });

        // Validation des champs
        if (cycleDTO.getNom() == null || cycleDTO.getNom().trim().isEmpty()) {
            logger.error("Échec : Le nom du cycle est requis");
            throw new IllegalArgumentException("Le nom du cycle est requis");
        }
        if (cycleDTO.getDureeAnnees() < 1) {
            logger.error("Échec : La durée doit être d'au moins 1 an");
            throw new IllegalArgumentException("La durée doit être d'au moins 1 an");
        }

        if (cycleDTO.getEcoleId() == null || cycleDTO.getEcoleId().trim().isEmpty()) {
            logger.error("Échec : L'école du cycle est requise");
            throw new IllegalArgumentException("L'école du cycle est requise");
        }

        Optional<Ecole> optionalEcole = ecoleRepository.findById(cycleDTO.getEcoleId());
        if(optionalEcole.isEmpty()){
            logger.error("Échec : Aucune école trouvée avec l'ID: "+cycleDTO.getEcoleId());
            throw new IllegalArgumentException("Échec : Aucune école trouvée avec l'ID: "+cycleDTO.getEcoleId());
        }

        // Vérification de l'unicité du nom
        if (!cycleDTO.getNom().equals(existingCycle.getNom()) &&
                cycleRepository.findByNomAndEcoleId(cycleDTO.getNom(), existingCycle.getEcole().getId()).isPresent()) {
            logger.error("Échec : Un cycle avec le nom '{}' existe déjà pour cette école", cycleDTO.getNom());
            throw new IllegalArgumentException("Un cycle avec ce nom existe déjà pour cette école");
        }

        // Mise à jour des champs
        existingCycle.setNom(cycleDTO.getNom());
        existingCycle.setDureeAnnees(cycleDTO.getDureeAnnees());
        existingCycle.setDescription(cycleDTO.getDescription());
        existingCycle.setEcole(optionalEcole.get());

        // Enregistrement
        Cycle updatedCycle = cycleRepository.save(existingCycle);
        logger.info("Cycle mis à jour avec succès : ID {}, Nom {}", updatedCycle.getId(), updatedCycle.getNom());
        return updatedCycle;
    }

    @Transactional
    public void deleteCycle(String id) {
        logger.info("Tentative de suppression du cycle avec l'ID : {}", id);

        // Vérifier l'existence du cycle
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cycle non trouvé avec l'ID : {}", id);
                    return new RuntimeException("Cycle non trouvé avec l'ID : " + id);
                });

        // Vérifier les relations avec des requêtes COUNT
        long filieresCount = cycleRepository.countFilieresByCycleId(id);
        if (filieresCount > 0) {
            logger.error("Échec : Le cycle '{}' a {} filières associées", cycle.getNom(), filieresCount);
            throw new IllegalStateException(
                    String.format("Impossible de supprimer le cycle '%s' car il a %d filière(s) associée(s)", cycle.getNom(), filieresCount)
            );
        }

        // Suppression
        cycleRepository.delete(cycle);
        logger.info("Cycle supprimé avec succès : ID {}", id);
    }
}