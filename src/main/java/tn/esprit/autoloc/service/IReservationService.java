package tn.esprit.autoloc.service;

import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;

import java.util.List;

public interface IReservationService {

    ReservationResponse create(ReservationRequest request);

    ReservationResponse findById(Long id);

    List<ReservationResponse> findAll();

    ReservationResponse update(Long id, ReservationRequest request);

    void deleteById(Long id);
}
