package com.souldev.security;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import com.souldev.security.entities.Configuration;
import com.souldev.security.repositories.ConfigurationRepository;
import com.souldev.security.security.entities.Role;
import com.souldev.security.security.entities.User;
import com.souldev.security.security.enums.RoleList;
import com.souldev.security.security.respositories.RoleRepository;
import com.souldev.security.security.respositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class Initializer {

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    private static final Map<String, String> DEFAULT_PATHS = new HashMap<>();

    static {
        DEFAULT_PATHS.put("file_path", "C:/my-school/file");
        DEFAULT_PATHS.put("report_path", "C:/my-school/reports");
        DEFAULT_PATHS.put("jasper_path", "C:/my-school/jaspers");
        DEFAULT_PATHS.put("logs_path", "C:/my-school/logs");
    }

    private final ConfigurationRepository configurationRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public Initializer(ConfigurationRepository configurationRepository, RoleRepository roleRepository,
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.configurationRepository = configurationRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initialize() {
        logger.info("Début de l'initialisation de l'application");
        try {
            initializeConfigurations();
            createDirectories();
            configureLogging();
            initializeRoles();
            initializeSuperAdmin();
            logger.info("Initialisation terminée avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de l'application : {}", e.getMessage(), e);
        }
    }

    private void initializeConfigurations() {
        logger.debug("Initialisation des configurations");
        for (Map.Entry<String, String> entry : DEFAULT_PATHS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                if (!configurationRepository.existsById(key)) {
                    configurationRepository.save(new Configuration(key, value));
                    logger.info("Configuration initialisée : {} = {}", key, value);
                } else {
                    logger.info("Configuration existante : {}", key);
                }
            } catch (Exception e) {
                logger.error("Erreur lors de l'initialisation de la configuration {} : {}", key, e.getMessage(), e);
            }
        }
    }

    private void createDirectories() {
        logger.debug("Création des dossiers");
        configurationRepository.findAll().forEach(config -> {
            String path = config.getValue();
            File directory = new File(path);
            try {
                if (!directory.exists()) {
                    boolean created = directory.mkdirs();
                    if (created) {
                        logger.info("Dossier créé : {}", path);
                    } else {
                        logger.warn("Échec de la création du dossier : {}", path);
                    }
                } else {
                    logger.info("Dossier existant : {}", path);
                }
            } catch (Exception e) {
                logger.error("Erreur lors de la création du dossier {} : {}", path, e.getMessage(), e);
            }
        });
    }

    private void configureLogging() {
        logger.debug("Configuration des logs");
        String logsPath = configurationRepository.findById("logs_path")
                .map(Configuration::getValue)
                .orElse(DEFAULT_PATHS.get("logs_path"));

        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
            encoder.start();

            FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
            fileAppender.setContext(loggerContext);
            fileAppender.setFile(logsPath + "/my_school.log");
            fileAppender.setEncoder(encoder);
            fileAppender.setAppend(true);
            fileAppender.start();

            ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.DEBUG);
            rootLogger.addAppender(fileAppender);

            logger.info("Logs configurés pour écrire dans : {}", logsPath + "/my_school.log");
        } catch (Exception e) {
            logger.error("Erreur lors de la configuration des logs : {}", e.getMessage(), e);
        }
    }

    private void initializeRoles() {
        logger.debug("Initialisation des rôles");
        for (RoleList roleName : RoleList.values()) {
            try {
                String roleNameString = roleName.name(); // Convertir l'enum en String
                if (roleRepository.findByName(roleNameString).isEmpty()) {
                    Role role = new Role(roleName);
                    role.setId("role_" + roleName.name().toLowerCase());
                    roleRepository.save(role);
                    logger.info("Rôle créé : {}", roleName);
                } else {
                    logger.info("Rôle existant : {}", roleName);
                }
            } catch (Exception e) {
                logger.error("Erreur lors de la création du rôle {} : {}", roleName, e.getMessage(), e);
            }
        }
    }

    private void initializeSuperAdmin() {
        String adminUsername = "superadmin";
        String adminPassword = "VotreMot2Passe@Fort!123";
        String adminEmail = "superadmin@ecole.com";

        try {
            if (!userRepository.existsByUsername(adminUsername)) {
                User superAdmin = new User();
                // Ne pas définir l'ID manuellement - sera généré automatiquement
                superAdmin.setUsername(adminUsername);
                superAdmin.setPassword(passwordEncoder.encode(adminPassword));
                superAdmin.setEmail(adminEmail);
                superAdmin.setFullName("Super Administrateur");
                superAdmin.setEnabled(true);

                Role superAdminRole = roleRepository.findByName(RoleList.ROLE_SUPER_ADMIN.name())
                        .orElseThrow(() -> new RuntimeException("Rôle Super Admin non trouvé"));

                superAdmin.setRoles(Set.of(superAdminRole));
                userRepository.save(superAdmin);
                logger.info("Compte Super Admin initialisé avec succès");
            } else {
                logger.info("Compte Super Admin déjà existant");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation du Super Admin", e);
            throw new RuntimeException("Échec de l'initialisation du Super Admin", e);
        }
    }
}