# AutoLoc

Plateforme de gestion de location de véhicules multi-agences.
Projet réalisé dans le cadre de l'UP ASI — Architecture des Systèmes d'Information (ESPRIT).

## Objectifs du projet

- Centraliser la gestion de la flotte de véhicules de plusieurs agences.
- Permettre aux clients de rechercher, réserver et gérer leurs locations.
- Suivre le cycle de vie d'une location : réservation, remise du véhicule, retour, facturation.
- Offrir aux responsables une vision de l'activité par agence.
- Exposer les fonctionnalités via une API REST (Spring Boot).

## Acteurs et cas d'utilisation (v0)

### Client
- Rechercher un véhicule disponible (agence, dates, catégorie)
- Réserver / annuler une réservation
- Consulter l'historique de ses locations

### Agent d'agence
- Gérer les réservations de son agence
- Effectuer la remise et le retour d'un véhicule
- Enregistrer les paiements

### Responsab[]()le d'agence (Manager)
- Gérer la flotte de véhicules de son agence
- Gérer les agents de son agence
- Consulter les statistiques de l'agence

### Administrateur
- Gérer les agences
- Gérer les utilisateurs et leurs rôles
- Paramétrer les catégories de véhicules et les tarifs

## Stack technique

Java 17+ (JDK 21), Spring Boot, Spring Data JPA, MySQL/MariaDB, Maven, Lombok, springdoc-openapi, JUnit 5 / Mockito, Git/GitHub, Postman, IntelliJ IDEA Ultimate.