package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;

/**
 * Le client et le véhicule sont résolus par le service à partir de leurs identifiants.
 */
@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequest request) {
        Reservation reservation = new Reservation();
        updateEntity(request, reservation);
        return reservation;
    }

    public void updateEntity(ReservationRequest request, Reservation reservation) {
        reservation.setDateDebut(request.dateDebut());
        reservation.setDateFin(request.dateFin());
        if (request.statut() != null) {
            reservation.setStatut(request.statut());
        }
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(reservation.getIdReservation(), reservation.getDateDebut(),
                reservation.getDateFin(), reservation.getStatut(),
                reservation.getClient().getIdClient(),
                reservation.getClient().getPrenom() + " " + reservation.getClient().getNom(),
                reservation.getVehicule().getIdVehicule(), reservation.getVehicule().getImmatriculation());
    }
}
