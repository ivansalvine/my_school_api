package com.souldev.security.services;

import com.souldev.security.entities.*;
import com.souldev.security.payload.request.CreneauRequestDTO;
import com.souldev.security.payload.response.CreneauResponseDTO;
import com.souldev.security.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreneauService {

    private static final Logger logger = LoggerFactory.getLogger(CreneauService.class);
    private final CreneauRepository creneauRepository;
    private final EmploiDuTempsRepository emploiDuTempsRepository;
    private final SalleRepository salleRepository;
    private final CoursRepository coursRepository;
    private final DevoirRepository devoirRepository;

    @Autowired
    public CreneauService(CreneauRepository creneauRepository, EmploiDuTempsRepository emploiDuTempsRepository,
                          SalleRepository salleRepository, CoursRepository coursRepository,
                          DevoirRepository devoirRepository) {
        this.creneauRepository = creneauRepository;
        this.emploiDuTempsRepository = emploiDuTempsRepository;
        this.salleRepository = salleRepository;
        this.coursRepository = coursRepository;
        this.devoirRepository = devoirRepository;
    }

    private RecurrenceRule convertToRecurrenceRule(CreneauRequestDTO.RecurrenceRuleDTO recurrenceDTO) {
        if (recurrenceDTO == null) {
            return null;
        }
        if (recurrenceDTO.getFrequence() == null) {
            logger.error("La fréquence de récurrence est requise");
            throw new IllegalArgumentException("La fréquence de récurrence est requise");
        }

        RecurrenceRule recurrence = new RecurrenceRule();
        recurrence.setFrequence(recurrenceDTO.getFrequence());
        recurrence.setIntervalle(recurrenceDTO.getIntervalle());
        recurrence.setJours(recurrenceDTO.getJours());
        recurrence.setDateFin(recurrenceDTO.getDateFin());
        recurrence.setNombreOccurrences(recurrenceDTO.getNombreOccurrences());

        if (recurrenceDTO.getJours() != null && !recurrenceDTO.getJours().isEmpty()) {
            List<String> validDays = Arrays.asList("LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE");
            if (!validDays.containsAll(recurrenceDTO.getJours())) {
                logger.error("Les jours spécifiés ne sont pas valides : {}", recurrenceDTO.getJours());
                throw new IllegalArgumentException("Les jours spécifiés ne sont pas valides");
            }
        }

        return recurrence;
    }

    private CreneauResponseDTO.RecurrenceRuleDTO convertToRecurrenceRuleDTO(RecurrenceRule recurrence) {
        if (recurrence == null) {
            return null;
        }

        return new CreneauResponseDTO.RecurrenceRuleDTO(
                recurrence.getId(),
                recurrence.getFrequence(),
                recurrence.getIntervalle(),
                recurrence.getJours(),
                recurrence.getDateFin(),
                recurrence.getNombreOccurrences()
        );
    }

    public CreneauResponseDTO createCreneau(CreneauRequestDTO request) {
        logger.info("Création d'un créneau : {} - {}", request.getDateDebut(), request.getDateFin());

        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(request.getEmploiDuTempsId())
                .orElseThrow(() -> {
                    logger.error("Emploi du temps non trouvé avec l'ID : {}", request.getEmploiDuTempsId());
                    return new EntityNotFoundException("Emploi du temps non trouvé avec l'ID : " + request.getEmploiDuTempsId());
                });
        Salle salle = salleRepository.findById(request.getSalleId())
                .orElseThrow(() -> {
                    logger.error("Salle non trouvée avec l'ID : {}", request.getSalleId());
                    return new EntityNotFoundException("Salle non trouvée avec l'ID : " + request.getSalleId());
                });

        Cours cours = null;
        Devoir devoir = null;
        if (request.getCoursId() != null) {
            cours = coursRepository.findById(request.getCoursId())
                    .orElseThrow(() -> {
                        logger.error("Cours non trouvé avec l'ID : {}", request.getCoursId());
                        return new EntityNotFoundException("Cours non trouvé avec l'ID : " + request.getCoursId());
                    });
        } else if (request.getDevoirId() != null) {
            devoir = devoirRepository.findById(request.getDevoirId())
                    .orElseThrow(() -> {
                        logger.error("Devoir non trouvé avec l'ID : {}", request.getDevoirId());
                        return new EntityNotFoundException("Devoir non trouvé avec l'ID : " + request.getDevoirId());
                    });
            if (devoir.getDateDevoir() != null && !request.getDateDebut().toLocalDate().equals(devoir.getDateDevoir())) {
                logger.error("Échec : La date du créneau ({}) ne correspond pas à la date du devoir ({})",
                        request.getDateDebut().toLocalDate(), devoir.getDateDevoir());
                throw new IllegalArgumentException("La date du créneau doit correspondre à la date du devoir");
            }
            if (devoir.getHeureDebut() != null && !request.getDateDebut().toLocalTime().equals(devoir.getHeureDebut())) {
                logger.error("Échec : L’heure de début du créneau ({}) ne correspond pas à l’heure de début du devoir ({})",
                        request.getDateDebut().toLocalTime(), devoir.getHeureDebut());
                throw new IllegalArgumentException("L’heure de début du créneau doit correspondre à l’heure de début du devoir");
            }
            if (devoir.getHeureFin() != null && !request.getDateFin().toLocalTime().equals(devoir.getHeureFin())) {
                logger.error("Échec : L’heure de fin du créneau ({}) ne correspond pas à l’heure de fin du devoir ({})",
                        request.getDateFin().toLocalTime(), devoir.getHeureFin());
                throw new IllegalArgumentException("L’heure de fin du créneau doit correspondre à l’heure de fin du devoir");
            }
        }

        if (request.getDateDebut() == null || request.getDateFin() == null) {
            logger.error("Les dates de début et de fin sont requises");
            throw new IllegalArgumentException("Les dates de début et de fin sont requises");
        }
        if (!request.getDateDebut().isBefore(request.getDateFin())) {
            logger.error("La date de début doit être antérieure à la date de fin");
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        RecurrenceRule recurrence = convertToRecurrenceRule(request.getRecurrenceRule());
        if (devoir != null && recurrence != null) {
            logger.error("Échec : Les devoirs ne peuvent pas avoir de récurrence");
            throw new IllegalArgumentException("Les devoirs ne peuvent pas avoir de récurrence");
        }

        Creneau creneau = new Creneau();
        creneau.setDateDebut(request.getDateDebut());
        creneau.setDateFin(request.getDateFin());
        creneau.setRecurrence(recurrence);
        creneau.setEmploiDuTemps(emploiDuTemps);
        creneau.setSalle(salle);
        creneau.setCours(cours);
        creneau.setDevoir(devoir);

        emploiDuTemps.getCreneaux().add(creneau);
        if (cours != null) {
            cours.getCreneaux().add(creneau);
        }

        Creneau savedCreneau = creneauRepository.save(creneau);
        logger.info("Créneau créé avec succès : ID {}", savedCreneau.getId());

        CreneauResponseDTO.RecurrenceRuleDTO recurrenceRuleDTO = convertToRecurrenceRuleDTO(savedCreneau.getRecurrence());

        return new CreneauResponseDTO(
                savedCreneau.getId(),
                savedCreneau.getDateDebut(),
                savedCreneau.getDateFin(),
                recurrenceRuleDTO,
                savedCreneau.getEmploiDuTemps().getId(),
                savedCreneau.getSalle().getId(),
                savedCreneau.getCours() != null ? savedCreneau.getCours().getId() : null,
                savedCreneau.getDevoir() != null ? savedCreneau.getDevoir().getId() : null
        );
    }

    public Creneau ajouterCreneau(Creneau creneau) {
        logger.info("Ajout d'un créneau : {} - {}", creneau.getDateDebut(), creneau.getDateFin());

        if (creneau.getEmploiDuTemps() == null || creneau.getEmploiDuTemps().getId() == null) {
            logger.error("L'emploi du temps est requis pour le créneau");
            throw new IllegalArgumentException("L'emploi du temps est requis pour le créneau");
        }
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(creneau.getEmploiDuTemps().getId())
                .orElseThrow(() -> {
                    logger.error("Emploi du temps non trouvé avec l'ID : {}", creneau.getEmploiDuTemps().getId());
                    return new EntityNotFoundException("Emploi du temps non trouvé avec l'ID : " + creneau.getEmploiDuTemps().getId());
                });

        if (creneau.getSalle() == null || creneau.getSalle().getId() == null) {
            logger.error("La salle est requise pour le créneau");
            throw new IllegalArgumentException("La salle est requise pour le créneau");
        }
        Salle salle = salleRepository.findById(creneau.getSalle().getId())
                .orElseThrow(() -> {
                    logger.error("Salle non trouvée avec l'ID : {}", creneau.getSalle().getId());
                    return new EntityNotFoundException("Salle non trouvée avec l'ID : " + creneau.getSalle().getId());
                });

        Cours cours = null;
        Devoir devoir = null;
        if (creneau.getCours() != null && creneau.getCours().getId() != null) {
            cours = coursRepository.findById(creneau.getCours().getId())
                    .orElseThrow(() -> {
                        logger.error("Cours non trouvé avec l'ID : {}", creneau.getCours().getId());
                        return new EntityNotFoundException("Cours non trouvé avec l'ID : " + creneau.getCours().getId());
                    });
        } else if (creneau.getDevoir() != null && creneau.getDevoir().getId() != null) {
            devoir = devoirRepository.findById(creneau.getDevoir().getId())
                    .orElseThrow(() -> {
                        logger.error("Devoir non trouvé avec l'ID : {}", creneau.getDevoir().getId());
                        return new EntityNotFoundException("Devoir non trouvé avec l'ID : " + creneau.getDevoir().getId());
                    });
            if (devoir.getDateDevoir() != null && !creneau.getDateDebut().toLocalDate().equals(devoir.getDateDevoir())) {
                logger.error("Échec : La date du créneau ({}) ne correspond pas à la date du devoir ({})",
                        creneau.getDateDebut().toLocalDate(), devoir.getDateDevoir());
                throw new IllegalArgumentException("La date du créneau doit correspondre à la date du devoir");
            }
            if (devoir.getHeureDebut() != null && !creneau.getDateDebut().toLocalTime().equals(devoir.getHeureDebut())) {
                logger.error("Échec : L’heure de début du créneau ({}) ne correspond pas à l’heure de début du devoir ({})",
                        creneau.getDateDebut().toLocalTime(), devoir.getHeureDebut());
                throw new IllegalArgumentException("L’heure de début du créneau doit correspondre à l’heure de début du devoir");
            }
            if (devoir.getHeureFin() != null && !creneau.getDateFin().toLocalTime().equals(devoir.getHeureFin())) {
                logger.error("Échec : L’heure de fin du créneau ({}) ne correspond pas à l’heure de fin du devoir ({})",
                        creneau.getDateFin().toLocalTime(), devoir.getHeureFin());
                throw new IllegalArgumentException("L’heure de fin du créneau doit correspondre à l’heure de fin du devoir");
            }
        }

        if (creneau.getDateDebut() == null || creneau.getDateFin() == null) {
            logger.error("Les dates de début et de fin sont requises");
            throw new IllegalArgumentException("Les dates de début et de fin sont requises");
        }
        if (!creneau.getDateDebut().isBefore(creneau.getDateFin())) {
            logger.error("La date de début doit être antérieure à la date de fin");
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        if (devoir != null && creneau.getRecurrence() != null) {
            logger.error("Échec : Les devoirs ne peuvent pas avoir de récurrence");
            throw new IllegalArgumentException("Les devoirs ne peuvent pas avoir de récurrence");
        }

        creneau.setEmploiDuTemps(emploiDuTemps);
        creneau.setSalle(salle);
        creneau.setCours(cours);
        creneau.setDevoir(devoir);

        emploiDuTemps.getCreneaux().add(creneau);
        if (cours != null) {
            cours.getCreneaux().add(creneau);
        }

        Creneau savedCreneau = creneauRepository.save(creneau);
        logger.info("Créneau ajouté avec succès : ID {}", savedCreneau.getId());
        return savedCreneau;
    }

    public List<CreneauResponseDTO> getAllCreneaux() {
        logger.info("Récupération de tous les créneaux");
        List<Creneau> creneaux = creneauRepository.findAll();
        return creneaux.stream()
                .map(creneau -> {
                    CreneauResponseDTO.RecurrenceRuleDTO recurrenceRuleDTO = convertToRecurrenceRuleDTO(creneau.getRecurrence());
                    return new CreneauResponseDTO(
                            creneau.getId(),
                            creneau.getDateDebut(),
                            creneau.getDateFin(),
                            recurrenceRuleDTO,
                            creneau.getEmploiDuTemps().getId(),
                            creneau.getSalle().getId(),
                            creneau.getCours() != null ? creneau.getCours().getId() : null,
                            creneau.getDevoir() != null ? creneau.getDevoir().getId() : null
                    );
                })
                .collect(Collectors.toList());
    }

    public CreneauResponseDTO getCreneauById(String id) {
        logger.info("Récupération du créneau avec l'ID : {}", id);
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Créneau non trouvé avec l'ID : {}", id);
                    return new EntityNotFoundException("Créneau non trouvé avec l'ID : " + id);
                });
        CreneauResponseDTO.RecurrenceRuleDTO recurrenceRuleDTO = convertToRecurrenceRuleDTO(creneau.getRecurrence());
        return new CreneauResponseDTO(
                creneau.getId(),
                creneau.getDateDebut(),
                creneau.getDateFin(),
                recurrenceRuleDTO,
                creneau.getEmploiDuTemps().getId(),
                creneau.getSalle().getId(),
                creneau.getCours() != null ? creneau.getCours().getId() : null,
                creneau.getDevoir() != null ? creneau.getDevoir().getId() : null
        );
    }

    public CreneauResponseDTO updateCreneau(String id, CreneauRequestDTO request) {
        logger.info("Mise à jour du créneau avec l'ID : {}", id);
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Créneau non trouvé avec l'ID : {}", id);
                    return new EntityNotFoundException("Créneau non trouvé avec l'ID : " + id);
                });
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(request.getEmploiDuTempsId())
                .orElseThrow(() -> {
                    logger.error("Emploi du temps non trouvé avec l'ID : {}", request.getEmploiDuTempsId());
                    return new EntityNotFoundException("Emploi du temps non trouvé avec l'ID : " + request.getEmploiDuTempsId());
                });
        Salle salle = salleRepository.findById(request.getSalleId())
                .orElseThrow(() -> {
                    logger.error("Salle non trouvée avec l'ID : {}", request.getSalleId());
                    return new EntityNotFoundException("Salle non trouvée avec l'ID : " + request.getSalleId());
                });

        Cours cours = null;
        Devoir devoir = null;
        if (request.getCoursId() != null) {
            cours = coursRepository.findById(request.getCoursId())
                    .orElseThrow(() -> {
                        logger.error("Cours non trouvé avec l'ID : {}", request.getCoursId());
                        return new EntityNotFoundException("Cours non trouvé avec l'ID : " + request.getCoursId());
                    });
        } else if (request.getDevoirId() != null) {
            devoir = devoirRepository.findById(request.getDevoirId())
                    .orElseThrow(() -> {
                        logger.error("Devoir non trouvé avec l'ID : {}", request.getDevoirId());
                        return new EntityNotFoundException("Devoir non trouvé avec l'ID : " + request.getDevoirId());
                    });
            if (devoir.getDateDevoir() != null && !request.getDateDebut().toLocalDate().equals(devoir.getDateDevoir())) {
                logger.error("Échec : La date du créneau ({}) ne correspond pas à la date du devoir ({})",
                        request.getDateDebut().toLocalDate(), devoir.getDateDevoir());
                throw new IllegalArgumentException("La date du créneau doit correspondre à la date du devoir");
            }
            if (devoir.getHeureDebut() != null && !request.getDateDebut().toLocalTime().equals(devoir.getHeureDebut())) {
                logger.error("Échec : L’heure de début du créneau ({}) ne correspond pas à l’heure de début du devoir ({})",
                        request.getDateDebut().toLocalTime(), devoir.getHeureDebut());
                throw new IllegalArgumentException("L’heure de début du créneau doit correspondre à l’heure de début du devoir");
            }
            if (devoir.getHeureFin() != null && !request.getDateFin().toLocalTime().equals(devoir.getHeureFin())) {
                logger.error("Échec : L’heure de fin du créneau ({}) ne correspond pas à l’heure de fin du devoir ({})",
                        request.getDateFin().toLocalTime(), devoir.getHeureFin());
                throw new IllegalArgumentException("L’heure de fin du créneau doit correspondre à l’heure de fin du devoir");
            }
        }

        if (request.getDateDebut() == null || request.getDateFin() == null) {
            logger.error("Les dates de début et de fin sont requises");
            throw new IllegalArgumentException("Les dates de début et de fin sont requises");
        }
        if (!request.getDateDebut().isBefore(request.getDateFin())) {
            logger.error("La date de début doit être antérieure à la date de fin");
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        RecurrenceRule recurrence = convertToRecurrenceRule(request.getRecurrenceRule());
        if (devoir != null && recurrence != null) {
            logger.error("Échec : Les devoirs ne peuvent pas avoir de récurrence");
            throw new IllegalArgumentException("Les devoirs ne peuvent pas avoir de récurrence");
        }

        creneau.setDateDebut(request.getDateDebut());
        creneau.setDateFin(request.getDateFin());
        creneau.setRecurrence(recurrence);
        creneau.setEmploiDuTemps(emploiDuTemps);
        creneau.setSalle(salle);
        creneau.setCours(cours);
        creneau.setDevoir(devoir);

        Creneau updatedCreneau = creneauRepository.save(creneau);
        logger.info("Créneau mis à jour avec succès : ID {}", updatedCreneau.getId());

        CreneauResponseDTO.RecurrenceRuleDTO recurrenceRuleDTO = convertToRecurrenceRuleDTO(updatedCreneau.getRecurrence());

        return new CreneauResponseDTO(
                updatedCreneau.getId(),
                updatedCreneau.getDateDebut(),
                updatedCreneau.getDateFin(),
                recurrenceRuleDTO,
                updatedCreneau.getEmploiDuTemps().getId(),
                updatedCreneau.getSalle().getId(),
                updatedCreneau.getCours() != null ? updatedCreneau.getCours().getId() : null,
                updatedCreneau.getDevoir() != null ? updatedCreneau.getDevoir().getId() : null
        );
    }

    public void deleteCreneau(String id) {
        logger.info("Suppression du créneau avec l'ID : {}", id);
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Créneau non trouvé avec l'ID : {}", id);
                    return new EntityNotFoundException("Créneau non trouvé avec l'ID : " + id);
                });
        creneauRepository.delete(creneau);
        logger.info("Créneau supprimé avec succès : ID {}", id);
    }
}