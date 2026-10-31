package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Corps JSON commun à toutes les réponses d'erreur ; {@code erreurs} détaille les champs invalides.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErreurResponse(int statut, String message, LocalDateTime horodatage, Map<String, String> erreurs) {

    public ErreurResponse(int statut, String message) {
        this(statut, message, LocalDateTime.now(), null);
    }
}
