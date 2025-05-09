package com.souldev.security.services;

import com.souldev.security.entities.*;
import com.souldev.security.payload.request.EcoleCreateDTO;
import com.souldev.security.payload.request.EcoleDTO;
import com.souldev.security.payload.request.EcoleUpdateDTO;
import com.souldev.security.repositories.ConfigurationRepository;
import com.souldev.security.repositories.DirectionRepository;
import com.souldev.security.repositories.EcoleRepository;
import com.souldev.security.security.respositories.RoleRepository;
import com.souldev.security.security.respositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class EcoleService {

    private static final Logger logger = LoggerFactory.getLogger(EcoleService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private final EcoleRepository ecoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DirectionRepository directionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfigurationRepository configurationRepository;
    //private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    public EcoleService(EcoleRepository ecoleRepository, UserRepository userRepository, RoleRepository roleRepository, DirectionRepository directionRepository, PasswordEncoder passwordEncoder, ConfigurationRepository configurationRepository) {
        this.ecoleRepository = ecoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.directionRepository = directionRepository;
        this.passwordEncoder = passwordEncoder;
        this.configurationRepository = configurationRepository;
    }

    // Créer une nouvelle école
    @Transactional
    public Ecole createEcole(EcoleCreateDTO ecoleCreateDTO) throws IOException {
        logger.info("Tentative de création d'une école : {}", ecoleCreateDTO.getNom());

        // Validations des champs obligatoires
        validateRequiredFields(ecoleCreateDTO);

        // Vérifications d'unicité
        checkUniquenessConstraints(ecoleCreateDTO);

        // Conversion du DTO en entité
        Ecole ecole = mapDtoToEntity(ecoleCreateDTO);

        // Gestion du logo optionnel
        if (ecoleCreateDTO.getLogo() != null && !ecoleCreateDTO.getLogo().isEmpty()) {
            String logoPath = saveLogoFile(ecoleCreateDTO.getLogo());
            ecole.setLogoUrl(logoPath);
        }

        System.err.println("on arrive ici -----------------------------");
        // Sauvegarde de l'école
        Ecole savedEcole = ecoleRepository.save(ecole);
        logger.info("École créée avec succès : ID {}", savedEcole.getId());
        return savedEcole;
    }

    private void validateRequiredFields(EcoleCreateDTO dto) {
        if (dto.getNom() == null || dto.getNom().isEmpty()) {
            logger.error("Échec : Le nom de l'école est requis");
            throw new IllegalArgumentException("Le nom de l'école est requis");
        }
        if (dto.getAdresse() == null || dto.getAdresse().isEmpty()) {
            logger.error("Échec : L'adresse de l'école est requise");
            throw new IllegalArgumentException("L'adresse de l'école est requise");
        }
        if (dto.getTelephone() == null || dto.getTelephone().isEmpty()) {
            logger.error("Échec : Le numéro de téléphone de l'école est requise");
            throw new IllegalArgumentException("Le numéro de téléphone de l'école est requise");
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            logger.error("Échec : L'email de l'école est requis");
            throw new IllegalArgumentException("L'email de l'école est requis");
        }
        if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            logger.error("Échec : L'email de l'école est invalide");
            throw new IllegalArgumentException("L'email de l'école est invalide");
        }
        if (dto.getDomaineName() == null || dto.getDomaineName().isEmpty()) {
            logger.error("Échec : Le nom de domaine de l'école est requis");
            throw new IllegalArgumentException("Le nom de domaine de l'école est requis");
        }
    }

    private void checkUniquenessConstraints(EcoleCreateDTO dto) {
        if (ecoleRepository.existsByNom(dto.getNom())) {
            logger.error("Échec : Une école avec le nom {} existe déjà", dto.getNom());
            throw new IllegalArgumentException("Une école avec ce nom existe déjà");
        }
        if (ecoleRepository.existsByEmail(dto.getEmail())) {
            logger.error("Échec : Une école avec l'email {} existe déjà", dto.getEmail());
            throw new IllegalArgumentException("Une école avec cet email existe déjà");
        }
        if (ecoleRepository.existsByDomaineName(dto.getDomaineName())) {
            logger.error("Échec : Une école avec le domaine {} existe déjà", dto.getDomaineName());
            throw new IllegalArgumentException("Une école avec ce domaine existe déjà");
        }
    }

    private String saveLogoFile(String base64Logo) throws IOException {
        try {
            Optional<Configuration> uploadDirConfig = configurationRepository.findByKey("file_path");
            if (uploadDirConfig.isEmpty()) {
                logger.error("Répertoire d'enregistrement des fichiers non configuré");
                throw new IllegalStateException("Le répertoire d'enregistrement des fichiers n'est pas configuré");
            }

            // Valider la chaîne Base64
            if (base64Logo == null || base64Logo.isEmpty()) {
                logger.warn("Aucun logo fourni");
                return null;
            }
            if (!base64Logo.matches("^(data:image/(png|jpeg);base64,)?[A-Za-z0-9+/=]+$")) {
                logger.error("Format Base64 invalide pour le logo");
                throw new IllegalArgumentException("Format Base64 invalide pour le logo");
            }

            // Supprimer le préfixe MIME si présent
            String base64Image = base64Logo;
            if (base64Logo.contains(",")) {
                base64Image = base64Logo.split(",")[1];
            }

            // Décoder la chaîne Base64
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

            // Générer un nom de fichier unique
            String fileName = "logo_" + UUID.randomUUID() + ".png";
            Path uploadPath = Paths.get(uploadDirConfig.get().getValue());
            if (!Files.exists(uploadPath)) {
                logger.info("Création du répertoire : {}", uploadPath);
                Files.createDirectories(uploadPath);
            }
            if (!Files.isWritable(uploadPath)) {
                logger.error("Répertoire non accessible en écriture : {}", uploadPath);
                throw new IllegalStateException("Répertoire non accessible en écriture");
            }

            // Enregistrer le fichier
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, decodedBytes);

            logger.info("Logo enregistré avec succès : {}", filePath);
            return fileName; // Retourner uniquement le nom du fichier
        } catch (IllegalArgumentException e) {
            logger.error("Échec lors du décodage du logo Base64 : {}", e.getMessage());
            throw new IllegalArgumentException("La chaîne Base64 du logo est invalide", e);
        } catch (IOException e) {
            logger.error("Échec lors de l'enregistrement du logo : {}", e.getMessage());
            throw new IOException("Erreur lors de l'enregistrement du logo", e);
        }
    }

    private Ecole mapDtoToEntity(EcoleCreateDTO dto) {
        Ecole ecole = new Ecole();
        ecole.setNom(dto.getNom());
        ecole.setAdresse(dto.getAdresse());
        ecole.setEmail(dto.getEmail());
        ecole.setDomaineName(dto.getDomaineName());
        ecole.setTelephone(dto.getTelephone());
        return ecole;
    }

    // Récupérer une école par ID
    public Optional<Ecole> getEcoleById(String id) {
        return ecoleRepository.findById(id);
    }

    // Récupérer toutes les écoles
    public List<EcoleDTO> getAllEcoles() {
        List<Ecole> ecoles = ecoleRepository.findAll();
        return toDtoList(ecoles);
    }

    // Mettre à jour une école
    @Transactional
    public Ecole updateEcoleLogo(String ecoleId, String base64Logo) {
        Ecole ecole = ecoleRepository.findById(ecoleId)
                .orElseThrow(() -> new IllegalArgumentException("École non trouvée"));

        // Supprimer l'ancien logo s'il existe
        if (ecole.getLogoUrl() != null) {
            try {
                Files.deleteIfExists(Paths.get(ecole.getLogoUrl()));
            } catch (IOException e) {
                logger.warn("Échec suppression ancien logo: {}", e.getMessage());
            }
        }

        // Enregistrer le nouveau logo
        if (base64Logo != null && !base64Logo.isEmpty()) {
            try {
                String logoPath = saveLogoFile(base64Logo); // Utilise la méthode corrigée
                ecole.setLogoUrl(logoPath);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement du logo", e);
            }
        } else {
            ecole.setLogoUrl(null);
        }

        return ecoleRepository.save(ecole);
    }

    private void validateUpdateFields(EcoleUpdateDTO dto) {
        if (dto.getNom() != null && dto.getNom().isEmpty()) {
            logger.error("Échec : Le nom ne peut pas être vide");
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }

        if (dto.getTelephone() != null && dto.getTelephone().isEmpty()) {
            logger.error("Échec : Le numéro de téléphone ne peut pas être vide");
            throw new IllegalArgumentException("Le numéro de téléphone ne peut pas être vide");
        }

        if (dto.getEmail() != null) {
            if (dto.getEmail().isEmpty()) {
                logger.error("Échec : L'email ne peut pas être vide");
                throw new IllegalArgumentException("L'email ne peut pas être vide");
            }
            if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
                logger.error("Échec : L'email est invalide");
                throw new IllegalArgumentException("L'email est invalide");
            }
        }
    }

    private void checkUniquenessForUpdate(String ecoleId, EcoleUpdateDTO dto) {
        if (dto.getNom() != null) {
            ecoleRepository.findByNomAndIdNot(dto.getNom(), ecoleId)
                    .ifPresent(e -> {
                        logger.error("Échec : Une autre école avec le nom {} existe déjà", dto.getNom());
                        throw new IllegalArgumentException("Ce nom est déjà utilisé par une autre école");
                    });
        }

        if (dto.getEmail() != null) {
            ecoleRepository.findByNomAndIdNot(dto.getEmail(), ecoleId)
                    .ifPresent(e -> {
                        logger.error("Échec : Une autre école avec l'email {} existe déjà", dto.getEmail());
                        throw new IllegalArgumentException("Cet email est déjà utilisé par une autre école");
                    });
        }

        if (dto.getDomaineName() != null) {
            ecoleRepository.findByDomaineNameAndIdNot(dto.getDomaineName(), ecoleId)
                    .ifPresent(e -> {
                        logger.error("Échec : Une autre école avec le domaine {} existe déjà", dto.getDomaineName());
                        throw new IllegalArgumentException("Ce domaine est déjà utilisé par une autre école");
                    });
        }
    }

    private void updateEcoleFields(Ecole ecole, EcoleUpdateDTO dto) {
        if (dto.getNom() != null) {
            ecole.setNom(dto.getNom());
        }
        if (dto.getAdresse() != null) {
            ecole.setAdresse(dto.getAdresse());
        }
        if (dto.getEmail() != null) {
            ecole.setEmail(dto.getEmail());
        }
        if (dto.getTelephone() != null) {
            ecole.setTelephone(dto.getTelephone());
        }
        if (dto.getDomaineName() != null) {
            ecole.setDomaineName(dto.getDomaineName());
        }
    }

    private void handleLogoUpdate(EcoleUpdateDTO dto, Ecole ecole) {
        if (dto.getLogo() != null) {
            // Si nouveau logo fourni
            if (!dto.getLogo().isEmpty()) {
                // Supprimer l'ancien logo s'il existe
                deleteExistingLogo(ecole.getLogoUrl());

                // Enregistrer le nouveau logo
                String logoPath = saveLogoFile(dto.getLogo());
                ecole.setLogoUrl(logoPath);
                logger.info("Nouveau logo enregistré : {}", logoPath);
            }
            // Si logo est explicitement null (pour suppression)
            else if (dto.isDeleteLogo()) {
                deleteExistingLogo(ecole.getLogoUrl());
                ecole.setLogoUrl(null);
                logger.info("Logo supprimé");
            }
        }
    }

    private void deleteExistingLogo(String logoPath) {
        if (logoPath != null) {
            try {
                Path path = Paths.get(logoPath);
                if (Files.exists(path)) {
                    Files.delete(path);
                    logger.info("Ancien logo supprimé : {}", logoPath);
                }
            } catch (IOException e) {
                logger.error("Échec lors de la suppression de l'ancien logo : {}", e.getMessage());
                // On ne bloque pas l'opération pour une erreur de suppression
            }
        }
    }

    private String saveLogoFile(MultipartFile logoFile) {
        try {
            Optional<Configuration> uploadDirConfig = configurationRepository.findByKey("file_path");
            if (uploadDirConfig.isEmpty()) {
                logger.error("Répertoire d'enregistrement des fichiers non configuré");
                throw new IllegalStateException("Le répertoire d'enregistrement des fichiers n'est pas configuré");
            }

            String originalFilename = logoFile.getOriginalFilename();
            if (originalFilename == null) {
                logger.error("Nom du fichier logo invalide");
                throw new IllegalArgumentException("Le nom du fichier logo est invalide");
            }

            // Supprimer .txt ou .TXT du nom
            String baseName = originalFilename.replaceAll("(?i)\\.txt", "");
            String fileExtension = baseName.contains(".") ? baseName.substring(baseName.lastIndexOf(".")) : "";
            String fileName = "logo_" + UUID.randomUUID() + fileExtension;

            Path uploadPath = Paths.get(uploadDirConfig.get().getValue());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(logoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Logo enregistré avec succès : {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            logger.error("Échec lors de l'enregistrement du logo : {}", e.getMessage());
            throw new RuntimeException("Erreur lors de l'enregistrement du logo", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Ecole> getAllEcoles(int page, int size, String[] sort, String search) {
        Sort sorting = Sort.by(createSortOrder(sort));
        Pageable pageable = PageRequest.of(page, size, sorting);

        if (search != null && !search.isEmpty()) {
            return ecoleRepository.findBySearchCriteria(search, pageable);
        }
        return ecoleRepository.findAll(pageable);
    }

    private List<Sort.Order> createSortOrder(String[] sort) {
        return Arrays.stream(sort)
                .map(s -> {
                    String[] split = s.split(",");
                    return new Sort.Order(
                            split[1].equalsIgnoreCase("desc")
                                    ? Sort.Direction.DESC
                                    : Sort.Direction.ASC,
                            split[0]
                    );
                })
                .collect(Collectors.toList());
    }


    private String getFileExtension(String filename) {
        return filename != null && filename.contains(".")
                ? filename.substring(filename.lastIndexOf("."))
                : "";
    }

    @Transactional
    public void deleteEcole(String id) {
        logger.info("Tentative de suppression de l'école ID : {}", id);

        // 1. Vérifier l'existence de l'école
        Ecole ecole = ecoleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("École non trouvée avec l'ID : {}", id);
                    return new EntityNotFoundException("École non trouvée");
                });

        // 2. Vérifier les dépendances directes (Cycles et Salles)
        if (!ecole.getCycles().isEmpty()) {
            logger.error("Impossible de supprimer : l'école contient {} cycles", ecole.getCycles().size());
            throw new IllegalStateException("Impossible de supprimer : l'école contient des cycles");
        }
        if (!ecole.getSalles().isEmpty()) {
            logger.error("Impossible de supprimer : l'école contient {} salles", ecole.getSalles().size());
            throw new IllegalStateException("Impossible de supprimer : l'école contient des salles");
        }

        // 3. Vérifier les dépendances indirectes (exemple : Direction)
        if (directionRepository.existsByEcoleId(id)) {
            logger.error("Impossible de supprimer : l'école est associée à des directions");
            throw new IllegalStateException("Impossible de supprimer : l'école est associée à des directions");
        }

        // 4. Supprimer le fichier logo si existant
        if (ecole.getLogoUrl() != null) {
            try {
                Files.deleteIfExists(Paths.get(ecole.getLogoUrl()));
                logger.info("Fichier logo supprimé : {}", ecole.getLogoUrl());
            } catch (IOException e) {
                logger.warn("Échec de la suppression du logo : {}", e.getMessage());
                // Continuer malgré l'erreur
            }
        }

        // 5. Supprimer l'école
        ecoleRepository.delete(ecole);
        logger.info("École supprimée avec succès : ID {}", id);
    }


    private EcoleDTO toDto(Ecole ecole) {
        return new EcoleDTO(
                ecole.getId(),
                ecole.getNom(),
                ecole.getAdresse(),
                ecole.getTelephone(),
                ecole.getEmail(),
                ecole.getDomaineName(),
                ecole.getLogoUrl(),
                ecole.getCreatedAt(),
                ecole.getUpdatedAt(),
                ecole.getCreatedBy(),
                ecole.getUpdatedBy()
        );
    }

    public List<EcoleDTO> toDtoList(List<Ecole> ecoles) {
        return ecoles.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

