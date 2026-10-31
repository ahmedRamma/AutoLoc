package tn.esprit.autoloc.web.dto;

import tn.esprit.autoloc.domain.RoleEmploye;

public record EmployeResponse(Long idEmploye, String nom, String prenom, RoleEmploye role,
                              Long idAgence, String nomAgence) {
}
