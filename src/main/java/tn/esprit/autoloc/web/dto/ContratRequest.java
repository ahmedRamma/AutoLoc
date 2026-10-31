package tn.esprit.autoloc.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ContratRequest(
        LocalDate dateSignature,

        @NotNull(message = "Le montant total est obligatoire")
        @PositiveOrZero(message = "Le montant total doit être positif ou nul")
        @Digits(integer = 8, fraction = 2, message = "Le montant total doit avoir au plus 2 décimales")
        BigDecimal montantTotal,

        boolean valide,

        @NotNull(message = "La réservation est obligatoire")
        Long idReservation,

        List<@Valid PaiementRequest> paiements
) {
}
