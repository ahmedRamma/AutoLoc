package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AgenceRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
        String nom,

        @NotBlank(message = "La ville est obligatoire")
        String ville,

        @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
        String adresse,

        @Pattern(regexp = TELEPHONE, message = "Le téléphone doit contenir 8 à 15 chiffres")
        String telephone
) {
    public static final String TELEPHONE = "^\\+?[0-9]{8,15}$";
}
