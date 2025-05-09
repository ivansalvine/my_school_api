package com.souldev.security.services;

import com.souldev.security.entities.Ecole;
import com.souldev.security.entities.Salle;
import com.souldev.security.payload.request.SalleRequestDTO;
import com.souldev.security.payload.response.SalleResponseDTO;
import com.souldev.security.repositories.EcoleRepository;
import com.souldev.security.repositories.SalleRepository;
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
public class SalleService {

    private static final Logger logger = LoggerFactory.getLogger(SalleService.class);
    private final SalleRepository salleRepository;
    private final EcoleRepository ecoleRepository;

    @Autowired
    public SalleService(SalleRepository salleRepository, EcoleRepository ecoleRepository) {
        this.salleRepository = salleRepository;
        this.ecoleRepository = ecoleRepository;
    }

    public SalleResponseDTO createSalle(SalleRequestDTO request) {
        logger.info("Création d'une salle : {}", request.getNom());

        // Vérifier l'existence de l'école
        Ecole ecole = ecoleRepository.findById(request.getEcoleId())
                .orElseThrow(() -> new EntityNotFoundException("École non trouvée avec l'ID : " + request.getEcoleId()));

        // Créer la salle
        Salle salle = new Salle();
        salle.setNom(request.getNom());
        salle.setCapacite(request.getCapacite());
        salle.setDescription(request.getDescription());
        salle.setEcole(ecole);

        Salle savedSalle = salleRepository.save(salle);
        return new SalleResponseDTO(
                savedSalle.getId(),
                savedSalle.getNom(),
                savedSalle.getCapacite(),
                savedSalle.getDescription(),
                savedSalle.getEcole().getId()
        );
    }

    public List<SalleResponseDTO> getAllSalles() {
        logger.info("Récupération de toutes les salles");
        List<Salle> salles = salleRepository.findAll();
        return salles.stream()
                .map(salle -> new SalleResponseDTO(
                        salle.getId(),
                        salle.getNom(),
                        salle.getCapacite(),
                        salle.getDescription(),
                        salle.getEcole().getId(),
                        salle.getCreatedAt(),
                        salle.getUpdatedAt(),
                        salle.getCreatedBy(),
                        salle.getUpdatedBy()
                ))
                .collect(Collectors.toList());
    }

    public SalleResponseDTO getSalleById(String id) {
        logger.info("Récupération de la salle avec l'ID : {}", id);
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'ID : " + id));
        return new SalleResponseDTO(
                salle.getId(),
                salle.getNom(),
                salle.getCapacite(),
                salle.getDescription(),
                salle.getEcole().getId()
        );
    }

    public SalleResponseDTO updateSalle(String id, SalleRequestDTO request) {
        logger.info("Mise à jour de la salle avec l'ID : {}", id);
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'ID : " + id));
        Ecole ecole = ecoleRepository.findById(request.getEcoleId())
                .orElseThrow(() -> new EntityNotFoundException("École non trouvée avec l'ID : " + request.getEcoleId()));

        // Mettre à jour les champs
        salle.setNom(request.getNom());
        salle.setCapacite(request.getCapacite());
        salle.setDescription(request.getDescription());
        salle.setEcole(ecole);

        Salle updatedSalle = salleRepository.save(salle);
        return new SalleResponseDTO(
                updatedSalle.getId(),
                updatedSalle.getNom(),
                updatedSalle.getCapacite(),
                updatedSalle.getDescription(),
                updatedSalle.getEcole().getId()
        );
    }

    public void deleteSalle(String id) {
        logger.info("Suppression de la salle avec l'ID : {}", id);
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'ID : " + id));
        salleRepository.delete(salle);
    }
}
