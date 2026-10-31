package tn.esprit.autoloc.web.dto;

import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;

public record ReservationResponse(Long idReservation, LocalDate dateDebut, LocalDate dateFin, StatutReservation statut,
                                  Long idClient, String nomClient, Long idVehicule, String immatriculation) {
}
