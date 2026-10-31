package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seuls les attributs modifiables du contrat : ni la réservation, ni les paiements (gérés à part).
 */
public record ContratUpdateRequest(
        LocalDate dateSignature,

        @NotNull(message = "Le montant total est obligatoire")
        @PositiveOrZero(message = "Le montant total doit être positif ou nul")
        @Digits(integer = 8, fraction = 2, message = "Le montant total doit avoir au plus 2 décimales")
        BigDecimal montantTotal,

        boolean valide
) {
}
