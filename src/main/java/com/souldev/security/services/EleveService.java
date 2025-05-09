package com.souldev.security.services;

import com.souldev.security.entities.Classe;
import com.souldev.security.entities.Ecole;
import com.souldev.security.entities.Eleve;
import com.souldev.security.payload.request.EleveDTO;
import com.souldev.security.payload.response.EleveResponseDTO;
import com.souldev.security.repositories.ClasseRepository;
import com.souldev.security.repositories.EcoleRepository;
import com.souldev.security.repositories.EleveRepository;
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
public class EleveService {

    private static final Logger logger = LoggerFactory.getLogger(EleveService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final String DEFAULT_FILE_PATH = "C:/my-school/file";
    private static final String FILE_PATH_KEY = "file_path";

    private final EleveRepository eleveRepository;
    private final UserRepository userRepository;
    private final ClasseRepository classeRepository;
    private final EcoleRepository ecoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfigurationService configurationService;
    private final String fileStoragePath;

    @Autowired
    public EleveService(EleveRepository eleveRepository, UserRepository userRepository,
                        ClasseRepository classeRepository, EcoleRepository ecoleRepository,
                        RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                        ConfigurationService configurationService) {
        this.eleveRepository = eleveRepository;
        this.userRepository = userRepository;
        this.classeRepository = classeRepository;
        this.ecoleRepository = ecoleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.configurationService = configurationService;

        // Récupérer le chemin de stockage depuis la configuration
        this.fileStoragePath = configurationService.getValueOrDefault(FILE_PATH_KEY, DEFAULT_FILE_PATH);
        logger.info("Répertoire de stockage des fichiers configuré : {}", this.fileStoragePath);
    }

    public Ecole getEcole() {
        logger.info("Récupération de l’école unique");
        List<Ecole> ecoles = ecoleRepository.findAll();
        if (ecoles.isEmpty()) {
            logger.error("Aucune école trouvée");
            throw new EntityNotFoundException("Aucune école trouvée");
        }
        return ecoles.get(0);
    }

    private String generateCodeAcces(Classe classe) {
        // Vérification de l'année scolaire et de la date de fin
        if (classe.getAnneeScolaire() == null || classe.getAnneeScolaire().getDateFin() == null) {
            logger.error("Échec : L’année scolaire ou la date de fin est manquante pour la classe ID : {}", classe.getId());
            throw new IllegalArgumentException("L’année scolaire ou la date de fin est requise pour générer le code d’accès");
        }

        // Compter le nombre d'élèves existants dans la classe + 1
        long nombreEleves = eleveRepository.countByClasseId(classe.getId()) + 1;

        // Nettoyer le nom de la classe pour éviter les caractères spéciaux
        String cleanNomClasse = classe.getNom().replaceAll("[^a-zA-Z0-9]", "_");

        // Générer le code d'accès : nomClasse_nombreEleve/annee
        String codeAcces = String.format("%s_%d/%d", cleanNomClasse, nombreEleves, classe.getAnneeScolaire().getDateFin().getYear());

        // Vérifier la longueur du code d'accès
        if (codeAcces.length() > 50) {
            logger.error("Échec : Le code d’accès dépasse 50 caractères : {}", codeAcces);
            throw new IllegalArgumentException("Le code d’accès dépasse la limite de 50 caractères");
        }

        // Vérifier l'unicité du code d'accès
        if (eleveRepository.findByCodeAcces(codeAcces).isPresent()) {
            logger.error("Échec : Le code d’accès {} existe déjà", codeAcces);
            throw new IllegalArgumentException("Le code d’accès existe déjà");
        }

        return codeAcces;
    }

    @Transactional
    public EleveResponseDTO createEleve(EleveDTO eleveDTO, MultipartFile photoFile) {
        logger.info("Tentative de création d’un étudiant : {} {}", eleveDTO.getNom(), eleveDTO.getPrenom());

        // Vérification de l'unicité du numéro d'étudiant
        if (eleveRepository.findByNumeroEtudiant(eleveDTO.getNumeroEtudiant()).isPresent()) {
            logger.error("Échec : Le numéro d’étudiant {} existe déjà", eleveDTO.getNumeroEtudiant());
            throw new IllegalArgumentException("Le numéro d’étudiant existe déjà");
        }

        // Récupération de la classe
        Classe classe = classeRepository.findById(eleveDTO.getClasseId())
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l’ID : {}", eleveDTO.getClasseId());
                    return new EntityNotFoundException("Classe non trouvée avec l’ID : " + eleveDTO.getClasseId());
                });

        // Gestion de la photo
        String photoUrl = photoFile != null ? handleFileUpload(photoFile, eleveDTO.getNom(), "photo"): "";

        // Création de l’entité Eleve
        Eleve eleve = new Eleve();
        eleve.setNom(eleveDTO.getNom());
        eleve.setPrenom(eleveDTO.getPrenom());
        eleve.setNumeroEtudiant(eleveDTO.getNumeroEtudiant());
        eleve.setDateNaissance(eleveDTO.getDateNaissance());
        eleve.setTelephone(eleveDTO.getTelephone());
        eleve.setClasse(classe);
        eleve.setPhotoUrl(photoUrl);

        // Génération du code d'accès
        eleve.setCodeAcces(generateCodeAcces(classe));

        // Configuration des autres champs
        Ecole ecole = getEcole();

        // Création du compte User
        User user = new User();
        user.setUsername(eleveDTO.getNom() + "@" + ecole.getDomaineName());

        // Génération du mot de passe sécurisé
        String rawPassword = generatePassword(eleveDTO.getNom(), String.valueOf(classe.getAnneeScolaire().getDateFin().getYear()));
        user.setPassword(passwordEncoder.encode(rawPassword));

        eleve.setPassword(rawPassword);
        //user.setEmail(eleveDTO.getNumeroEtudiant() + "@" + ecole.getDomaineName());
        user.setEmail(eleveDTO.getEmail());
        user.setFullName(eleveDTO.getPrenom() + " " + eleveDTO.getNom());
        user.setEnabled(true);

        // Attribution du rôle ROLE_STUDENT
        Role studentRole = roleRepository.findByName(RoleList.ROLE_STUDENT.name())
                .orElseThrow(() -> {
                    logger.error("Rôle ROLE_STUDENT non trouvé");
                    return new EntityNotFoundException("Rôle ROLE_STUDENT non trouvé");
                });
        user.setRoles(new HashSet<>() {{
            add(studentRole);
        }});

        // Enregistrement de l’utilisateur
        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès : {}", savedUser.getUsername());

        // Association
        eleve.setUser(savedUser);

        // Enregistrement de l’étudiant
        Eleve savedEleve = eleveRepository.save(eleve);
        logger.info("Étudiant créé avec succès : ID {}", savedEleve.getId());
        return mapToResponseDTO(savedEleve);
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
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Fichier {} téléchargé avec succès : {}", fileType, filePath);
            return fileName;
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement du fichier {} : {}", fileType, e.getMessage());
            throw new RuntimeException("Erreur lors du téléchargement du fichier " + fileType, e);
        }
    }

    private String generatePassword(String nom, String nomClasse) {
        String base = nom.length() >= 5 ? nom.substring(0, 5) : nom + "12345".substring(nom.length(), 5);
        return base.toLowerCase() + nomClasse.toLowerCase();
    }

    public EleveResponseDTO getEleveById(String id) {
        logger.info("Recherche de l’étudiant avec l’ID : {}", id);
        Eleve eleve = eleveRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Étudiant non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Étudiant non trouvé avec l’ID : " + id);
                });
        return mapToResponseDTO(eleve);
    }

    public List<EleveResponseDTO> getAllEleves() {
        logger.info("Récupération de tous les étudiants");
        List<Eleve> eleves = eleveRepository.findAll();
        logger.info("Nombre d’étudiants récupérés : {}", eleves.size());
        return eleves.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EleveResponseDTO> getElevesByClasse(String classeId) {
        logger.info("Récupération des étudiants pour la classe ID : {}", classeId);
        List<Eleve> eleves = eleveRepository.findByClasseId(classeId);
        logger.info("Nombre d’étudiants trouvés pour la classe : {}", eleves.size());
        return eleves.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EleveResponseDTO> getAllElevesByEcole() {
        logger.info("Récupération des étudiants pour l’école unique");
        List<Eleve> eleves = eleveRepository.findAll();
        logger.info("Nombre d’étudiants trouvés pour l’école : {}", eleves.size());
        return eleves.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public EleveResponseDTO getEleveByUserId(String userId) {
        logger.info("Recherche de l’étudiant par ID utilisateur : {}", userId);
        Eleve eleve = eleveRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Aucun étudiant trouvé pour l’utilisateur ID : {}", userId);
                    return new EntityNotFoundException("Aucun étudiant trouvé pour l’utilisateur ID : " + userId);
                });
        return mapToResponseDTO(eleve);
    }

    public EleveResponseDTO getEleveByNumeroEtudiant(String numeroEtudiant) {
        logger.info("Recherche de l’étudiant par numéro : {}", numeroEtudiant);
        Eleve eleve = eleveRepository.findByNumeroEtudiant(numeroEtudiant)
                .orElseThrow(() -> {
                    logger.error("Aucun étudiant trouvé pour le numéro : {}", numeroEtudiant);
                    return new EntityNotFoundException("Aucun étudiant trouvé pour le numéro : " + numeroEtudiant);
                });
        return mapToResponseDTO(eleve);
    }

    @Transactional
    public EleveResponseDTO updateEleve(String eleveId, EleveDTO eleveDTO, MultipartFile photoFile) {
        logger.info("Tentative de mise à jour de l’étudiant avec ID : {}", eleveId);

        // Récupération de l’étudiant existant
        Eleve existingEleve = eleveRepository.findById(eleveId)
                .orElseThrow(() -> {
                    logger.error("Étudiant non trouvé avec l’ID : {}", eleveId);
                    return new EntityNotFoundException("Étudiant non trouvé avec l’ID : " + eleveId);
                });

        // Vérification de l'unicité du numéro d'étudiant
        if (!eleveDTO.getNumeroEtudiant().equals(existingEleve.getNumeroEtudiant()) &&
                eleveRepository.findByNumeroEtudiant(eleveDTO.getNumeroEtudiant()).isPresent()) {
            logger.error("Échec : Le numéro d’étudiant {} existe déjà", eleveDTO.getNumeroEtudiant());
            throw new IllegalArgumentException("Le numéro d’étudiant existe déjà");
        }

        // Récupération de la classe
        Classe classe = classeRepository.findById(eleveDTO.getClasseId())
                .orElseThrow(() -> {
                    logger.error("Classe non trouvée avec l’ID : {}", eleveDTO.getClasseId());
                    return new EntityNotFoundException("Classe non trouvée avec l’ID : " + eleveDTO.getClasseId());
                });

        // Gestion de la photo
        String photoUrl = handleFileUpload(photoFile, eleveDTO.getNom(), "photo");

        // Suppression de l'ancienne photo si une nouvelle est fournie
        if (photoUrl != null && existingEleve.getPhotoUrl() != null) {
            try {
                String filePath = fileStoragePath + "/" + existingEleve.getPhotoUrl();
                Files.deleteIfExists(Paths.get(filePath));
                logger.info("Ancienne photo supprimée : {}", filePath);
            } catch (IOException e) {
                logger.warn("Impossible de supprimer l'ancienne photo : {}", e.getMessage());
            }
        }

        // Mise à jour des champs de l’étudiant
        existingEleve.setNom(eleveDTO.getNom());
        existingEleve.setPrenom(eleveDTO.getPrenom());
        existingEleve.setNumeroEtudiant(eleveDTO.getNumeroEtudiant());
        existingEleve.setDateNaissance(eleveDTO.getDateNaissance());
        existingEleve.setTelephone(eleveDTO.getTelephone());
        existingEleve.setClasse(classe);
        if (photoUrl != null) {
            existingEleve.setPhotoUrl(photoUrl);
        }

        // Mise à jour du code d'accès si la classe a changé
        if (!existingEleve.getClasse().getId().equals(classe.getId())) {
            existingEleve.setCodeAcces(generateCodeAcces(classe));
        }

        // Mise à jour du compte User associé
        User user = existingEleve.getUser();
        if (user == null) {
            logger.error("Échec : Aucun compte utilisateur associé à l’étudiant");
            throw new EntityNotFoundException("Aucun compte utilisateur associé à l’étudiant");
        }

        // Mise à jour de l’email et fullName
        Ecole ecole = getEcole();
        user.setUsername(eleveDTO.getNom().toLowerCase() + "@" + ecole.getDomaineName());
        //user.setEmail(eleveDTO.getNom() + "@" + ecole.getDomaineName());
        user.setEmail(eleveDTO.getNom().toLowerCase() + "@" + ecole.getDomaineName());
        user.setFullName(eleveDTO.getPrenom() + " " + eleveDTO.getNom());

        // Enregistrement du User mis à jour
        userRepository.save(user);
        logger.info("Utilisateur mis à jour avec succès : {}", user.getUsername());

        // Enregistrement de l’étudiant mis à jour
        Eleve updatedEleve = eleveRepository.save(existingEleve);
        logger.info("Étudiant mis à jour avec succès : ID {}", updatedEleve.getId());
        return mapToResponseDTO(updatedEleve);
    }

    @Transactional
    public void deleteEleve(String id) {
        logger.info("Tentative de suppression de l’étudiant ID : {}", id);
        Eleve eleve = eleveRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Étudiant non trouvé avec l’ID : {}", id);
                    return new EntityNotFoundException("Étudiant non trouvé avec l’ID : " + id);
                });

        // Suppression de la photo associée
        if (eleve.getPhotoUrl() != null) {
            try {
                String filePath = fileStoragePath + "/" + eleve.getPhotoUrl();
                Files.deleteIfExists(Paths.get(filePath));
                logger.info("Photo supprimée : {}", filePath);
            } catch (IOException e) {
                logger.warn("Impossible de supprimer la photo : {}", e.getMessage());
            }
        }

        // Suppression de l'utilisateur associé
        User user = eleve.getUser();
        if (user != null) {
            userRepository.delete(user);
            logger.info("Utilisateur associé supprimé : {}", user.getUsername());
        }

        // Suppression de l’étudiant
        eleveRepository.delete(eleve);
        logger.info("Étudiant supprimé avec succès : ID {}", id);
    }

    private EleveResponseDTO mapToResponseDTO(Eleve eleve) {
        User user = eleve.getUser();
        return new EleveResponseDTO(
                eleve.getId(),
                eleve.getNom(),
                eleve.getPrenom(),
                eleve.getNumeroEtudiant(),
                eleve.getDateNaissance(),
                eleve.getCodeAcces(),
                eleve.getTelephone(),
                eleve.getClasse() != null ? eleve.getClasse().getId() : null,
                eleve.getPhotoUrl(),
                user != null ? user.getId() : null,
                user != null ? user.getUsername() : null,
                user != null ? user.getEmail() : null,
                eleve.getPassword(),
                eleve.getCreatedAt(),
                eleve.getUpdatedAt(),
                eleve.getCreatedBy(),
                eleve.getUpdatedBy()
        );
    }
}