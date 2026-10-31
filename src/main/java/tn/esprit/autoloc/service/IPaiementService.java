package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Paiement;

import java.util.List;

/**
 * Lecture seule : la création et la suppression d'un paiement passent par le Contrat (composition).
 */
public interface IPaiementService {

    Paiement findById(Long id);

    List<Paiement> findAll();
}
