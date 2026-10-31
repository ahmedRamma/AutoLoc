package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;

public record ReservationRequest(
        @NotNull(message = "La date de début est obligatoire")
        LocalDate dateDebut,

        @NotNull(message = "La date de fin est obligatoire")
        LocalDate dateFin,

        StatutReservation statut,

        @NotNull(message = "Le client est obligatoire")
        Long idClient,

        @NotNull(message = "Le véhicule est obligatoire")
        Long idVehicule
) {
}
