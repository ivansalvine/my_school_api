package com.souldev.security.security.jwt;

import com.souldev.security.payload.response.CustomApiResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    private final Key signingKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public JwtProvider(@Value("${jwt.secret}") String jwtSecret) {
        try {
            // Décoder la clé Base64 et créer une clé sécurisée
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            logger.error("Clé secrète JWT invalide : {}", e.getMessage());
            throw new RuntimeException("Clé secrète JWT invalide", e);
        }
    }

    public String getUserNameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Erreur lors de l'extraction du username depuis le token : {}", e.getMessage());
            return null;
        }
    }

    public CustomApiResponse validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return new CustomApiResponse(true, "Token valide", null, 200);
        } catch (SignatureException e) {
            logger.error("Signature JWT invalide : {}", e.getMessage());
            return new CustomApiResponse(false, "Signature JWT invalide", null, 401);
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformé : {}", e.getMessage());
            return new CustomApiResponse(false, "Token JWT malformé", null, 401);
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré : {}", e.getMessage());
            return new CustomApiResponse(false, "Token JWT expiré", null, 401);
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté : {}", e.getMessage());
            return new CustomApiResponse(false, "Token JWT non supporté", null, 401);
        } catch (IllegalArgumentException e) {
            logger.error("Token JWT vide ou invalide : {}", e.getMessage());
            return new CustomApiResponse(false, "Token JWT vide ou invalide", null, 401);
        }
    }

    public String generateToken(String username) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new java.util.Date())
                    .setExpiration(new java.util.Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(signingKey, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du token : {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la génération du token", e);
        }
    }
}