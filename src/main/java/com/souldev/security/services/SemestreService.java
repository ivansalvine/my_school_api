package com.souldev.security.services;

import com.souldev.security.entities.AnneeScolaire;
import com.souldev.security.entities.Semestre;
import com.souldev.security.payload.request.SemestreRequestDTO;
import com.souldev.security.payload.request.SemestreResponseDTO;
import com.souldev.security.repositories.AnneeScolaireRepository;
import com.souldev.security.repositories.SemestreRepository;
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
public class SemestreService {

    private static final Logger logger = LoggerFactory.getLogger(SemestreService.class);
    private final SemestreRepository semestreRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;

    @Autowired
    public SemestreService(SemestreRepository semestreRepository, AnneeScolaireRepository anneeScolaireRepository) {
        this.semestreRepository = semestreRepository;
        this.anneeScolaireRepository = anneeScolaireRepository;
    }

    public SemestreResponseDTO createSemestre(SemestreRequestDTO request) {
        logger.info("Création du semestre : {}", request.getNom());
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(request.getAnneeScolaireId())
                .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée avec l'ID : " + request.getAnneeScolaireId()));

        Semestre semestre = new Semestre();
        semestre.setNom(request.getNom());
        semestre.setDateDebut(request.getDateDebut());
        semestre.setDateFin(request.getDateFin());
        semestre.setAnneeScolaire(anneeScolaire);

        Semestre savedSemestre = semestreRepository.save(semestre);
        return new SemestreResponseDTO(
                savedSemestre.getId(),
                savedSemestre.getNom(),
                savedSemestre.getAnneeScolaire().getId(),
                savedSemestre.getDateDebut(),
                semestre.getDateFin()
        );
    }

    public List<SemestreResponseDTO> getAllSemestres() {
        logger.info("Récupération de tous les semestres");
        List<Semestre> semestres = semestreRepository.findAll();
        logger.info("Nombre de semestres récupérés : {}", semestres.size());
        return semestres.stream()
                .map(semestre -> new SemestreResponseDTO(
                        semestre.getId(),
                        semestre.getNom(),
                        semestre.getAnneeScolaire().getId(),
                        semestre.getDateDebut(),
                        semestre.getDateFin(),
                        semestre.getCreatedAt(),
                        semestre.getUpdatedAt(),
                        semestre.getCreatedBy(),
                        semestre.getUpdatedBy()
                ))
                .collect(Collectors.toList());
    }

    public SemestreResponseDTO getSemestreById(String id) {
        logger.info("Récupération du semestre avec l'ID : {}", id);
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Semestre non trouvé avec l'ID : " + id));
        return new SemestreResponseDTO(
                semestre.getId(),
                semestre.getNom(),
                semestre.getAnneeScolaire().getId(),
                semestre.getDateDebut(),
                semestre.getDateFin(),
                semestre.getCreatedAt(),
                semestre.getUpdatedAt(),
                semestre.getCreatedBy(),
                semestre.getUpdatedBy()
        );
    }

    public SemestreResponseDTO updateSemestre(String id, SemestreRequestDTO request) {
        logger.info("Mise à jour du semestre avec l'ID : {}", id);
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Semestre non trouvé avec l'ID : " + id));
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(request.getAnneeScolaireId())
                .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée avec l'ID : " + request.getAnneeScolaireId()));

        semestre.setNom(request.getNom());
        semestre.setDateFin(request.getDateFin());
        semestre.setDateDebut(request.getDateDebut());
        semestre.setAnneeScolaire(anneeScolaire);

        Semestre updatedSemestre = semestreRepository.save(semestre);
        return new SemestreResponseDTO(
                updatedSemestre.getId(),
                updatedSemestre.getNom(),
                updatedSemestre.getAnneeScolaire().getId(),
                updatedSemestre.getDateDebut(),
                updatedSemestre.getDateFin()
        );
    }

    public void deleteSemestre(String id) {
        logger.info("Suppression du semestre avec l'ID : {}", id);
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Semestre non trouvé avec l'ID : " + id));
        semestreRepository.delete(semestre);
        logger.info("Semestre supprimé avec succès : {}", id);
    }
}