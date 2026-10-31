package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ClientRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        String prenom,

        @NotBlank(message = "L'e-mail est obligatoire")
        @Email(message = "Le format de l'e-mail est invalide")
        String email,

        @Pattern(regexp = AgenceRequest.TELEPHONE, message = "Le téléphone doit contenir 8 à 15 chiffres")
        String telephone,

        @NotBlank(message = "Le numéro de permis est obligatoire")
        String numPermis,

        @PastOrPresent(message = "La date d'inscription ne peut pas être dans le futur")
        LocalDate dateInscription
) {
}
