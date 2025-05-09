package com.souldev.security.services;

import com.souldev.security.entities.Fonction;
import com.souldev.security.repositories.FonctionRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class FonctionService {

    private final FonctionRepository fonctionRepository;

    public FonctionService(FonctionRepository fonctionRepository) {
        this.fonctionRepository = fonctionRepository;
    }

    public Fonction getOne(String id){
        Optional<Fonction> optionalFonction = fonctionRepository.findById(id);
        return optionalFonction.orElse(null);
    }

    public List<Fonction> getAllFonction(){
        return fonctionRepository.findAll();
    }


    public Fonction createFonction(String name, String description){
        try {
            if(name.isEmpty()){
                throw new RuntimeException("Le nom de la fonction ne peut etre vide !");
            }

            Fonction fonction = new Fonction(name, description);
            return fonctionRepository.save(fonction);
        }
        catch (Exception e){
            return null;
        }
    }


    public Fonction updateFonction(String name, String description, String id){
        try {
            Optional<Fonction> optionalFonction = fonctionRepository.findById(id);
            if(optionalFonction.isEmpty()){
                throw new EntityNotFoundException("Erreur, fonction introuvable !");
            }

            System.err.println("optionalFonction --> "+optionalFonction.get());

            if(name.isEmpty()){
                throw new IllegalArgumentException("Le nom de la fonction ne peut etre vide !");
            }
            System.err.println("name --> "+name);
            System.err.println("description --> "+description);
            Fonction fonction = optionalFonction.get();
            fonction.setDescription(description);
            fonction.setName(name);
            return fonctionRepository.saveAndFlush(fonction);
        }
        catch (Exception e){
            return null;
        }
    }

    public boolean deleteFonction(String id){
        try {
            Optional<Fonction> optionalFonction = fonctionRepository.findById(id);
            if(optionalFonction.isEmpty()){
                throw new RuntimeException("Erreur, fonction introuvable !");
            }

            fonctionRepository.deleteById(id);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
