package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tn.esprit.autoloc.domain.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaiementRequest(
        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être strictement positif")
        @Digits(integer = 8, fraction = 2, message = "Le montant doit avoir au plus 2 décimales")
        BigDecimal montant,

        LocalDate datePaiement,

        @NotNull(message = "Le mode de paiement est obligatoire (CARTE, ESPECES, VIREMENT)")
        ModePaiement modePaiement
) {
}
