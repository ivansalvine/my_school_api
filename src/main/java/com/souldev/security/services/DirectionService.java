package com.souldev.security.services;


import com.souldev.security.entities.Direction;
import com.souldev.security.entities.Ecole;
import com.souldev.security.entities.Fonction;
import com.souldev.security.payload.request.DirectionRequestDTO;
import com.souldev.security.payload.response.DirectionResponseDTO;
import com.souldev.security.repositories.DirectionRepository;
import com.souldev.security.repositories.EcoleRepository;
import com.souldev.security.repositories.FonctionRepository;
import com.souldev.security.security.entities.Role;
import com.souldev.security.security.entities.User;
import com.souldev.security.security.respositories.RoleRepository;
import com.souldev.security.security.respositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.souldev.security.utils.Utils.generatePassword;

@Service
@Transactional
public class DirectionService {
    private static final Logger logger = LoggerFactory.getLogger(DirectionService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final String DEFAULT_FILE_PATH = "C:/my-school/file";
    private static final String FILE_PATH_KEY = "file_path";

    private final DirectionRepository directionRepository;
    private final EcoleRepository ecoleRepository;
    private final FonctionRepository fonctionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfigurationService configurationService;
    private final String fileStoragePath;

    @Autowired
    public DirectionService(DirectionRepository directionRepository, EcoleRepository ecoleRepository,
                            FonctionRepository fonctionRepository, UserRepository userRepository,
                            RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                            ConfigurationService configurationService) {
        this.directionRepository = directionRepository;
        this.ecoleRepository = ecoleRepository;
        this.fonctionRepository = fonctionRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.configurationService = configurationService;
        this.fileStoragePath = configurationService.getValueOrDefault(FILE_PATH_KEY, DEFAULT_FILE_PATH);
        logger.info("Répertoire de stockage des fichiers configuré : {}", this.fileStoragePath);
    }

    public DirectionResponseDTO createDirection(DirectionRequestDTO dto, MultipartFile photoFile) {
        logger.info("Tentative de création d’un agent de direction : {} {}", dto.getNom(), dto.getPrenom());

        // Récupérer les entités liées
        Ecole ecole = ecoleRepository.findById(dto.getEcoleId())
                .orElseThrow(() -> {
                    logger.error("École non trouvée avec l’ID : {}", dto.getEcoleId());
                    return new EntityNotFoundException("École non trouvée avec l’ID : " + dto.getEcoleId());
                });
        Fonction fonction = fonctionRepository.findById(dto.getFonctionId())
                .orElseThrow(() -> {
                    logger.error("Fonction non trouvée avec l’ID : {}", dto.getFonctionId());
                    return new EntityNotFoundException("Fonction non trouvée avec l’ID : " + dto.getFonctionId());
                });
        Optional<Role> role = roleRepository.findByName("ROLE_DIRECTOR");
        if(role.isEmpty()){
            throw new EntityNotFoundException("Rôle ROLE_DIRECTOR non trouvé");
        }

        // Générer le mot de passe si non fourni
        String password =  generatePassword(dto.getNom());
        logger.info("Mot de passe généré pour {}: {}", dto.getNom().toLowerCase()+"@"+ecole.getDomaineName(), password);

        // Gérer l'upload de la photo
        String photoUrl = handleFileUpload(photoFile, dto.getNom(), "photo");

        // Créer un utilisateur
        User user = new User();
        user.setUsername(dto.getNom().toLowerCase()+"@"+ecole.getDomaineName());
        user.setEmail(dto.getNom().toLowerCase()+"@"+ecole.getDomaineName());
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(dto.getNom() + " " + dto.getPrenom());
        user.setEnabled(true);
        user.setRoles(new ArrayList<>());
        user.getRoles().add(role.get());
        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès : {}", savedUser.getUsername());

        // Créer l'agent de direction
        Direction direction = new Direction(
                dto.getNom(),
                dto.getPrenom(),
                fonction,
                ecole,
                user,
                dto.getNom().toLowerCase()+"@"+ecole.getDomaineName(),
                generatePassword(dto.getNom()),
                photoUrl
        );
        Direction savedDirection = directionRepository.save(direction);

        // Mettre à jour la relation inverse
        user.setDirection(savedDirection);
        userRepository.save(user);

        logger.info("Agent de direction créé avec succès : ID {}", savedDirection.getId());
        return mapToResponseDTO(savedDirection);
    }

    public DirectionResponseDTO getDirection(String id) {
        logger.info("Recherche de l’agent de direction avec l’ID : {}", id);
        Direction direction = directionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Agent de direction non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Agent de direction non trouvé avec l’ID : " + id);
                });
        return mapToResponseDTO(direction);
    }

    public List<DirectionResponseDTO> getAllDirections() {
        logger.info("Récupération de tous les agents de direction");
        List<Direction> directions = directionRepository.findAll();
        logger.info("Nombre d’agents de direction récupérés : {}", directions.size());
        return directions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public DirectionResponseDTO updateDirection(String id, DirectionRequestDTO dto, MultipartFile photoFile) {
        logger.info("Tentative de mise à jour de l’agent de direction avec ID : {}", id);

        // Récupérer l’agent existant
        Direction existingDirection = directionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Agent de direction non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Agent de direction non trouvé avec l’ID : " + id);
                });

        // Vérifier l'unicité de l'email
//        if (!dto.getEmail().equals(existingDirection.getEmail()) &&
//                (directionRepository.existsByEmail(dto.getEmail()) || userRepository.existsByEmail(dto.getEmail()))) {
//            logger.error("Échec : L'email {} existe déjà", dto.getEmail());
//            throw new IllegalArgumentException("L'email existe déjà");
//        }

        // Récupérer les entités liées
        Ecole ecole = ecoleRepository.findById(dto.getEcoleId())
                .orElseThrow(() -> {
                    logger.error("École non trouvée avec l’ID : {}", dto.getEcoleId());
                    return new EntityNotFoundException("École non trouvée avec l’ID : " + dto.getEcoleId());
                });
        Fonction fonction = fonctionRepository.findById(dto.getFonctionId())
                .orElseThrow(() -> {
                    logger.error("Fonction non trouvée avec l’ID : {}", dto.getFonctionId());
                    return new EntityNotFoundException("Fonction non trouvée avec l’ID : " + dto.getFonctionId());
                });

        // Gérer l'upload de la photo
        String photoUrl = handleFileUpload(photoFile, dto.getNom(), "photo");

        // Supprimer l'ancienne photo si une nouvelle est fournie
        if (photoUrl != null && existingDirection.getPhotoUrl() != null) {
            try {
                String filePath = fileStoragePath + "/" + existingDirection.getPhotoUrl();
                Files.deleteIfExists(Paths.get(filePath));
                logger.info("Ancienne photo supprimée : {}", filePath);
            } catch (IOException e) {
                logger.warn("Impossible de supprimer l'ancienne photo : {}", e.getMessage());
            }
        }

        // Mettre à jour les champs
        existingDirection.setNom(dto.getNom());
        existingDirection.setPrenom(dto.getPrenom());
        //existingDirection.setEmail(dto.getNom()+"@"+ecole.getDomaineName());
        existingDirection.setEcole(ecole);
        existingDirection.setFonction(fonction);
        if (photoUrl != null) {
            existingDirection.setPhotoUrl(photoUrl);
        }

        // Mettre à jour l'utilisateur
        User user = existingDirection.getUser();
        if (user == null) {
            logger.error("Échec : Aucun compte utilisateur associé à l’agent de direction");
            throw new EntityNotFoundException("Aucun compte utilisateur associé à l’agent de direction");
        }

        user.setFullName(dto.getNom() + " " + dto.getPrenom());
        userRepository.save(user);
        logger.info("Utilisateur mis à jour avec succès : {}", user.getUsername());

        // Enregistrer l’agent mis à jour
        Direction updatedDirection = directionRepository.save(existingDirection);
        logger.info("Agent de direction mis à jour avec succès : ID {}", updatedDirection.getId());
        return mapToResponseDTO(updatedDirection);
    }

    public void deleteDirection(String id) {
        logger.info("Tentative de suppression de l’agent de direction ID : {}", id);
        Direction direction = directionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Agent de direction non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Agent de direction non trouvé avec l’ID : " + id);
                });

        // Supprimer la photo associée
        if (direction.getPhotoUrl() != null) {
            try {
                String filePath = fileStoragePath + "/" + direction.getPhotoUrl();
                Files.deleteIfExists(Paths.get(filePath));
                logger.info("Photo supprimée : {}", filePath);
            } catch (IOException e) {
                logger.warn("Impossible de supprimer la photo : {}", e.getMessage());
            }
        }

        // Supprimer l'utilisateur associé
        User user = direction.getUser();
        if (user != null) {
            user.setDirection(null);
            userRepository.save(user);
            userRepository.delete(user);
            logger.info("Utilisateur associé supprimé : {}", user.getUsername());
        }

        // Supprimer l’agent
        directionRepository.delete(direction);
        logger.info("Agent de direction supprimé avec succès : ID {}", id);
    }

    private String handleFileUpload(MultipartFile file, String nom, String fileType) {
        if (file == null || file.isEmpty()) {
            logger.info("Aucun fichier {} fourni", fileType);
            return null;
        }

        // Validation de la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            logger.error("Échec : La taille du fichier {} dépasse la limite de {} bytes", fileType, MAX_FILE_SIZE);
            throw new IllegalArgumentException("La taille du fichier " + fileType + " dépasse la limite de 5MB");
        }

        // Validation de l'extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            logger.error("Échec : Nom de fichier {} invalide", fileType);
            throw new IllegalArgumentException("Nom de fichier " + fileType + " invalide");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            logger.error("Échec : Extension de fichier {} non autorisée pour {}", extension, fileType);
            throw new IllegalArgumentException("Extension de fichier non autorisée pour " + fileType + ". Extensions autorisées : " + ALLOWED_IMAGE_EXTENSIONS);
        }

        // Génération d'un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + nom.toLowerCase().replaceAll("[^a-z0-9]", "_") + "." + extension;
        Path filePath = Paths.get(fileStoragePath, fileName);

        try {
            Files.createDirectories(filePath.getParent()); // Créer le répertoire si nécessaire
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Fichier {} téléchargé avec succès : {}", fileType, filePath);
            return fileName;
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement du fichier {} : {}", fileType, e.getMessage());
            throw new RuntimeException("Erreur lors du téléchargement du fichier " + fileType, e);
        }
    }

    private DirectionResponseDTO mapToResponseDTO(Direction direction) {
        User user = direction.getUser();
        return new DirectionResponseDTO(
                direction.getId(),
                direction.getNom(),
                direction.getPrenom(),
                direction.getEmail(),
                direction.getEcole() != null ? direction.getEcole().getNom() : null,
                direction.getFonction() != null ? direction.getFonction().getName() : null,
                user != null ? user.getUsername() : null,
                direction.getPassword(),
                direction.getPhotoUrl(),
                direction.getCreatedAt(),
                direction.getUpdatedAt(),
                direction.getCreatedBy(),
                direction.getUpdatedBy()
        );
    }
}