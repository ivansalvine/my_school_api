package com.souldev.security.repositories;

import com.souldev.security.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, String> {


    List<Note> findByEleveId(String etudiantId);
    List<Note> findByDevoirId(String devoirId);
    boolean existsByDevoirIdAndEleveId(String devoirId, String etudiantId);
    /**
     * Récupère toutes les notes d’un élève pour les devoirs associés à un semestre spécifique.
     *
     * @param eleveId    L’ID de l’élève
     * @param semestreId L’ID du semestre
     * @return Liste des notes correspondantes
     */
    List<Note> findByEleveIdAndDevoirSemestreId(String eleveId, String semestreId);

    /**
     * Vérifie si une note existe pour un élève et un devoir spécifiques.
     *
     * @param eleveId  L’ID de l’élève
     * @param devoirId L’ID du devoir
     * @return true si une note existe, false sinon
     */
    boolean existsByEleveIdAndDevoirId(String eleveId, String devoirId);


    /**
     * Récupère toutes les notes d’un élève pour les devoirs associés à un semestre et une matière spécifiques.
     *
     * @param eleveId    L’ID de l’élève
     * @param semestreId L’ID du semestre
     * @param matiereId  L’ID de la matière
     * @return Liste des notes correspondantes
     */
    @Query("SELECT n FROM Note n JOIN n.devoir d WHERE n.eleve.id = :eleveId AND d.semestre.id = :semestreId AND d.matiere.id = :matiereId")
    List<Note> findByEleveIdAndDevoirSemestreIdAndDevoirMatiereId(
            @Param("eleveId") String eleveId,
            @Param("semestreId") String semestreId,
            @Param("matiereId") String matiereId
    );

}
