package com.souldev.security.security.controllers;

import com.souldev.security.payload.response.CustomApiResponse;
import com.souldev.security.payload.response.UserResponse;
import com.souldev.security.security.dtos.LoginUser;
import com.souldev.security.security.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.OPTIONS}, allowedHeaders = "*")
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "Endpoints pour la connexion et l’inscription des utilisateurs")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginUser loginUser) {
        try {
            UserResponse response = authService.login(loginUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new CustomApiResponse(false, e.getMessage(), null, 401));
        }
    }

}