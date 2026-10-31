package tn.esprit.autoloc.domain;

/**
 * Patron de conception Prototype : un objet sait produire une copie de lui-même.
 * Le code client crée de nouveaux objets en copiant un modèle existant,
 * sans connaître les détails de sa construction.
 *
 * @param <T> type de l'objet copié
 */
public interface Prototype<T> {

    /**
     * @return un nouvel objet, non persisté, initialisé à partir de celui-ci
     */
    T copier();
}
