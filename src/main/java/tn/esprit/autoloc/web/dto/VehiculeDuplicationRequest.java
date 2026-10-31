package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Seule donnée à fournir pour dupliquer un véhicule : l'immatriculation (unique) de la copie.
 */
public record VehiculeDuplicationRequest(
        @NotBlank(message = "L'immatriculation de la copie est obligatoire")
        @Size(max = 20, message = "L'immatriculation ne doit pas dépasser 20 caractères")
        String immatriculation
) {
}
