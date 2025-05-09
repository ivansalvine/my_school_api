package com.souldev.security.services;

import com.souldev.security.entities.Configuration;
import com.souldev.security.repositories.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    // Créer ou mettre à jour une configuration (upsert)
    public Configuration saveConfiguration(Configuration configuration) {
        if (configuration.getKey() == null || configuration.getKey().isEmpty()) {
            throw new IllegalArgumentException("La clé est requise pour une configuration");
        }
        if (configuration.getValue() == null) {
            throw new IllegalArgumentException("La valeur ne peut pas être null pour la clé : " + configuration.getKey());
        }
        return configurationRepository.save(configuration);
    }

    // Récupérer une configuration par clé
    public Optional<Configuration> getConfigurationByKey(String key) {
        return configurationRepository.findById(key);
    }

    // Récupérer toutes les configurations
    public List<Configuration> getAllConfigurations() {
        return configurationRepository.findAll();
    }

    // Mettre à jour une configuration (par clé)
    public Configuration updateConfiguration(String key, String newValue) {
        Configuration configuration = configurationRepository.findById(key)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée avec la clé : " + key));
        configuration.setValue(newValue);
        return configurationRepository.save(configuration);
    }

    // Supprimer une configuration
    public void deleteConfiguration(String key) {
        Configuration configuration = configurationRepository.findById(key)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée avec la clé : " + key));
        configurationRepository.delete(configuration);
    }

    // Récupérer une valeur avec une valeur par défaut si absente
    public String getValueOrDefault(String key, String defaultValue) {
        return configurationRepository.findById(key)
                .map(Configuration::getValue)
                .orElse(defaultValue);
    }
}