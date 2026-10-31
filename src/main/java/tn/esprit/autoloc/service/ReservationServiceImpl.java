package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private static final String RESSOURCE = "Reservation";

    private final IReservationRepository reservationRepository;

    @Override
    @Transactional
    public Reservation create(Reservation reservation) {
        if (reservation.getIdReservation() != null) {
            throw new IllegalArgumentException("Une nouvelle réservation ne doit pas avoir d'identifiant");
        }
        if (reservation.getClient() == null) {
            throw new IllegalArgumentException("Le client de la réservation est obligatoire");
        }
        if (reservation.getVehicule() == null) {
            throw new IllegalArgumentException("Le véhicule de la réservation est obligatoire");
        }
        valider(reservation);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return findOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional
    public Reservation update(Long id, Reservation reservation) {
        valider(reservation);
        Reservation existant = findOrThrow(id);
        existant.setDateDebut(reservation.getDateDebut());
        existant.setDateFin(reservation.getDateFin());
        existant.setStatut(reservation.getStatut());
        if (reservation.getClient() != null) {
            existant.setClient(reservation.getClient());
        }
        if (reservation.getVehicule() != null) {
            existant.setVehicule(reservation.getVehicule());
        }
        return reservationRepository.save(existant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESSOURCE, id);
        }
        reservationRepository.deleteById(id);
    }

    private Reservation findOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESSOURCE, id));
    }

    private void valider(Reservation reservation) {
        if (reservation.getDateDebut() != null && reservation.getDateFin() != null
                && reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
