package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class DirectionDTO {

    @NotBlank(message = "Le nom est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;

    @NotBlank(message = "L'école est requise")
    private String ecoleId;
    @NotBlank(message = "La fonction est requise")
    private String fonctionId;
}
