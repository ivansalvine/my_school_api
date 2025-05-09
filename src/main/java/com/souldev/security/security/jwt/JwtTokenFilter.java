package com.souldev.security.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.security.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    private final JwtProvider jwtProvider;
    private final UserService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtTokenFilter(JwtProvider jwtProvider, UserService userDetailsService, ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token == null) {
            logger.debug("Aucun token JWT trouvé dans la requête : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            CustomApiResponse validationResponse = jwtProvider.validateToken(token);
            if (!validationResponse.getSuccess()) {
                logger.warn("Token JWT invalide pour la requête {} : {}", request.getRequestURI(), validationResponse.getMessage());
                sendErrorResponse(response, validationResponse);
                return;
            }

            authenticateUser(token, request);
            logger.debug("Utilisateur authentifié avec succès pour la requête : {}", request.getRequestURI());
            filterChain.doFilter(request, response);

        } catch (UsernameNotFoundException ex) {
            logger.warn("Utilisateur non trouvé pour le token : {}", ex.getMessage());
            sendErrorResponse(response, new CustomApiResponse(false, "Compte utilisateur introuvable", null, 401));
            return;
        } catch (Exception ex) {
            logger.error("Erreur d'authentification pour la requête {} : {}", request.getRequestURI(), ex.getMessage(), ex);
            sendErrorResponse(response,
                    new CustomApiResponse(false, "Erreur d'authentification : " + ex.getMessage(), null, 401));
            return;
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            logger.debug("Token extrait : {}", token);
            return token;
        }
        return null;
    }

    private void authenticateUser(String token, HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.debug("Utilisateur déjà authentifié, aucune nouvelle authentification nécessaire.");
            return;
        }

        String username = jwtProvider.getUserNameFromToken(token);
        logger.debug("Appel de loadUserByUsername pour : {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        logger.debug("Utilisateur chargé : {}", username);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void sendErrorResponse(HttpServletResponse response,
                                   CustomApiResponse apiResponse) throws IOException {
        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatutCode());
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}