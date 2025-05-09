package com.souldev.security.services;

import com.souldev.security.entities.Diplome;
import com.souldev.security.entities.Eleve;
import com.souldev.security.repositories.DiplomeRepository;
import com.souldev.security.repositories.EleveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiplomeService {

    private final DiplomeRepository diplomeRepository;
    private final EleveRepository etudiantRepository;

    @Autowired
    public DiplomeService(DiplomeRepository diplomeRepository, EleveRepository etudiantRepository) {
        this.diplomeRepository = diplomeRepository;
        this.etudiantRepository = etudiantRepository;
    }

    // Créer un nouveau diplôme
    @Transactional
    public Diplome createDiplome(Diplome diplome) {
        if (diplome.getEleve() == null || diplome.getEleve().getId() == null) {
            throw new IllegalArgumentException("L’étudiant est requis pour créer un diplôme");
        }
        if (diplome.getNom() == null || diplome.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom du diplôme est requis");
        }
        if (diplome.getDateDelivrance() == null) {
            throw new IllegalArgumentException("La date d’obtention est requise");
        }

        Eleve etudiant = etudiantRepository.findById(diplome.getEleve().getId())
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l’ID : " + diplome.getEleve().getId()));
        diplome.setEleve(etudiant);
        return diplomeRepository.save(diplome);
    }

    // Récupérer un diplôme par ID
    public Optional<Diplome> getDiplomeById(String id) {
        return diplomeRepository.findById(id);
    }

    // Récupérer tous les diplômes
    public List<Diplome> getAllDiplomes() {
        return diplomeRepository.findAll();
    }

    // Récupérer les diplômes d’un étudiant
    public List<Diplome> getDiplomesByEtudiant(String etudiantId) {
        return diplomeRepository.findByEleveId(etudiantId);
    }

    // Récupérer les diplômes par statut (délivrés ou non)
    public List<Diplome> getDiplomesByDelivre(boolean delivre) {
        return diplomeRepository.findByDelivred(delivre);
    }

    // Mettre à jour un diplôme
    @Transactional
    public Diplome updateDiplome(String id, Diplome updatedDiplome) {
        Diplome diplome = diplomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diplôme non trouvé avec l’ID : " + id));

        if (updatedDiplome.getEleve() != null && updatedDiplome.getEleve().getId() != null) {
            Eleve etudiant = etudiantRepository.findById(updatedDiplome.getEleve().getId())
                    .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l’ID : " + updatedDiplome.getEleve().getId()));
            diplome.setEleve(etudiant);
        }
        if (updatedDiplome.getNom() != null && !updatedDiplome.getNom().isEmpty()) {
            diplome.setNom(updatedDiplome.getNom());
        }
        if (updatedDiplome.getMention() != null) {
            diplome.setMention(updatedDiplome.getMention());
        }
        if (updatedDiplome.getDateDelivrance() != null) {
            diplome.setDateDelivrance(updatedDiplome.getDateDelivrance());
        }
        // Le statut "delivre" peut être modifié (true/false)
        diplome.setDelivred(updatedDiplome.isDelivred());

        return diplomeRepository.save(diplome);
    }

    // Supprimer un diplôme
    @Transactional
    public void deleteDiplome(String id) {
        Diplome diplome = diplomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diplôme non trouvé avec l’ID : " + id));
        diplomeRepository.delete(diplome);
    }
}