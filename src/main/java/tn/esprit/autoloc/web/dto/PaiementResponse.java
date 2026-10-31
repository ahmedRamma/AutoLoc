package tn.esprit.autoloc.web.dto;

import tn.esprit.autoloc.domain.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaiementResponse(Long idPaiement, BigDecimal montant, LocalDate datePaiement,
                               ModePaiement modePaiement, Long idContrat) {
}
