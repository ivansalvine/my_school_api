package com.souldev.security.services;

import com.souldev.security.entities.Enseignement;
import com.souldev.security.entities.Lecon;
import com.souldev.security.payload.request.LeconRequestDTO;
import com.souldev.security.payload.response.LeconResponseDTO;
import com.souldev.security.repositories.EnseignementRepository;
import com.souldev.security.repositories.LeconRepository;
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
public class LeconService {
    private static final Logger logger = LoggerFactory.getLogger(LeconService.class);

    private final LeconRepository leconRepository;
    private final EnseignementRepository enseignementRepository;

    @Autowired
    public LeconService(LeconRepository leconRepository, EnseignementRepository enseignementRepository) {
        this.leconRepository = leconRepository;
        this.enseignementRepository = enseignementRepository;
    }

    public LeconResponseDTO createLecon(LeconRequestDTO dto) {
        logger.info("Tentative de création d’une leçon : {}", dto.getTitre());

        Enseignement enseignement = enseignementRepository.findById(dto.getEnseignementId())
                .orElseThrow(() -> {
                    logger.error("Enseignement non trouvé avec l’ID : {}", dto.getEnseignementId());
                    return new EntityNotFoundException("Enseignement non trouvé avec l’ID : " + dto.getEnseignementId());
                });

        Lecon lecon = new Lecon(
                dto.getTitre(),
                dto.getDescription(),
                dto.getDatePrevue(),
                dto.getOrdre(),
                enseignement
        );

        Lecon savedLecon = leconRepository.save(lecon);
        logger.info("Leçon créée avec succès : ID {}", savedLecon.getId());
        return mapToResponseDTO(savedLecon);
    }

    public LeconResponseDTO getLecon(String id) {
        logger.info("Recherche de la leçon avec l’ID : {}", id);
        Lecon lecon = leconRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Leçon non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Leçon non trouvée avec l’ID : " + id);
                });
        return mapToResponseDTO(lecon);
    }

    public List<LeconResponseDTO> getAllLecons() {
        logger.info("Récupération de toutes les leçons");
        List<Lecon> lecons = leconRepository.findAll();
        logger.info("Nombre de leçons récupérées : {}", lecons.size());
        return lecons.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LeconResponseDTO> getLeconsByEnseignement(String enseignementId) {
        logger.info("Récupération des leçons pour l’enseignement ID : {}", enseignementId);
        List<Lecon> lecons = leconRepository.findByEnseignementId(enseignementId);
        logger.info("Nombre de leçons récupérées pour l’enseignement : {}", lecons.size());
        return lecons.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public LeconResponseDTO updateLecon(String id, LeconRequestDTO dto) {
        logger.info("Tentative de mise à jour de la leçon avec ID : {}", id);

        Lecon existingLecon = leconRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Leçon non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Leçon non trouvée avec l’ID : " + id);
                });

        Enseignement enseignement = enseignementRepository.findById(dto.getEnseignementId())
                .orElseThrow(() -> {
                    logger.error("Enseignement non trouvé avec l’ID : {}", dto.getEnseignementId());
                    return new EntityNotFoundException("Enseignement non trouvé avec l’ID : " + dto.getEnseignementId());
                });

        existingLecon.setTitre(dto.getTitre());
        existingLecon.setDescription(dto.getDescription());
        existingLecon.setDatePrevue(dto.getDatePrevue());
        existingLecon.setOrdre(dto.getOrdre());
        existingLecon.setEnseignement(enseignement);

        Lecon updatedLecon = leconRepository.save(existingLecon);
        logger.info("Leçon mise à jour avec succès : ID {}", updatedLecon.getId());
        return mapToResponseDTO(updatedLecon);
    }

    public void deleteLecon(String id) {
        logger.info("Tentative de suppression de la leçon avec ID : {}", id);
        Lecon lecon = leconRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Leçon non trouvée avec l’ID : {}", id);
                    return new EntityNotFoundException("Leçon non trouvée avec l’ID : " + id);
                });

        leconRepository.delete(lecon);
        logger.info("Leçon supprimée avec succès : ID {}", id);
    }

    private LeconResponseDTO mapToResponseDTO(Lecon lecon) {
        return new LeconResponseDTO(
                lecon.getId(),
                lecon.getTitre(),
                lecon.getDescription(),
                lecon.getDatePrevue(),
                lecon.getOrdre(),
                lecon.getEnseignement().getId(),
                lecon.getCreatedAt(),
                lecon.getUpdatedAt(),
                lecon.getCreatedBy(),
                lecon.getUpdatedBy()
        );
    }
}