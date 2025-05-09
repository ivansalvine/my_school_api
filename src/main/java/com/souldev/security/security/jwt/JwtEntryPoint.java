package com.souldev.security.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souldev.security.payload.response.CustomApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);
    private final ObjectMapper objectMapper;

    public JwtEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Erreur d'authentification : {}", authException.getMessage());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        CustomApiResponse apiResponse = new CustomApiResponse(
                false, "Accès non autorisé : " + authException.getMessage(), null, 401);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}