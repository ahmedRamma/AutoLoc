package tn.esprit.autoloc.web.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import java.time.LocalDateTime;

/**
 * Traduit les exceptions levées par la couche Service en réponses HTTP lisibles.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErreurResponse(int statut, String message, LocalDateTime horodatage) {
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErreurResponse> handleNotFound(ResourceNotFoundException ex) {
        return erreur(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErreurResponse> handleBadRequest(IllegalArgumentException ex) {
        return erreur(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErreurResponse> handleConflict(DataIntegrityViolationException ex) {
        return erreur(HttpStatus.CONFLICT,
                "Opération refusée par la base : valeur déjà utilisée, ou ressource liée inexistante ou encore référencée");
    }

    private ResponseEntity<ErreurResponse> erreur(HttpStatus statut, String message) {
        return ResponseEntity.status(statut)
                .body(new ErreurResponse(statut.value(), message, LocalDateTime.now()));
    }
}
