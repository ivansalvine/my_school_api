package com.souldev.security.services;

import com.souldev.security.entities.Ecole;
import com.souldev.security.entities.Professeur;
import com.souldev.security.payload.request.ProfesseurDTO;
import com.souldev.security.payload.response.ProfesseurResponseDTO;
import com.souldev.security.repositories.*;
import com.souldev.security.security.entities.Role;
import com.souldev.security.security.entities.User;
import com.souldev.security.security.enums.RoleList;
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
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfesseurService {

    private static final Logger logger = LoggerFactory.getLogger(ProfesseurService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final List<String> ALLOWED_LICENCE_EXTENSIONS = List.of("pdf", "jpg", "jpeg", "png");
    private static final String DEFAULT_FILE_PATH = "C:/my-school/file";
    private static final String FILE_PATH_KEY = "file_path";
    private static final String FILE_URL_PREFIX = "/files/";

    private final ProfesseurRepository professeurRepository;
    private final UserRepository userRepository;
    private final EcoleRepository ecoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MatiereRepository matiereRepository;
    private final FiliereRepository filiereRepository;
    private final ClasseRepository classeRepository;
    private final ConfigurationService configurationService;
    private final String fileStoragePath;

    @Autowired
    public ProfesseurService(ProfesseurRepository professeurRepository, UserRepository userRepository,
                             EcoleRepository ecoleRepository, RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder, MatiereRepository matiereRepository,
                             FiliereRepository filiereRepository, ClasseRepository classeRepository,
                             ConfigurationService configurationService) {
        this.professeurRepository = professeurRepository;
        this.userRepository = userRepository;
        this.ecoleRepository = ecoleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.matiereRepository = matiereRepository;
        this.filiereRepository = filiereRepository;
        this.classeRepository = classeRepository;
        this.configurationService = configurationService;

        // Récupérer le chemin de stockage depuis la configuration
        this.fileStoragePath = configurationService.getValueOrDefault(FILE_PATH_KEY, DEFAULT_FILE_PATH);
        logger.info("Répertoire de stockage des fichiers configuré : {}", this.fileStoragePath);
    }

    /**
     * Récupère l'école unique.
     */
    private Ecole getEcole() {
        logger.info("Récupération de l’école unique");
        List<Ecole> ecoles = ecoleRepository.findAll();
        if (ecoles.isEmpty()) {
            logger.error("Aucune école trouvée");
            throw new EntityNotFoundException("Aucune école trouvée");
        }
        return ecoles.get(0);
    }

    /**
     * Crée un nouveau professeur avec un compte utilisateur et des fichiers.
     */
    public ProfesseurResponseDTO createProfesseur(ProfesseurDTO professeurDTO, MultipartFile imageFile, MultipartFile licenceFile) {
        logger.info("Tentative de création d’un professeur : {} {}", professeurDTO.getNom(), professeurDTO.getPrenom());

        // Validation des champs du DTO
        validateProfesseurDTO(professeurDTO);

        // Gestion des fichiers
        String imageUrl = handleFileUpload(imageFile, professeurDTO.getNom(), ALLOWED_IMAGE_EXTENSIONS, "image");
        String licenceUrl = handleFileUpload(licenceFile, professeurDTO.getNom(), ALLOWED_LICENCE_EXTENSIONS, "licence");

        // Création du compte User
        User user = new User();
        String username = professeurDTO.getNom().toLowerCase();
        user.setUsername(username);
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("Échec : Le nom d’utilisateur {} existe déjà", user.getUsername());
            throw new IllegalArgumentException("Le nom d’utilisateur existe déjà");
        }

        // Génération du mot de passe sécurisé
        String rawPassword = generatePassword(professeurDTO.getNom());
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Configuration des autres champs
        Ecole ecole = getEcole();
        user.setEmail(username + "@" + ecole.getDomaineName());
        user.setFullName(professeurDTO.getPrenom() + " " + professeurDTO.getNom());
        user.setEnabled(true);

        // Attribution du rôle ROLE_TEACHER
        Role teacherRole = roleRepository.findByName(RoleList.ROLE_TEACHER.name())
                .orElseThrow(() -> {
                    logger.error("Rôle ROLE_TEACHER non trouvé");
                    return new EntityNotFoundException("Rôle ROLE_TEACHER non trouvé");
                });
        user.setRoles(new HashSet<>() {{
            add(teacherRole);
        }});

        // Enregistrement de l’utilisateur
        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès : {}", savedUser.getUsername());

        // Création du professeur
        Professeur professeur = new Professeur();
        professeur.setNom(professeurDTO.getNom());
        professeur.setPrenom(professeurDTO.getPrenom());
        professeur.setTelephone(professeurDTO.getTelephone());
        professeur.setDiplome(professeurDTO.getDiplome());
        professeur.setAnneeExperience(professeurDTO.getAnneeExperience());
        professeur.setImageUrl(imageUrl);
        professeur.setLicenceUrl(licenceUrl);
        professeur.setUser(savedUser);

        // Enregistrement du professeur
        Professeur savedProfesseur = professeurRepository.save(professeur);
        logger.info("Professeur créé avec succès : ID {}", savedProfesseur.getId());

        return mapToResponseDTO(savedProfesseur);
    }

    /**
     * Récupère un professeur par son ID.
     */
    public ProfesseurResponseDTO getProfesseurById(String id) {
        logger.info("Recherche du professeur avec l’ID : {}", id);
        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Professeur non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Professeur non trouvé avec l’ID : " + id);
                });
        return mapToResponseDTO(professeur);
    }

    /**
     * Récupère tous les professeurs.
     */
    public List<ProfesseurResponseDTO> getAllProfesseurs() {
        logger.info("Récupération de tous les professeurs");
        List<Professeur> professeurs = professeurRepository.findAll();
        logger.info("Nombre de professeurs récupérés : {}", professeurs.size());
        return professeurs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un professeur par l'ID de son utilisateur.
     */
    public ProfesseurResponseDTO getProfesseurByUserId(String userId) {
        logger.info("Recherche du professeur par ID utilisateur : {}", userId);
        Professeur professeur = professeurRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Aucun professeur trouvé pour l’utilisateur ID : {}", userId);
                    return new EntityNotFoundException("Aucun professeur trouvé pour l’utilisateur ID : " + userId);
                });
        return mapToResponseDTO(professeur);
    }

    /**
     * Met à jour un professeur existant.
     */
//    public ProfesseurResponseDTO updateProfesseur(String professeurId, ProfesseurDTO professeurDTO,
//                                                  MultipartFile imageFile, MultipartFile licenceFile) {
//        logger.info("Tentative de mise à jour du professeur avec ID : {}", professeurId);
//
//        // Récupération du professeur existant
//        Professeur existingProfesseur = professeurRepository.findById(professeurId)
//                .orElseThrow(() -> {
//                    logger.error("Professeur non trouvé avec l’ID : {}", professeurId);
//                    return new EntityNotFoundException("Professeur non trouvé avec l’ID : " + professeurId);
//                });
//
//        // Validation des champs du DTO
//        validateProfesseurDTO(professeurDTO);
//
//        // Gestion des fichiers
//        String imageUrl = handleFileUpload(imageFile, professeurDTO.getNom(), ALLOWED_IMAGE_EXTENSIONS, "image");
//        String licenceUrl = handleFileUpload(licenceFile, professeurDTO.getNom(), ALLOWED_LICENCE_EXTENSIONS, "licence");
//
//        // Suppression des anciens fichiers si de nouveaux sont téléchargés
//        if (imageUrl != null && existingProfesseur.getImageUrl() != null) {
//            try {
//                String filePath = fileStoragePath + "/" + existingProfesseur.getImageUrl().substring(FILE_URL_PREFIX.length());
//                Files.deleteIfExists(Paths.get(filePath));
//                logger.info("Ancien fichier image supprimé : {}", filePath);
//            } catch (IOException e) {
//                logger.warn("Impossible de supprimer l'ancien fichier image : {}", e.getMessage());
//            }
//        }
//        if (licenceUrl != null && existingProfesseur.getLicenceUrl() != null) {
//            try {
//                String filePath = fileStoragePath + "/" + existingProfesseur.getLicenceUrl().substring(FILE_URL_PREFIX.length());
//                Files.deleteIfExists(Paths.get(filePath));
//                logger.info("Ancien fichier licence supprimé : {}", filePath);
//            } catch (IOException e) {
//                logger.warn("Impossible de supprimer l'ancien fichier licence : {}", e.getMessage());
//            }
//        }
//
//        // Mise à jour des champs du professeur
//        existingProfesseur.setNom(professeurDTO.getNom());
//        existingProfesseur.setPrenom(professeurDTO.getPrenom());
//        existingProfesseur.setTelephone(professeurDTO.getTelephone());
//        existingProfesseur.setDiplome(professeurDTO.getDiplome());
//        existingProfesseur.setAnneeExperience(professeurDTO.getAnneeExperience());
//        if (imageUrl != null) {
//            existingProfesseur.setImageUrl(imageUrl);
//        }
//        if (licenceUrl != null) {
//            existingProfesseur.setLicenceUrl(licenceUrl);
//        }
//
//        // Mise à jour du compte User associé
//        User user = existingProfesseur.getUser();
//        if (user == null) {
//            logger.error("Échec : Aucun compte utilisateur associé au professeur");
//            throw new EntityNotFoundException("Aucun compte utilisateur associé au professeur");
//        }
//
//        // Mise à jour du username
//        String newUsername = professeurDTO.getNom().toLowerCase() + "." + professeurDTO.getPrenom().toLowerCase();
//        if (!user.getUsername().equals(newUsername)) {
//            if (userRepository.existsByUsername(newUsername)) {
//                logger.error("Échec : Le nom d’utilisateur {} existe déjà", newUsername);
//                throw new IllegalArgumentException("Le nom d’utilisateur existe déjà");
//            }
//            user.setUsername(newUsername);
//        }
//
//        // Mise à jour de l’email et fullName
//        Ecole ecole = getEcole();
//        user.setEmail(newUsername + "@" + ecole.getDomaineName());
//        user.setFullName(professeurDTO.getPrenom() + " " + professeurDTO.getNom());
//
//        // Enregistrement du User mis à jour
//        userRepository.save(user);
//        logger.info("Utilisateur mis à jour avec succès : {}", user.getUsername());
//
//        // Enregistrement du professeur mis à jour
//        Professeur updatedProfesseur = professeurRepository.save(existingProfesseur);
//        logger.info("Professeur mis à jour avec succès : ID {}", updatedProfesseur.getId());
//
//        return mapToResponseDTO(updatedProfesseur);
//    }

    public ProfesseurResponseDTO updateProfesseur(String professeurId, ProfesseurDTO professeurDTO,
                                                  MultipartFile imageFile, MultipartFile licenceFile) {
        logger.info("Tentative de mise à jour du professeur avec ID : {}", professeurId);

        // Récupération du professeur existant
        Professeur existingProfesseur = professeurRepository.findById(professeurId)
                .orElseThrow(() -> {
                    logger.error("Professeur non trouvé avec l’ID : {}", professeurId);
                    return new EntityNotFoundException("Professeur non trouvé avec l’ID : " + professeurId);
                });

        // Validation des champs du DTO
        validateProfesseurDTO(professeurDTO);

        // Gestion des fichiers
        String imageUrl = handleFileUpload(imageFile, professeurDTO.getNom(), ALLOWED_IMAGE_EXTENSIONS, "image");
        String licenceUrl = handleFileUpload(licenceFile, professeurDTO.getNom(), ALLOWED_LICENCE_EXTENSIONS, "licence");

        // Suppression des anciens fichiers si de nouveaux sont téléchargés
        if (imageUrl != null && existingProfesseur.getImageUrl() != null) {
            try {
                String filePath = fileStoragePath + "/" + existingProfesseur.getImageUrl();
                Files.deleteIfExists(Paths.get(filePath));
                logger.info("Ancien fichier image supprimé : {}", filePath);
            } catch (IOException e) {
                logger.warn("Impossible de supprimer l'ancien fichier image : {}", e.getMessage());
            }
        }
        if (licenceUrl != null && existingProfesseur.getLicenceUrl() != null) {
            try {
                String filePath = fileStoragePath + "/" + existingProfesseur.getLicenceUrl();
                Files.deleteIfExists(Paths.get(filePath));
                logger.info("Ancien fichier licence supprimé : {}", filePath);
            } catch (IOException e) {
                logger.warn("Impossible de supprimer l'ancien fichier licence : {}", e.getMessage());
            }
        }

        // Mise à jour des champs du professeur
        existingProfesseur.setNom(professeurDTO.getNom());
        existingProfesseur.setPrenom(professeurDTO.getPrenom());
        existingProfesseur.setTelephone(professeurDTO.getTelephone());
        existingProfesseur.setDiplome(professeurDTO.getDiplome());
        existingProfesseur.setAnneeExperience(professeurDTO.getAnneeExperience());
        if (imageUrl != null) {
            existingProfesseur.setImageUrl(imageUrl);
        }
        if (licenceUrl != null) {
            existingProfesseur.setLicenceUrl(licenceUrl);
        }

        // Mise à jour du compte User associé
        User user = existingProfesseur.getUser();
        if (user == null) {
            logger.error("Échec : Aucun compte utilisateur associé au professeur");
            throw new EntityNotFoundException("Aucun compte utilisateur associé au professeur");
        }

        // Mise à jour du username
        String newUsername = professeurDTO.getNom().toLowerCase();
        if (!user.getUsername().equals(newUsername)) {
            if (userRepository.existsByUsername(newUsername)) {
                logger.error("Échec : Le nom d’utilisateur {} existe déjà", newUsername);
                throw new IllegalArgumentException("Le nom d’utilisateur existe déjà");
            }
            user.setUsername(newUsername);
        }

        // Mise à jour de l’email et fullName
        Ecole ecole = getEcole();
        user.setEmail(newUsername + "@" + ecole.getDomaineName());
        user.setFullName(professeurDTO.getPrenom() + " " + professeurDTO.getNom());

        // Enregistrement du User mis à jour
        userRepository.save(user);
        logger.info("Utilisateur mis à jour avec succès : {}", user.getUsername());

        // Enregistrement du professeur mis à jour
        Professeur updatedProfesseur = professeurRepository.save(existingProfesseur);
        logger.info("Professeur mis à jour avec succès : ID {}", updatedProfesseur.getId());

        return mapToResponseDTO(updatedProfesseur);
    }

    /**
     * Valide les champs du DTO.
     */
    private void validateProfesseurDTO(ProfesseurDTO professeurDTO) {
        if (professeurDTO.getNom() == null || professeurDTO.getNom().isBlank()) {
            logger.error("Échec : Le nom est requis");
            throw new IllegalArgumentException("Le nom est requis");
        }
        if (professeurDTO.getPrenom() == null || professeurDTO.getPrenom().isBlank()) {
            logger.error("Échec : Le prénom est requis");
            throw new IllegalArgumentException("Le prénom est requis");
        }
        if (professeurDTO.getDiplome() == null || professeurDTO.getDiplome().isBlank()) {
            logger.error("Échec : Le diplôme est requis");
            throw new IllegalArgumentException("Le diplôme est requis");
        }
        if (professeurDTO.getAnneeExperience() == null || professeurDTO.getAnneeExperience().isBlank()) {
            logger.error("Échec : L'année d'expérience est requise");
            throw new IllegalArgumentException("L'année d'expérience est requise");
        }
    }

    /**
     * Gère le téléchargement des fichiers.
     */
    // Dans handleFileUpload
    private String handleFileUpload(MultipartFile file, String nom, List<String> allowedExtensions, String fileType) {
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
        if (!allowedExtensions.contains(extension)) {
            logger.error("Échec : Extension de fichier {} non autorisée pour {}", extension, fileType);
            throw new IllegalArgumentException("Extension de fichier non autorisée pour " + fileType + ". Extensions autorisées : " + allowedExtensions);
        }

        // Génération d'un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + nom.toLowerCase().replaceAll("[^a-z0-9]", "_") + "." + extension;
        Path filePath = Paths.get(fileStoragePath, fileName);

        try {
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Fichier {} téléchargé avec succès : {}", fileType, filePath);
            return fileName; // Retourner uniquement le nom du fichier
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement du fichier {} : {}", fileType, e.getMessage());
            throw new RuntimeException("Erreur lors du téléchargement du fichier " + fileType, e);
        }
    }

    /**
     * Génération du mot de passe sécurisé.
     */
    private String generatePassword(String nom) {
        String base = nom.length() >= 5 ? nom.substring(0, 5) : nom + "12345".substring(nom.length(), 5);
        return base.toLowerCase() + "prof";
    }

    /**
     * Convertit une entité Professeur en ProfesseurResponseDTO.
     */
    private ProfesseurResponseDTO mapToResponseDTO(Professeur professeur) {
        User user = professeur.getUser();
        return new ProfesseurResponseDTO(
                professeur.getId(),
                professeur.getNom(),
                professeur.getPrenom(),
                professeur.getTelephone(),
                professeur.getDiplome(),
                professeur.getAnneeExperience(),
                professeur.getLicenceUrl(),
                professeur.getImageUrl(),
                user != null ? user.getId() : null,
                user != null ? user.getUsername() : null,
                user != null ? user.getEmail() : null,
                professeur.getCreatedAt(),
                professeur.getUpdatedAt(),
                professeur.getCreatedBy(),
                professeur.getUpdatedBy()
        );
    }
}