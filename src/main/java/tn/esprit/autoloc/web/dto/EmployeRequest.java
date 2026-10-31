package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tn.esprit.autoloc.domain.RoleEmploye;

public record EmployeRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        String prenom,

        @NotNull(message = "Le rôle est obligatoire (AGENT ou MANAGER)")
        RoleEmploye role,

        @NotNull(message = "L'agence est obligatoire")
        Long idAgence
) {
}
