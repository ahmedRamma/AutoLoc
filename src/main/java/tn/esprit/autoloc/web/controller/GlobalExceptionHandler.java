package tn.esprit.autoloc.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.web.dto.ErreurResponse;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestion centralisée des erreurs de tous les contrôleurs : chaque erreur est renvoyée
 * au même format JSON {@link ErreurResponse} (statut, message, horodatage, erreurs par champ).
 * <p>
 * Hérite de {@link ResponseEntityExceptionHandler} pour traiter aussi les erreurs standard
 * de Spring MVC (JSON illisible, méthode non autorisée 405, paramètre invalide...).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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
                "Opération refusée par la base : valeur déjà utilisée ou ressource encore référencée");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurResponse> handleUnexpected(Exception ex) {
        log.error("Erreur inattendue", ex);
        return erreur(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
    }

    /** Échec de {@code @Valid} sur un DTO : 400 avec le message de chaque champ invalide. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erreurs.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ErreurResponse(HttpStatus.BAD_REQUEST.value(),
                "Données invalides", LocalDateTime.now(), erreurs));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return ResponseEntity.badRequest().body(new ErreurResponse(HttpStatus.BAD_REQUEST.value(),
                "Corps JSON illisible : vérifier la syntaxe, le format des dates (AAAA-MM-JJ) et les valeurs des énumérations"));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatusCode status, WebRequest request) {
        return ResponseEntity.badRequest().body(new ErreurResponse(HttpStatus.BAD_REQUEST.value(),
                "Paramètre invalide : " + ex.getValue()));
    }

    /** Autres erreurs standard de Spring MVC (405, 415...) : même format JSON. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        return ResponseEntity.status(statusCode).headers(headers)
                .body(new ErreurResponse(statusCode.value(), ex.getMessage()));
    }

    private ResponseEntity<ErreurResponse> erreur(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(new ErreurResponse(statut.value(), message));
    }
}
