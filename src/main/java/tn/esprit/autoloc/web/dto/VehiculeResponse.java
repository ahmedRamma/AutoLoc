package tn.esprit.autoloc.web.dto;

import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

import java.math.BigDecimal;
import java.util.List;

public record VehiculeResponse(Long idVehicule, String immatriculation, String marque, String modele,
                               CategorieVehicule categorie, BigDecimal tarifJournalier, StatutVehicule statut,
                               Long idAgence, String nomAgence, List<EquipementResponse> equipements) {
}
