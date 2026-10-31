package tn.esprit.autoloc.service;

import tn.esprit.autoloc.web.dto.PaiementResponse;

import java.util.List;

/**
 * Lecture seule : la création et la suppression d'un paiement passent par le Contrat (composition).
 */
public interface IPaiementService {

    PaiementResponse findById(Long id);

    List<PaiementResponse> findAll();
}
