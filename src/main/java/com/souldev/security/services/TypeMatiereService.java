package com.souldev.security.services;

import com.souldev.security.entities.Eleve;
import com.souldev.security.entities.Matiere;
import com.souldev.security.entities.Note;
import com.souldev.security.entities.TypeMatiere;
import com.souldev.security.repositories.EleveRepository;
import com.souldev.security.repositories.MatiereRepository;
import com.souldev.security.repositories.NoteRepository;
import com.souldev.security.repositories.TypeMatiereRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TypeMatiereService {
    private static final Logger logger = LoggerFactory.getLogger(TypeMatiereService.class);

    private final TypeMatiereRepository typeMatiereRepository;
    private final MatiereRepository matiereRepository;
    private final NoteRepository noteRepository;
    private final EleveRepository eleveRepository;

    @Autowired
    public TypeMatiereService(TypeMatiereRepository typeMatiereRepository,
                              MatiereRepository matiereRepository,
                              NoteRepository noteRepository,
                              EleveRepository eleveRepository) {
        this.typeMatiereRepository = typeMatiereRepository;
        this.matiereRepository = matiereRepository;
        this.noteRepository = noteRepository;
        this.eleveRepository = eleveRepository;
    }

    /**
     * Récupère un type de matière par son ID.
     * @param id ID du type de matière.
     * @return TypeMatiere ou null si non trouvé.
     */
    public TypeMatiere getTypeMatiere(String id) {
        logger.info("Recherche du type de matière avec ID : {}", id);
        return typeMatiereRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Type de matière non trouvé avec ID : {}", id);
                    return new EntityNotFoundException("Type de matière non trouvé avec ID : " + id);
                });
    }

    /**
     * Récupère tous les types de matières.
     * @return Liste de TypeMatiere.
     */
    public List<TypeMatiere> getAllTypeMatiere() {
        logger.info("Récupération de tous les types de matières");
        List<TypeMatiere> types = typeMatiereRepository.findAll();
        logger.info("Nombre de types de matières récupérés : {}", types.size());
        return types;
    }

    /**
     * Crée un nouveau type de matière.
     * @param name Nom du type de matière.
     * @param description Description du type de matière.
     * @return TypeMatiere créé.
     */
    public TypeMatiere createTypeMatiere(String name, String description) {
        logger.info("Création d’un type de matière : {}", name);
        if (name == null || name.trim().isEmpty()) {
            logger.error("Le nom du type de matière ne peut être vide");
            throw new IllegalArgumentException("Le nom ne peut être vide !");
        }

        TypeMatiere typeMatiere = new TypeMatiere(name.trim(), description);
        TypeMatiere savedType = typeMatiereRepository.save(typeMatiere);
        logger.info("Type de matière créé avec succès : ID {}", savedType.getId());
        return savedType;
    }

    /**
     * Met à jour un type de matière existant.
     * @param id ID du type de matière.
     * @param name Nouveau nom.
     * @param description Nouvelle description.
     * @return TypeMatiere mis à jour.
     */
    public TypeMatiere updateTypeMatiere(String id, String name, String description) {
        logger.info("Mise à jour du type de matière avec ID : {}", id);
        TypeMatiere typeMatiere = typeMatiereRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Type de matière non trouvé avec ID : {}", id);
                    return new EntityNotFoundException("Type de matière introuvable, merci de vérifier !");
                });

        if (name == null || name.trim().isEmpty()) {
            logger.error("Le nom du type de matière ne peut être vide");
            throw new IllegalArgumentException("Le nom ne peut être vide !");
        }

        typeMatiere.setName(name.trim());
        typeMatiere.setDescription(description);
        TypeMatiere updatedType = typeMatiereRepository.save(typeMatiere);
        logger.info("Type de matière mis à jour avec succès : ID {}", updatedType.getId());
        return updatedType;
    }

    /**
     * Supprime un type de matière.
     * @param id ID du type de matière.
     * @return true si supprimé, lance une exception sinon.
     */
    public boolean deleteTypeMatiere(String id) {
        logger.info("Suppression du type de matière avec ID : {}", id);
        TypeMatiere typeMatiere = typeMatiereRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Type de matière non trouvé avec ID : {}", id);
                    return new EntityNotFoundException("Type de matière introuvable, merci de vérifier !");
                });

        // Vérifier si des matières sont associées
        List<Matiere> matieres = matiereRepository.findByTypeMatiereId(id);
        if (!matieres.isEmpty()) {
            logger.error("Impossible de supprimer le type de matière {} car il est associé à {} matière(s)", id, matieres.size());
            throw new IllegalStateException("Impossible de supprimer un type de matière associé à des matières !");
        }

        typeMatiereRepository.delete(typeMatiere);
        logger.info("Type de matière supprimé avec succès : ID {}", id);
        return true;
    }

    /**
     * Calcule la moyenne pondérée d’un élève pour un type de matière dans un semestre.
     * @param eleveId ID de l’élève.
     * @param typeMatiereId ID du type de matière.
     * @param semestreId ID du semestre.
     * @return Moyenne pondérée ou 0.0 si aucune note.
     */
    public double calculateAverageByTypeMatiere(String eleveId, String typeMatiereId, String semestreId) {
        logger.info("Calcul de la moyenne pour l’élève {} dans le type de matière {} pour le semestre {}", eleveId, typeMatiereId, semestreId);

        // Récupérer les matières associées au type de matière
        List<Matiere> matieres = matiereRepository.findByTypeMatiereId(typeMatiereId);
        if (matieres.isEmpty()) {
            logger.warn("Aucune matière trouvée pour le type de matière ID : {}", typeMatiereId);
            return 0.0;
        }

        double sommeMoyennesPonderees = 0.0;
        double sommeCoefficients = 0.0;

        // Pour chaque matière, calculer la moyenne des notes
        for (Matiere matiere : matieres) {
            List<Note> notes = noteRepository.findByEleveIdAndDevoirSemestreIdAndDevoirMatiereId(
                    eleveId, semestreId, matiere.getId()
            );
            if (!notes.isEmpty()) {
                double moyenne = notes.stream()
                        .mapToDouble(Note::getValeur)
                        .average()
                        .orElse(0.0);
                double coefficient = matiere.getCoefficient() != null ? matiere.getCoefficient() : 0.0;
                sommeMoyennesPonderees += moyenne * coefficient;
                sommeCoefficients += coefficient;
            }
        }

        double moyenne = sommeCoefficients > 0 ? sommeMoyennesPonderees / sommeCoefficients : 0.0;
        logger.info("Moyenne calculée pour l’élève {} dans le type de matière {} : {}", eleveId, typeMatiereId, moyenne);
        return moyenne;
    }

    /**
     * Calcule le rang d’un élève pour un type de matière dans une classe pour un semestre.
     * @param eleveId ID de l’élève.
     * @param typeMatiereId ID du type de matière.
     * @param classeId ID de la classe.
     * @param semestreId ID du semestre.
     * @return Rang de l’élève (1 pour le premier, etc.).
     */
    public int calculateRankByTypeMatiere(String eleveId, String typeMatiereId, String classeId, String semestreId) {
        logger.info("Calcul du rang pour l’élève {} dans le type de matière {} pour la classe {} et le semestre {}",
                eleveId, typeMatiereId, classeId, semestreId);

        // Récupérer tous les élèves de la classe
        List<Eleve> eleves = eleveRepository.findByClasseId(classeId);
        if (eleves.isEmpty()) {
            logger.warn("Aucune élève trouvé dans la classe ID : {}", classeId);
            return 1;
        }

        // Calculer la moyenne par type de matière pour chaque élève
        List<Map.Entry<Eleve, Double>> moyennes = new ArrayList<>();
        for (Eleve eleve : eleves) {
            double moyenne = calculateAverageByTypeMatiere(eleve.getId(), typeMatiereId, semestreId);
            moyennes.add(new AbstractMap.SimpleEntry<>(eleve, moyenne));
        }

        // Trier par moyenne décroissante
        moyennes.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // Trouver le rang de l’élève
        for (int i = 0; i < moyennes.size(); i++) {
            if (moyennes.get(i).getKey().getId().equals(eleveId)) {
                logger.info("Rang calculé pour l’élève {} dans le type de matière {} : {}", eleveId, typeMatiereId, i + 1);
                return i + 1;
            }
        }

        logger.warn("Élève {} non trouvé dans les moyennes, retourne le dernier rang", eleveId);
        return moyennes.size() + 1;
    }
}