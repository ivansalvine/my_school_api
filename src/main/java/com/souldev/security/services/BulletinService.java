package com.souldev.security.services;

import com.souldev.security.entities.*;
import com.souldev.security.payload.request.BulletinGenerateRequestDTO;
import com.souldev.security.payload.response.BulletinDetailResponseDTO;
import com.souldev.security.payload.response.PdfGenerationResponseDTO;
import com.souldev.security.repositories.*;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BulletinService {
    private static final Logger logger = LoggerFactory.getLogger(BulletinService.class);
    private static final String BASE_URL = "http://localhost:8080/bulletins/download/"; // À configurer selon votre serveur

    private final BulletinRepository bulletinRepository;
    private final EleveRepository eleveRepository;
    private final ClasseRepository classeRepository;
    private final SemestreRepository semestreRepository;
    private final NoteRepository noteRepository;
    private final MatiereRepository matiereRepository;
    private final TypeMatiereService typeMatiereService;
    private final ConfigurationRepository configurationRepository;

    @Autowired
    public BulletinService(
            BulletinRepository bulletinRepository,
            EleveRepository eleveRepository,
            ClasseRepository classeRepository,
            SemestreRepository semestreRepository,
            NoteRepository noteRepository,
            MatiereRepository matiereRepository,
            TypeMatiereService typeMatiereService,
            ConfigurationRepository configurationRepository) {
        this.bulletinRepository = bulletinRepository;
        this.eleveRepository = eleveRepository;
        this.classeRepository = classeRepository;
        this.semestreRepository = semestreRepository;
        this.noteRepository = noteRepository;
        this.matiereRepository = matiereRepository;
        this.typeMatiereService = typeMatiereService;
        this.configurationRepository = configurationRepository;
    }

    @Transactional
    public List<PdfGenerationResponseDTO> generateBulletinsForClasse(String classeId, BulletinGenerateRequestDTO dto) throws JRException {
        logger.info("Génération des bulletins pour la classe ID : {}", classeId);

        // Récupérer le chemin des bulletins depuis la configuration
        String bulletinPath = configurationRepository.findById("bulletin_path")
                .map(Configuration::getValue)
                .orElseThrow(() -> {
                    logger.error("Configuration bulletin_path non trouvée");
                    return new EntityNotFoundException("Configuration bulletin_path non trouvée");
                });

        // Valider les entités
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l’ID : {}", classeId);
                    return new EntityNotFoundException("Classe non trouvée avec l’ID : " + classeId);
                });

        Semestre semestre = semestreRepository.findById(dto.getSemestreId())
                .orElseThrow(() -> {
                    logger.error("Semestre non trouvé avec l’ID : {}", dto.getSemestreId());
                    return new EntityNotFoundException("Semestre non trouvé avec l’ID : " + dto.getSemestreId());
                });

        // Vérifier que la classe dans le DTO correspond à celle fournie
        if (!dto.getClasseId().equals(classeId)) {
            logger.error("L’ID de la classe dans le DTO ({}) ne correspond pas à celui fourni ({})", dto.getClasseId(), classeId);
            throw new IllegalArgumentException("L’ID de la classe dans le DTO ne correspond pas à celui fourni !");
        }

        // Récupérer les élèves de la classe
        List<Eleve> eleves = eleveRepository.findByClasseId(classeId);
        if (eleves.isEmpty()) {
            logger.warn("Aucun élève trouvé pour la classe ID : {}", classeId);
            throw new EntityNotFoundException("Aucun élève trouvé pour la classe ID : " + classeId);
        }

        List<PdfGenerationResponseDTO> results = new ArrayList<>();

        // Générer un bulletin pour chaque élève
        for (Eleve eleve : eleves) {
            // Créer ou mettre à jour le bulletin
            Bulletin bulletin = bulletinRepository.findByEleveIdAndSemestreId(eleve.getId(), dto.getSemestreId())
                    .orElse(new Bulletin(eleve, semestre, classe, dto.getCommentaire()));
            bulletin.setClasse(classe);
            bulletin.setCommentaire(dto.getCommentaire());
            Bulletin savedBulletin = bulletinRepository.save(bulletin);

            // Générer les détails du bulletin
            BulletinDetailResponseDTO details = generateBulletinDetails(savedBulletin);

            // Générer le PDF
            byte[] pdf = generateBulletinPdf(details);

            // Enregistrer le PDF
            String pdfFileName = String.format("bulletin_%s_%s_%s.pdf", eleve.getId(), classeId, dto.getSemestreId());
            Path pdfPath = Paths.get(bulletinPath, pdfFileName);
            try {
                Files.write(pdfPath, pdf);
                logger.info("PDF généré et enregistré pour l’élève {} : {}", eleve.getId(), pdfPath);
            } catch (IOException e) {
                logger.error("Erreur lors de l’enregistrement du PDF pour l’élève {} : {}", eleve.getId(), e.getMessage());
                throw new RuntimeException("Erreur lors de l’enregistrement du PDF", e);
            }

            // Générer l’URL de téléchargement
            String downloadUrl = BASE_URL + pdfFileName;

            // Ajouter le résultat
            results.add(new PdfGenerationResponseDTO(
                    eleve.getId(),
                    savedBulletin.getId(),
                    pdfFileName,
                    downloadUrl
            ));
        }

        logger.info("Génération terminée : {} bulletins générés pour la classe ID : {}", results.size(), classeId);
        return results;
    }

    private BulletinDetailResponseDTO generateBulletinDetails(Bulletin bulletin) {
        logger.info("Génération des détails pour le bulletin ID : {}", bulletin.getId());

        // Récupérer les notes
        List<Note> notes = noteRepository.findByEleveIdAndDevoirSemestreId(
                bulletin.getEleve().getId(),
                bulletin.getSemestre().getId()
        );

        // Grouper les notes par matière
        Map<Matiere, List<Note>> notesByMatiere = notes.stream()
                .collect(Collectors.groupingBy(note -> note.getDevoir().getMatiere()));

        // Récupérer tous les types de matières
        List<TypeMatiere> typeMatieres = typeMatiereService.getAllTypeMatiere();
        List<BulletinDetailResponseDTO.TypeMatiereDetail> typeMatiereDetails = new ArrayList<>();
        double sommeMoyennesPonderees = 0.0;
        double sommeCoefficients = 0.0;

        // Pour chaque type de matière
        for (TypeMatiere typeMatiere : typeMatieres) {
            List<BulletinDetailResponseDTO.MatiereDetail> matiereDetails = new ArrayList<>();
            double sommeMoyennesPondereesType = 0.0;
            double sommeCoefficientsType = 0.0;

            // Récupérer les matières de ce type
            List<Matiere> matieres = matiereRepository.findByTypeMatiereId(typeMatiere.getId());
            for (Matiere matiere : matieres) {
                List<Note> matiereNotes = notesByMatiere.getOrDefault(matiere, new ArrayList<>());
                if (!matiereNotes.isEmpty()) {
                    double moyenne = matiereNotes.stream()
                            .mapToDouble(Note::getValeur)
                            .average()
                            .orElse(0.0);
                    double coefficient = matiere.getCoefficient() != null ? matiere.getCoefficient() : 0.0;
                    sommeMoyennesPondereesType += moyenne * coefficient;
                    sommeCoefficientsType += coefficient;
                    sommeMoyennesPonderees += moyenne * coefficient;
                    sommeCoefficients += coefficient;

                    double moyenneClasse = calculateClassAverageForMatiere(bulletin.getClasse(), bulletin.getSemestre(), matiere);
                    String professeurNom = matiereNotes.stream()
                            .map(note -> note.getDevoir().getProfesseur())
                            .filter(Objects::nonNull)
                            .map(Professeur::getNom)
                            .findFirst()
                            .orElse("N/A");
                    String commentaires = matiereNotes.stream()
                            .map(Note::getCommentaire)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining("; "));

                    matiereDetails.add(new BulletinDetailResponseDTO.MatiereDetail(
                            matiere.getNom(),
                            moyenne,
                            coefficient,
                            moyenneClasse,
                            professeurNom,
                            commentaires
                    ));
                }
            }

            // Calculer la moyenne et le rang pour ce type de matière
            double moyenneType = sommeCoefficientsType > 0 ? sommeMoyennesPondereesType / sommeCoefficientsType : 0.0;
            int rangType = typeMatiereService.calculateRankByTypeMatiere(
                    bulletin.getEleve().getId(),
                    typeMatiere.getId(),
                    bulletin.getClasse().getId(),
                    bulletin.getSemestre().getId()
            );

            if (!matiereDetails.isEmpty()) {
                typeMatiereDetails.add(new BulletinDetailResponseDTO.TypeMatiereDetail(
                        typeMatiere.getName(),
                        moyenneType,
                        rangType,
                        matiereDetails
                ));
            }
        }

        double moyenneGenerale = sommeCoefficients > 0 ? sommeMoyennesPonderees / sommeCoefficients : 0.0;
        int rang = calculateRank(bulletin.getEleve(), bulletin.getClasse(), bulletin.getSemestre());

        return new BulletinDetailResponseDTO(
                bulletin.getId(),
                bulletin.getEleve().getNom(),
                bulletin.getSemestre().getNom(),
                bulletin.getClasse().getNom(),
                typeMatiereDetails,
                moyenneGenerale,
                rang,
                bulletin.getCommentaire()
        );
    }

    private double calculateClassAverageForMatiere(Classe classe, Semestre semestre, Matiere matiere) {
        List<Eleve> eleves = eleveRepository.findByClasseId(classe.getId());
        if (eleves.isEmpty()) {
            return 0.0;
        }

        double sommeMoyennes = 0.0;
        int nombreElevesAvecNotes = 0;

        for (Eleve eleve : eleves) {
            List<Note> notes = noteRepository.findByEleveIdAndDevoirSemestreIdAndDevoirMatiereId(
                    eleve.getId(), semestre.getId(), matiere.getId()
            );
            if (!notes.isEmpty()) {
                double moyenne = notes.stream()
                        .mapToDouble(Note::getValeur)
                        .average()
                        .orElse(0.0);
                sommeMoyennes += moyenne;
                nombreElevesAvecNotes++;
            }
        }

        return nombreElevesAvecNotes > 0 ? sommeMoyennes / nombreElevesAvecNotes : 0.0;
    }

    private int calculateRank(Eleve eleve, Classe classe, Semestre semestre) {
        // Implémentation simplifiée pour le rang
        return 1; // À remplacer par une logique réelle
    }

    private byte[] generateBulletinPdf(BulletinDetailResponseDTO details) throws JRException {
        // Implémentation simplifiée pour générer un PDF
        return new byte[0]; // À remplacer par une logique réelle avec JasperReports
    }

    // Autres méthodes si nécessaires
}