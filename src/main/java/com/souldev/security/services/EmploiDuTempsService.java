package com.souldev.security.services;

import com.souldev.security.entities.*;
import com.souldev.security.payload.request.CreneauRequestDTO;
import com.souldev.security.payload.request.EmploiDuTempsRequestDTO;
import com.souldev.security.payload.response.CreneauResponseDTO;
import com.souldev.security.payload.response.EmploiDuTempsResponseDTO;
import com.souldev.security.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmploiDuTempsService {
    private static final Logger logger = LoggerFactory.getLogger(EmploiDuTempsService.class);

    private final EmploiDuTempsRepository emploiDuTempsRepository;
    private final ClasseRepository classeRepository;
    private final SemestreRepository semestreRepository;
    private final CreneauRepository creneauRepository;
    private final RecurrenceRuleRepository recurrenceRuleRepository;
    private final DevoirRepository devoirRepository;

    @Autowired
    public EmploiDuTempsService(EmploiDuTempsRepository emploiDuTempsRepository,
                                ClasseRepository classeRepository,
                                SemestreRepository semestreRepository,
                                CreneauRepository creneauRepository,
                                RecurrenceRuleRepository recurrenceRuleRepository,
                                DevoirRepository devoirRepository) {
        this.emploiDuTempsRepository = emploiDuTempsRepository;
        this.classeRepository = classeRepository;
        this.semestreRepository = semestreRepository;
        this.creneauRepository = creneauRepository;
        this.recurrenceRuleRepository = recurrenceRuleRepository;
        this.devoirRepository = devoirRepository;
    }

    public EmploiDuTempsResponseDTO createEmploiDuTemps(EmploiDuTempsRequestDTO dto) {
        logger.info("Tentative de création d’un emploi du temps pour la classe ID : {}", dto.getClasseId());

        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> new EntityNotFoundException("Classe non trouvée avec l’ID : " + dto.getClasseId()));
        Semestre semestre = semestreRepository.findById(dto.getSemestreId())
                .orElseThrow(() -> new EntityNotFoundException("Semestre non trouvé avec l’ID : " + dto.getSemestreId()));

        if (!classe.getAnneeScolaire().getId().equals(semestre.getAnneeScolaire().getId())) {
            logger.error("Échec : La classe et le semestre ne sont pas dans la même année scolaire");
            throw new IllegalArgumentException("La classe et le semestre doivent appartenir à la même année scolaire");
        }

        EmploiDuTemps emploiDuTemps = new EmploiDuTemps(
                "Emploi du temps " + classe.getNom(),
                semestre,
                classe
        );

        EmploiDuTemps savedEmploiDuTemps = emploiDuTempsRepository.save(emploiDuTemps);
        logger.info("Emploi du temps créé avec succès : ID {}", savedEmploiDuTemps.getId());
        return mapToResponseDTO(savedEmploiDuTemps);
    }

    public EmploiDuTempsResponseDTO getEmploiDuTemps(String id) {
        logger.info("Recherche de l’emploi du temps avec l’ID : {}", id);
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Emploi du temps non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Emploi du temps non trouvé avec l’ID : " + id);
                });
        return mapToResponseDTO(emploiDuTemps);
    }

    public List<EmploiDuTempsResponseDTO> getAllEmploisDuTemps() {
        logger.info("Récupération de tous les emplois du temps");
        List<EmploiDuTemps> emploisDuTemps = emploiDuTempsRepository.findAll();
        logger.info("Nombre d’emplois du temps récupérés : {}", emploisDuTemps.size());
        return emploisDuTemps.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public EmploiDuTempsResponseDTO updateEmploiDuTemps(String id, EmploiDuTempsRequestDTO dto) {
        logger.info("Tentative de mise à jour de l’emploi du temps avec ID : {}", id);

        EmploiDuTemps existingEmploiDuTemps = emploiDuTempsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Emploi du temps non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Emploi du temps non trouvé avec l’ID : " + id);
                });

        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> new EntityNotFoundException("Classe non trouvée avec l’ID : " + dto.getClasseId()));
        Semestre semestre = semestreRepository.findById(dto.getSemestreId())
                .orElseThrow(() -> new EntityNotFoundException("Semestre non trouvé avec l’ID : " + dto.getSemestreId()));

        if (!classe.getAnneeScolaire().getId().equals(semestre.getAnneeScolaire().getId())) {
            logger.error("Échec : La classe et le semestre ne sont pas dans la même année scolaire");
            throw new IllegalArgumentException("La classe et le semestre doivent appartenir à la même année scolaire");
        }

        existingEmploiDuTemps.setNom("Emploi du temps " + classe.getNom());
        existingEmploiDuTemps.setSemestre(semestre);
        existingEmploiDuTemps.setClasse(classe);

        EmploiDuTemps updatedEmploiDuTemps = emploiDuTempsRepository.save(existingEmploiDuTemps);
        logger.info("Emploi du temps mis à jour avec succès : ID {}", updatedEmploiDuTemps.getId());
        return mapToResponseDTO(updatedEmploiDuTemps);
    }

    @Transactional
    public EmploiDuTempsResponseDTO addCreneau(String emploiDuTempsId, CreneauRequestDTO creneauDTO) {
        logger.info("Ajout d’un créneau à l’emploi du temps ID : {}", emploiDuTempsId);

        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(creneauDTO.getEmploiDuTempsId())
                .orElseThrow(() -> new EntityNotFoundException("Emploi du temps non trouvé avec l’ID : " + creneauDTO.getEmploiDuTempsId()));

        if (!emploiDuTempsId.equals(creneauDTO.getEmploiDuTempsId())) {
            logger.error("Échec : L’ID de l’emploi du temps dans l’URL ne correspond pas à celui du DTO");
            throw new IllegalArgumentException("L’ID de l’emploi du temps dans l’URL doit correspondre à celui du DTO");
        }

        Salle salle = creneauRepository.findSalleById(creneauDTO.getSalleId())
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l’ID : " + creneauDTO.getSalleId()));

        Cours cours = null;
        Devoir devoir = null;
        if (creneauDTO.getCoursId() != null) {
            cours = creneauRepository.findCoursById(creneauDTO.getCoursId())
                    .orElseThrow(() -> new EntityNotFoundException("Cours non trouvé avec l’ID : " + creneauDTO.getCoursId()));
        } else if (creneauDTO.getDevoirId() != null) {
            devoir = devoirRepository.findById(creneauDTO.getDevoirId())
                    .orElseThrow(() -> new EntityNotFoundException("Devoir non trouvé avec l’ID : " + creneauDTO.getDevoirId()));
            // Valider la date et les heures du créneau
            if (devoir.getDateDevoir() != null && !creneauDTO.getDateDebut().toLocalDate().equals(devoir.getDateDevoir())) {
                logger.error("Échec : La date du créneau ({}) ne correspond pas à la date du devoir ({})",
                        creneauDTO.getDateDebut().toLocalDate(), devoir.getDateDevoir());
                throw new IllegalArgumentException("La date du créneau doit correspondre à la date du devoir");
            }
            if (devoir.getHeureDebut() != null && !creneauDTO.getDateDebut().toLocalTime().equals(devoir.getHeureDebut())) {
                logger.error("Échec : L’heure de début du créneau ({}) ne correspond pas à l’heure de début du devoir ({})",
                        creneauDTO.getDateDebut().toLocalTime(), devoir.getHeureDebut());
                throw new IllegalArgumentException("L’heure de début du créneau doit correspondre à l’heure de début du devoir");
            }
            if (devoir.getHeureFin() != null && !creneauDTO.getDateFin().toLocalTime().equals(devoir.getHeureFin())) {
                logger.error("Échec : L’heure de fin du créneau ({}) ne correspond pas à l’heure de fin du devoir ({})",
                        creneauDTO.getDateFin().toLocalTime(), devoir.getHeureFin());
                throw new IllegalArgumentException("L’heure de fin du créneau doit correspondre à l’heure de fin du devoir");
            }
        }

        Semestre semestre = emploiDuTemps.getSemestre();
        if (semestre.getDateDebut() != null && semestre.getDateFin() != null) {
            if (creneauDTO.getDateDebut().toLocalDate().isBefore(semestre.getDateDebut()) ||
                    creneauDTO.getDateDebut().toLocalDate().isAfter(semestre.getDateFin()) ||
                    creneauDTO.getDateFin().toLocalDate().isBefore(semestre.getDateDebut()) ||
                    creneauDTO.getDateFin().toLocalDate().isAfter(semestre.getDateFin())) {
                logger.error("Échec : Les dates du créneau ne sont pas dans les bornes du semestre");
                throw new IllegalArgumentException("Les dates du créneau doivent être dans les bornes du semestre");
            }
        }

        RecurrenceRule recurrence = null;
        if (creneauDTO.getRecurrenceRule() != null) {
            CreneauRequestDTO.RecurrenceRuleDTO ruleDTO = creneauDTO.getRecurrenceRule();
            recurrence = new RecurrenceRule(
                    ruleDTO.getFrequence(),
                    ruleDTO.getIntervalle() != null ? ruleDTO.getIntervalle() : 1,
                    ruleDTO.getJours(),
                    ruleDTO.getDateFin(),
                    ruleDTO.getNombreOccurrences()
            );

            if (recurrence.getDateFin() != null && semestre.getDateFin() != null &&
                    recurrence.getDateFin().isAfter(semestre.getDateFin())) {
                logger.error("Échec : La date de fin de la récurrence dépasse la fin du semestre");
                throw new IllegalArgumentException("La date de fin de la récurrence ne doit pas dépasser la fin du semestre");
            }

            // Les devoirs ne peuvent pas être récurrents
            if (devoir != null) {
                logger.error("Échec : Les devoirs ne peuvent pas avoir de récurrence");
                throw new IllegalArgumentException("Les devoirs ne peuvent pas avoir de récurrence");
            }

            recurrence = recurrenceRuleRepository.save(recurrence);
        }

        List<Creneau> creneaux = generateCreneaux(creneauDTO, emploiDuTemps, salle, cours, devoir, recurrence);

        for (Creneau creneau : creneaux) {
            emploiDuTemps.addCreneau(creneau);
            creneauRepository.save(creneau);
            logger.debug("Créneau persisté : id={}, start={}, end={}, coursId={}, devoirId={}",
                    creneau.getId(), creneau.getDateDebut(), creneau.getDateFin(),
                    creneau.getCours() != null ? creneau.getCours().getId() : null,
                    creneau.getDevoir() != null ? creneau.getDevoir().getId() : null);
        }

        EmploiDuTemps updatedEmploiDuTemps = emploiDuTempsRepository.save(emploiDuTemps);
        logger.info("Créneaux ajoutés à l’emploi du temps ID : {}", emploiDuTempsId);
        return mapToResponseDTO(updatedEmploiDuTemps);
    }

    public void deleteEmploiDuTemps(String id) {
        logger.info("Tentative de suppression de l’emploi du temps ID : {}", id);
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Emploi du temps non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Emploi du temps non trouvé avec l’ID : " + id);
                });

        emploiDuTempsRepository.delete(emploiDuTemps);
        logger.info("Emploi du temps supprimé avec succès : ID {}", id);
    }

    private List<Creneau> generateCreneaux(CreneauRequestDTO creneauDTO, EmploiDuTemps emploiDuTemps,
                                           Salle salle, Cours cours, Devoir devoir, RecurrenceRule recurrence) {
        List<Creneau> creneaux = new ArrayList<>();
        LocalDateTime baseStart = creneauDTO.getDateDebut();
        LocalDateTime baseEnd = creneauDTO.getDateFin();
        LocalDate endDate = recurrence != null && recurrence.getDateFin() != null
                ? recurrence.getDateFin()
                : emploiDuTemps.getSemestre().getDateFin();
        int maxOccurrences = recurrence != null && recurrence.getNombreOccurrences() != null
                ? recurrence.getNombreOccurrences()
                : Integer.MAX_VALUE;

        logger.debug("Génération des créneaux : baseStart={}, baseEnd={}, endDate={}, maxOccurrences={}, coursId={}, devoirId={}",
                baseStart, baseEnd, endDate, maxOccurrences,
                cours != null ? cours.getId() : null,
                devoir != null ? devoir.getId() : null);

        if (recurrence == null) {
            if (devoir != null) {
                // Valider la date et les heures du créneau
                if (devoir.getDateDevoir() != null && !baseStart.toLocalDate().equals(devoir.getDateDevoir())) {
                    logger.error("Échec : La date du créneau ({}) ne correspond pas à la date du devoir ({})",
                            baseStart.toLocalDate(), devoir.getDateDevoir());
                    throw new IllegalArgumentException("La date du créneau doit correspondre à la date du devoir");
                }
                if (devoir.getHeureDebut() != null && !baseStart.toLocalTime().equals(devoir.getHeureDebut())) {
                    logger.error("Échec : L’heure de début du créneau ({}) ne correspond pas à l’heure de début du devoir ({})",
                            baseStart.toLocalTime(), devoir.getHeureDebut());
                    throw new IllegalArgumentException("L’heure de début du créneau doit correspondre à l’heure de début du devoir");
                }
                if (devoir.getHeureFin() != null && !baseEnd.toLocalTime().equals(devoir.getHeureFin())) {
                    logger.error("Échec : L’heure de fin du créneau ({}) ne correspond pas à l’heure de fin du devoir ({})",
                            baseEnd.toLocalTime(), devoir.getHeureFin());
                    throw new IllegalArgumentException("L’heure de fin du créneau doit correspondre à l’heure de fin du devoir");
                }
            }
            Creneau creneau = new Creneau(baseStart, baseEnd, null, emploiDuTemps, salle, cours, devoir);
            creneaux.add(creneau);
            logger.debug("Créneau unique ajouté : {}", creneau);
            return creneaux;
        }

        // Les devoirs ne peuvent pas être récurrents
        if (devoir != null) {
            logger.error("Échec : Les devoirs ne peuvent pas avoir de récurrence");
            throw new IllegalArgumentException("Les devoirs ne peuvent pas avoir de récurrence");
        }

        int occurrenceCount = 0;
        LocalDate currentDate = baseStart.toLocalDate();

        while (currentDate.isBefore(endDate.plusDays(1)) && occurrenceCount < maxOccurrences) {
            if (isValidDate(currentDate, recurrence)) {
                LocalDateTime start = currentDate.atTime(baseStart.toLocalTime());
                LocalDateTime end = currentDate.atTime(baseEnd.toLocalTime());
                Creneau creneau = new Creneau(start, end, recurrence, emploiDuTemps, salle, cours, devoir);
                creneaux.add(creneau);
                occurrenceCount++;
                logger.debug("Créneau ajouté : start={}, end={}, coursId={}, devoirId={}",
                        start, end,
                        cours != null ? cours.getId() : null,
                        devoir != null ? devoir.getId() : null);
            }
            switch (recurrence.getFrequence()) {
                case DAILY:
                    currentDate = currentDate.plusDays(recurrence.getIntervalle() != null ? recurrence.getIntervalle() : 1);
                    break;
                case WEEKLY:
                    currentDate = currentDate.plusDays(1);
                    break;
                case MONTHLY:
                    currentDate = currentDate.plusMonths(recurrence.getIntervalle() != null ? recurrence.getIntervalle() : 1);
                    break;
            }
        }

        logger.debug("Total créneaux générés : {}", creneaux.size());
        return creneaux;
    }

    private boolean isValidDate(LocalDate date, RecurrenceRule rule) {
        if (rule.getFrequence() == RecurrenceRule.Frequence.WEEKLY && rule.getJours() != null) {
            String dayOfWeek = date.getDayOfWeek().toString().toUpperCase();
            String jourFr = convertToFrenchDay(dayOfWeek);
            logger.debug("Vérification jour : date={}, jourFr={}, joursAttendus={}", date, jourFr, rule.getJours());
            return rule.getJours().stream().anyMatch(jour -> jour.equalsIgnoreCase(jourFr));
        }
        return true;
    }

    private String convertToFrenchDay(String dayOfWeek) {
        switch (dayOfWeek) {
            case "MONDAY": return "LUNDI";
            case "TUESDAY": return "MARDI";
            case "WEDNESDAY": return "MERCREDI";
            case "THURSDAY": return "JEUDI";
            case "FRIDAY": return "VENDREDI";
            case "SATURDAY": return "SAMEDI";
            case "SUNDAY": return "DIMANCHE";
            default:
                logger.error("Jour non valide : {}", dayOfWeek);
                return "";
        }
    }

    private EmploiDuTempsResponseDTO mapToResponseDTO(EmploiDuTemps emploiDuTemps) {
        return new EmploiDuTempsResponseDTO(
                emploiDuTemps.getId(),
                emploiDuTemps.getNom(),
                emploiDuTemps.getSemestre().getId(),
                emploiDuTemps.getClasse().getId(),
                emploiDuTemps.getCreneaux().stream()
                        .map(creneau -> new CreneauResponseDTO(
                                creneau.getId(),
                                creneau.getDateDebut(),
                                creneau.getDateFin(),
                                creneau.getRecurrence() != null ? new CreneauResponseDTO.RecurrenceRuleDTO(
                                        creneau.getRecurrence().getId(),
                                        creneau.getRecurrence().getFrequence(),
                                        creneau.getRecurrence().getIntervalle(),
                                        creneau.getRecurrence().getJours(),
                                        creneau.getRecurrence().getDateFin(),
                                        creneau.getRecurrence().getNombreOccurrences()
                                ) : null,
                                emploiDuTemps.getId(),
                                creneau.getSalle().getId(),
                                creneau.getCours() != null ? creneau.getCours().getId() : null,
                                creneau.getDevoir() != null ? creneau.getDevoir().getId() : null
                        ))
                        .collect(Collectors.toList()),
                emploiDuTemps.getCreatedAt(),
                emploiDuTemps.getUpdatedAt(),
                emploiDuTemps.getCreatedBy(),
                emploiDuTemps.getUpdatedBy()
        );
    }
}