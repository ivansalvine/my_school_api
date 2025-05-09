package com.souldev.security.security.services;

import com.souldev.security.entities.Direction;
import com.souldev.security.entities.Eleve;
import com.souldev.security.entities.Professeur;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.UserResponse;
import com.souldev.security.repositories.DirectionRepository;
import com.souldev.security.repositories.EleveRepository;
import com.souldev.security.repositories.ProfesseurRepository;
import com.souldev.security.security.dtos.LoginUser;
import com.souldev.security.security.entities.User;
import com.souldev.security.security.enums.RoleList;
import com.souldev.security.security.jwt.JwtProvider;
import com.souldev.security.security.respositories.UserRepository;
import com.souldev.security.services.DirectionService;
import com.souldev.security.services.ProfesseurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ProfesseurRepository professeurRepository;
    private final DirectionRepository directionRepository;
    private final RoleService roleService;
    private final ProfesseurService professeurService;
    private final DirectionService directionService;
    private final PasswordEncoder passwordEncoder;
    private final EleveRepository eleveRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            JwtProvider jwtProvider,
            ProfesseurRepository professeurRepository,
            DirectionRepository directionRepository,
            RoleService roleService,
            ProfesseurService professeurService,
            DirectionService directionService,
            PasswordEncoder passwordEncoder,
            EleveRepository eleveRepository,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.professeurRepository = professeurRepository;
        this.directionRepository = directionRepository;
        this.roleService = roleService;
        this.professeurService = professeurService;
        this.directionService = directionService;
        this.passwordEncoder = passwordEncoder;
        this.eleveRepository = eleveRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse login(LoginUser loginUser) {
        logger.debug("Tentative de connexion pour l'utilisateur : {}", loginUser.getUserName());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUserName(),
                            loginUser.getPassword()
                    )
            );
            logger.debug("Authentification réussie pour : {}", loginUser.getUserName());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String username = authentication.getName();
            logger.debug("Nom d'utilisateur extrait : {}", username);
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("Utilisateur non trouvé après authentification");
            }
            logger.debug("Utilisateur chargé : {}", optionalUser.get().getFullName());

            String jwt = jwtProvider.generateToken(username);
            logger.debug("Token généré : {}", jwt);

            String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .findFirst()
                    .orElse("ROLE_USER");

            logger.debug("Connexion réussie pour : {}, rôles : {}", username,
                    authentication.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(", ")));
            return new UserResponse(
                    true,
                    "Connexion réussie pour : " + username,
                    jwt,
                    username,
                    optionalUser.get().getFullName(),
                    role
            );
        } catch (BadCredentialsException e) {
            logger.warn("Mauvais identifiants pour : {}", loginUser.getUserName());
            throw new RuntimeException("Nom d'utilisateur ou mot de passe incorrect");
        } catch (AuthenticationException e) {
            logger.warn("Échec de l'authentification pour : {}", loginUser.getUserName());
            throw new RuntimeException("Échec de l'authentification : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la connexion pour : {}", loginUser.getUserName(), e);
            throw new RuntimeException("Erreur lors de la connexion : " + e.getMessage());
        }
    }

    @Transactional
    public CustomApiResponse registerEntity(Eleve eleve, String password) {
        if (userRepository.existsByUsername(eleve.getUser().getUsername())) {
            return new CustomApiResponse(false, "Nom d'utilisateur déjà pris", null, 400);
        }
        if (userRepository.existsByEmail(eleve.getUser().getEmail())) {
            return new CustomApiResponse(false, "Email déjà utilisé", null, 400);
        }

        User user = eleve.getUser();
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRoles(Set.of(roleService.getByRoleName(RoleList.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Rôle étudiant non trouvé"))));
        userRepository.save(user);
        eleve.setUser(user);
        eleveRepository.save(eleve);

        return new CustomApiResponse(true, "Étudiant inscrit avec succès", null, 200);
    }

    @Transactional
    public CustomApiResponse registerEntity(Professeur professeur, String password) {
        if (userRepository.existsByUsername(professeur.getUser().getUsername())) {
            return new CustomApiResponse(false, "Nom d'utilisateur déjà pris", null, 400);
        }
        if (userRepository.existsByEmail(professeur.getUser().getEmail())) {
            return new CustomApiResponse(false, "Email déjà utilisé", null, 400);
        }

        User user = professeur.getUser();
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRoles(Set.of(roleService.getByRoleName(RoleList.ROLE_TEACHER)
                .orElseThrow(() -> new RuntimeException("Rôle professeur non trouvé"))));
        userRepository.save(user);
        professeur.setUser(user);
        professeurRepository.save(professeur);

        return new CustomApiResponse(true, "Professeur inscrit avec succès", null, 200);
    }

    @Transactional
    public CustomApiResponse registerEntity(Direction direction, String password) {
        if (userRepository.existsByUsername(direction.getUser().getUsername())) {
            return new CustomApiResponse(false, "Nom d'utilisateur déjà pris", null, 400);
        }
        if (userRepository.existsByEmail(direction.getUser().getEmail())) {
            return new CustomApiResponse(false, "Email déjà utilisé", null, 400);
        }

        User user = direction.getUser();
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRoles(Set.of(roleService.getByRoleName(RoleList.ROLE_DIRECTOR)
                .orElseThrow(() -> new RuntimeException("Rôle direction non trouvé"))));
        userRepository.save(user);
        direction.setUser(user);
        directionRepository.save(direction);

        return new CustomApiResponse(true, "Direction inscrite avec succès", null, 200);
    }
}