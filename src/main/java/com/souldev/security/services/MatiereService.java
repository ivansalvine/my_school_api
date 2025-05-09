package com.souldev.security.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.souldev.security.entities.Classe;
import com.souldev.security.entities.Filiere;
import com.souldev.security.entities.Matiere;
import com.souldev.security.payload.request.MatiereDTO;
import com.souldev.security.payload.request.MatiereResponseDTO;
import com.souldev.security.repositories.ClasseRepository;
import com.souldev.security.repositories.FiliereRepository;
import com.souldev.security.repositories.MatiereRepository;
import com.souldev.security.repositories.ProfesseurRepository;
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
public class MatiereService {

    private static final Logger logger = LoggerFactory.getLogger(MatiereService.class);

    private final MatiereRepository matiereRepository;
    private final ProfesseurRepository professeurRepository;
    private final FiliereRepository filiereRepository;
    private final ClasseRepository classeRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public MatiereService(MatiereRepository matiereRepository,
                          ProfesseurRepository professeurRepository,
                          FiliereRepository filiereRepository,
                          ClasseRepository classeRepository, ObjectMapper objectMapper) {
        this.matiereRepository = matiereRepository;
        this.professeurRepository = professeurRepository;
        this.filiereRepository = filiereRepository;
        this.classeRepository = classeRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public MatiereResponseDTO createMatiere(MatiereDTO matiereDTO) throws JsonProcessingException {
        logger.info("Tentative de création d’une matière : {}", matiereDTO.getNom());

        // Validation de l'unicité du nom
        if (matiereRepository.findByNom(matiereDTO.getNom()).isPresent()) {
            logger.error("Échec : La matière {} existe déjà", matiereDTO.getNom());
            throw new IllegalArgumentException("La matière existe déjà");
        }

        String filiereIdsJson = matiereDTO.getFiliereIds().toString();
        List<String> validFiliereIds = List.of(filiereIdsJson.split(";"));

        System.err.println("validFiliereIds "+validFiliereIds);

        // Création de l'entité Matiere
        Matiere matiere = new Matiere();
        matiere.setNom(matiereDTO.getNom());
        matiere.setDescription(matiereDTO.getDescription());
        matiere.setCoefficient(matiereDTO.getCoefficient());

        logger.info("Filieres reçues : {}", matiereDTO.getFiliereIds());

        // Gestion des filières
        if (matiereDTO.getFiliereIds() != null && !matiereDTO.getFiliereIds().isEmpty()) {
            // Filtrer les identifiants valides


            if (!validFiliereIds.isEmpty()) {
                List<Filiere> filieres = filiereRepository.findAllById(validFiliereIds);
                logger.info("Filières trouvées : {}", filieres.stream().map(Filiere::getId).collect(Collectors.toList()));
                if (filieres.size() != validFiliereIds.size()) {
                    logger.error("Échec : Une ou plusieurs filières non trouvées");
                    throw new EntityNotFoundException("Une ou plusieurs filières non trouvées");
                }
                matiere.setFilieres(filieres);
                filieres.forEach(filiere -> filiere.getMatieres().add(matiere));
            } else {
                logger.warn("Aucun ID de filière valide fourni");
            }
        }

        // Enregistrement
        Matiere savedMatiere = matiereRepository.save(matiere);
        logger.info("Matière créée avec succès : ID {}", savedMatiere.getId());
        return toMatiereDTO(savedMatiere);
    }

    public Optional<MatiereResponseDTO> getMatiereById(String id) {
        logger.info("Recherche de la matière avec l’ID : {}", id);
        Optional<Matiere> matiere = matiereRepository.findById(id);
        if (matiere.isPresent()) {
            logger.info("Matière trouvée : {}", matiere.get().getNom());
        } else {
            logger.warn("Aucune matière trouvée avec l’ID : {}", id);
        }
        return matiere.map(this::toMatiereDTO);
    }

    public List<MatiereResponseDTO> getAllMatieres() {
        logger.info("Récupération de toutes les matières");
        List<Matiere> matieres = matiereRepository.findAll();
        logger.info("Nombre de matières récupérées : {}", matieres.size());
        return matieres.stream().map(this::toMatiereDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatiereResponseDTO> getMatieresByClasse(String classeId) {
        logger.info("Récupération des matières pour la classe ID : {}", classeId);

        // Récupération de la classe
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l'ID : {}", classeId);
                    return new EntityNotFoundException("Classe non trouvée avec l'ID : " + classeId);
                });

        // Récupération de la filière associée à la classe
        Filiere filiere = classe.getFiliere();
        if (filiere == null) {
            logger.error("Aucune filière associée à la classe ID : {}", classeId);
            throw new EntityNotFoundException("Aucune filière associée à la classe ID : " + classeId);
        }

        // Récupération des matières associées à la filière
        List<Matiere> matieres = matiereRepository.findByFilieresId(filiere.getId());
        logger.info("Nombre de matières trouvées pour la classe ID {} : {}", classeId, matieres.size());
        return matieres.stream().map(this::toMatiereDTO).collect(Collectors.toList());
    }

    public List<MatiereResponseDTO> getMatieresByFiliere(String filiereId) {
        logger.info("Récupération des matières pour la filière ID : {}", filiereId);
        List<Matiere> matieres = matiereRepository.findByFilieresId(filiereId);
        logger.info("Nombre de matières trouvées pour la filière : {}", matieres.size());
        return matieres.stream().map(this::toMatiereDTO).collect(Collectors.toList());
    }

    @Transactional
    public MatiereResponseDTO updateMatiere(String matiereId, MatiereDTO matiereDTO) {
        logger.info("Tentative de mise à jour de la matière avec ID : {}", matiereId);

        // Récupération de la matière existante
        Matiere existingMatiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> {
                    logger.error("Matière non trouvée avec l’ID : {}", matiereId);
                    return new EntityNotFoundException("Matière non trouvée avec l’ID : " + matiereId);
                });

        // Validation de l'unicité du nom
        if (!matiereDTO.getNom().equals(existingMatiere.getNom()) &&
                matiereRepository.findByNom(matiereDTO.getNom()).isPresent()) {
            logger.error("Échec : La matière {} existe déjà", matiereDTO.getNom());
            throw new IllegalArgumentException("La matière existe déjà");
        }

        String filiereIdsJson = matiereDTO.getFiliereIds();
        List<String> validFiliereIds = List.of(filiereIdsJson.split(";"));

        // Mise à jour des champs
        existingMatiere.setNom(matiereDTO.getNom());
        existingMatiere.setDescription(matiereDTO.getDescription());
        existingMatiere.setCoefficient(matiereDTO.getCoefficient());

        // Gestion des filières
        existingMatiere.getFilieres().clear();
        if (matiereDTO.getFiliereIds() != null && !matiereDTO.getFiliereIds().isEmpty()) {
            List<Filiere> filieres = filiereRepository.findAllById(validFiliereIds);
            if (filieres.size() != validFiliereIds.size()) {
                logger.error("Échec : Une ou plusieurs filières non trouvées");
                throw new EntityNotFoundException("Une ou plusieurs filières non trouvées");
            }
            existingMatiere.setFilieres(filieres);
            filieres.forEach(filiere -> filiere.getMatieres().add(existingMatiere));
        }

        // Enregistrement
        Matiere updatedMatiere = matiereRepository.save(existingMatiere);
        logger.info("Matière mise à jour avec succès : ID {}", updatedMatiere.getId());
        return toMatiereDTO(updatedMatiere);
    }

    @Transactional
    public MatiereResponseDTO addFiliereToMatiere(String matiereId, String filiereId) {
        logger.info("Ajout de la filière ID {} à la matière ID {}", filiereId, matiereId);

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> {
                    logger.error("Matière non trouvée avec l’ID : {}", matiereId);
                    return new EntityNotFoundException("Matière non trouvée avec l’ID : " + matiereId);
                });

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l’ID : {}", filiereId);
                    return new EntityNotFoundException("Filière non trouvée avec l’ID : " + filiereId);
                });

        if (!matiere.getFilieres().contains(filiere)) {
            matiere.getFilieres().add(filiere);
            filiere.getMatieres().add(matiere);
            matiereRepository.save(matiere);
            logger.info("Filière {} ajoutée à la matière {}", filiereId, matiereId);
        } else {
            logger.warn("La filière {} est déjà associée à la matière {}", filiereId, matiereId);
        }

        return toMatiereDTO(matiere);
    }

    @Transactional
    public MatiereResponseDTO removeFiliereFromMatiere(String matiereId, String filiereId) {
        logger.info("Suppression de la filière ID {} de la matière ID {}", filiereId, matiereId);

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> {
                    logger.error("Matière non trouvée avec l’ID : {}", matiereId);
                    return new EntityNotFoundException("Matière non trouvée avec l’ID : " + matiereId);
                });

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> {
                    logger.error("Filière non trouvée avec l’ID : {}", filiereId);
                    return new EntityNotFoundException("Filière non trouvée avec l’ID : " + filiereId);
                });

        if (matiere.getFilieres().contains(filiere)) {
            matiere.getFilieres().remove(filiere);
            filiere.getMatieres().remove(matiere);
            matiereRepository.save(matiere);
            logger.info("Filière {} supprimée de la matière {}", filiereId, matiereId);
        } else {
            logger.warn("La filière {} n’est pas associée à la matière {}", filiereId, matiereId);
        }

        return toMatiereDTO(matiere);
    }

    // Conversion de Matiere en MatiereDTO
    private MatiereResponseDTO toMatiereDTO(Matiere matiere) {
        List<String> filiereIds = matiere.getFilieres().stream()
                .map(Filiere::getId)
                .collect(Collectors.toList());
        return new MatiereResponseDTO(
                matiere.getId(),
                matiere.getNom(),
                matiere.getDescription(),
                matiere.getCoefficient(),
                filiereIds,
                matiere.getCreatedAt(),
                matiere.getUpdatedAt(),
                matiere.getCreatedBy(),
                matiere.getUpdatedBy()
        );
    }
}
