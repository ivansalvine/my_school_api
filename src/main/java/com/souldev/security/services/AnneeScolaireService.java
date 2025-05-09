package com.souldev.security.services;

import com.souldev.security.entities.AnneeScolaire;
import com.souldev.security.entities.Classe;
import com.souldev.security.entities.Semestre;
import com.souldev.security.payload.request.AnneScolaireModel;
import com.souldev.security.payload.response.AnneeScolaireResponseDTO;
import com.souldev.security.repositories.AnneeScolaireRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnneeScolaireService {

    private static final Logger logger = LoggerFactory.getLogger(AnneeScolaireService.class);

    private final AnneeScolaireRepository anneeScolaireRepository;

    @Autowired
    public AnneeScolaireService(AnneeScolaireRepository anneeScolaireRepository) {
        this.anneeScolaireRepository = anneeScolaireRepository;
    }

    @Transactional
    public boolean createAnneeScolaire(AnneScolaireModel anneeScolaireModel) {
        logger.info("Tentative de création d'une année scolaire : {}", anneeScolaireModel.getNom());

        // Validation
        validateAnneeScolaireModel(anneeScolaireModel);
        if (anneeScolaireRepository.findByNom(anneeScolaireModel.getNom()).isPresent()) {
            logger.error("Échec : Une année scolaire avec le nom '{}' existe déjà", anneeScolaireModel.getNom());
            throw new IllegalArgumentException("Une année scolaire avec ce nom existe déjà");
        }

        // Création
        AnneeScolaire anneeScolaire = new AnneeScolaire();
        anneeScolaire.setNom(anneeScolaireModel.getNom());
        anneeScolaire.setDescription(anneeScolaireModel.getDescription());
        anneeScolaire.setDateDebut(anneeScolaireModel.getDateDebut());
        anneeScolaire.setDateFin(anneeScolaireModel.getDateFin());

        // Enregistrement
        AnneeScolaire savedAnneeScolaire = anneeScolaireRepository.save(anneeScolaire);
        logger.info("Année scolaire créée avec succès : ID {}, Nom {}", savedAnneeScolaire.getId(), savedAnneeScolaire.getNom());
        return true;
    }

    public List<AnneeScolaireResponseDTO> getAllAnneeScolaires() {
        logger.info("Récupération de toutes les années scolaires");
        List<AnneeScolaire> annees = anneeScolaireRepository.findAll();
        logger.info("Nombre d'années scolaires récupérées : {}", annees.size());
        return annees.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public AnneeScolaireResponseDTO getAnneeScolaireById(String id) {
        logger.info("Recherche de l'année scolaire avec l'ID : {}", id);
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Année scolaire non trouvée avec l'ID : {}", id);
                    return new RuntimeException("Année scolaire non trouvée avec l'ID : " + id);
                });
        logger.info("Année scolaire trouvée : {}", anneeScolaire.getNom());
        return mapToResponseDTO(anneeScolaire);
    }

    public AnneeScolaireResponseDTO getAnneeScolaireByNom(String nom) {
        logger.info("Recherche de l'année scolaire avec le nom : {}", nom);
        Optional<AnneeScolaire> anneeScolaire = anneeScolaireRepository.findByNom(nom);
        if (anneeScolaire.isPresent()) {
            logger.info("Année scolaire trouvée : ID {}", anneeScolaire.get().getId());
        } else {
            logger.warn("Aucune année scolaire trouvée avec le nom : {}", nom);
            return null;
        }
        return mapToResponseDTO(anneeScolaire.get());
    }

    @Transactional
    public AnneeScolaire updateAnneeScolaire(String id, AnneScolaireModel updatedAnneeScolaire) {
        logger.info("Tentative de mise à jour de l'année scolaire avec l'ID : {}", id);

        // Récupération
        AnneeScolaire existingAnneeScolaire = anneeScolaireRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Année scolaire non trouvée avec l'ID : {}", id);
                    return new RuntimeException("Année scolaire non trouvée avec l'ID : " + id);
                });

        // Validation
        validateAnneeScolaireModel(updatedAnneeScolaire);
        if (!updatedAnneeScolaire.getNom().equals(existingAnneeScolaire.getNom()) &&
                anneeScolaireRepository.findByNom(updatedAnneeScolaire.getNom()).isPresent()) {
            logger.error("Échec : Une autre année scolaire existe avec le nom '{}'", updatedAnneeScolaire.getNom());
            throw new IllegalArgumentException("Une autre année scolaire existe avec ce nom");
        }

        // Mise à jour
        existingAnneeScolaire.setNom(updatedAnneeScolaire.getNom());
        existingAnneeScolaire.setDescription(updatedAnneeScolaire.getDescription());
        existingAnneeScolaire.setDateDebut(updatedAnneeScolaire.getDateDebut());
        existingAnneeScolaire.setDateFin(updatedAnneeScolaire.getDateFin());

        // Enregistrement
        AnneeScolaire savedAnneeScolaire = anneeScolaireRepository.save(existingAnneeScolaire);
        logger.info("Année scolaire mise à jour avec succès : ID {}, Nom {}", savedAnneeScolaire.getId(), savedAnneeScolaire.getNom());
        return savedAnneeScolaire;
    }

    @Transactional
    public boolean deleteAnneeScolaire(String id) {
        logger.info("Tentative de suppression de l'année scolaire avec l'ID : {}", id);

        // Récupération
        Optional<AnneeScolaire> optionalAnneeScolaire = anneeScolaireRepository.findById(id);
        if(optionalAnneeScolaire.isEmpty()){
            logger.error("Année scolaire non trouvée avec l'ID : {}", id);
            return false;
        }

        if (!optionalAnneeScolaire.get().getInscriptions().isEmpty()) {
            logger.error("Échec : L'année scolaire '{}' a {} inscriptions associées", optionalAnneeScolaire.get().getNom(), optionalAnneeScolaire.get().getInscriptions().size());
            return false;
        }

        // Suppression
        anneeScolaireRepository.delete(optionalAnneeScolaire.get());
        logger.info("Année scolaire supprimée avec succès : ID {}", id);
        return true;
    }

    private void validateAnneeScolaireModel(AnneScolaireModel model) {
        // Validation du nom
        if (model == null || model.getNom() == null || model.getNom().trim().isEmpty()) {
            logger.error("Échec : Le nom de l'année scolaire est requis");
            throw new IllegalArgumentException("Le nom de l'année scolaire est requis");
        }
        if (!model.getNom().matches("\\d{4}-\\d{4}")) {
            logger.error("Échec : Le nom '{}' ne respecte pas le format 'YYYY-YYYY'", model.getNom());
            throw new IllegalArgumentException("Le nom doit être au format 'YYYY-YYYY', ex. '2024-2025'");
        }
        // Validation des années
        String[] years = model.getNom().split("-");
        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);
        if (endYear != startYear + 1) {
            logger.error("Échec : Les années '{}' ne sont pas consécutives", model.getNom());
            throw new IllegalArgumentException("Les années doivent être consécutives, ex. '2024-2025'");
        }
        // Validation des dates
        if (model.getDateDebut() != null && model.getDateFin() != null) {
            if (model.getDateDebut().isAfter(model.getDateFin())) {
                logger.error("Échec : La date de début '{}' est postérieure à la date de fin '{}'", model.getDateDebut(), model.getDateFin());
                throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
            }
            if (model.getDateDebut().getYear() != startYear || model.getDateFin().getYear() != endYear) {
                logger.error("Échec : Les dates '{}' et '{}' ne correspondent pas aux années '{}'", model.getDateDebut(), model.getDateFin(), model.getNom());
                throw new IllegalArgumentException("Les dates doivent correspondre aux années indiquées dans le nom");
            }
        }
        logger.debug("Validation réussie pour l'année scolaire : {}", model.getNom());
    }


    private AnneeScolaireResponseDTO mapToResponseDTO(AnneeScolaire anneeScolaire) {
        return new AnneeScolaireResponseDTO(
                anneeScolaire.getId(),
                anneeScolaire.getNom(),
                anneeScolaire.getDateDebut(),
                anneeScolaire.getDateFin(),
                anneeScolaire.getDescription(),
                anneeScolaire.getSemestres() != null ? anneeScolaire.getSemestres().size() : 0,
                anneeScolaire.getClasses() != null ? anneeScolaire.getClasses().size() : 0,
                anneeScolaire.getInscriptions() != null ? anneeScolaire.getInscriptions().size() : 0,
                anneeScolaire.getSemestres() != null ?
                        anneeScolaire.getSemestres().stream().map(Semestre::getId).collect(Collectors.toList()) : List.of(),
                anneeScolaire.getClasses() != null ?
                        anneeScolaire.getClasses().stream().map(Classe::getId).collect(Collectors.toList()) : List.of(),
                anneeScolaire.getCreatedAt(),
                anneeScolaire.getUpdatedAt(),
                anneeScolaire.getCreatedBy(),
                anneeScolaire.getUpdatedBy()
        );
    }
}