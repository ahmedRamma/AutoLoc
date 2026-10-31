# DTO, validation et gestion centralisée des erreurs

## 1. Architecture

```
Postman / Swagger ──JSON──▶ Controller ──Request DTO──▶ Service ──▶ Mapper ──▶ Entité ──▶ Repository ──▶ MySQL
                  ◀─JSON─── Controller ◀─Response DTO── Service ◀── Mapper ◀── Entité
```

| Package | Contenu |
|---|---|
| `web.dto` | `…Request` (données reçues, avec les contraintes de validation) et `…Response` (données renvoyées) — *records* Java immuables ; `ContratUpdateRequest` ; `ErreurResponse` (format commun des erreurs). |
| `web.mapper` | Un mapper par entité (`@Component`) : `toEntity(request)`, `updateEntity(request, entite)`, `toResponse(entite)`. Écrits à la main : la conversion reste lisible et explicable. |
| `service` | Les services reçoivent un DTO de requête, résolvent les liens par identifiant (`idAgence`, `idClient`…, 404 si absent), appliquent les règles métier et renvoient un DTO de réponse. La conversion se fait **dans la transaction** : les associations LAZY sont lues sans erreur, d'où `spring.jpa.open-in-view=false`. |
| `web.controller` | Contrôleurs minces : `@Valid @RequestBody …Request`, retour `…Response`. Aucune entité JPA exposée. |
| `domain` | Entités JPA **sans aucune annotation Jackson** (plus de `@JsonIgnore`, `@JsonManagedReference`…). |

## 2. Ce que les DTO optimisent

| Avant (entités exposées) | Après (DTO) |
|---|---|
| `GET /reservations` renvoyait la réservation + tout le client + tout le véhicule + toute l'agence du véhicule. | `ReservationResponse` à plat : `idClient`, `nomClient`, `idVehicule`, `immatriculation`. |
| Corps imbriqués : `"agence": {"idAgence": 1}`. | Identifiant simple : `"idAgence": 1` ; agence inexistante → **404** clair (avant : erreur SQL). |
| Le client pouvait envoyer `idVehicule`, `reservations`… (Sonar `java:S4684`, *mass assignment*). | Le `Request` ne contient que les champs autorisés : l'identifiant ne peut plus être imposé. |
| Annotations Jackson provisoires et `open-in-view=true` pour éviter boucles et `LazyInitializationException`. | Aucune boucle possible : les DTO ne se référencent pas entre eux. |

## 3. Validation (Bean Validation, `spring-boot-starter-validation`)

| DTO | Contraintes principales |
|---|---|
| `AgenceRequest` | `nom`, `ville` : `@NotBlank` ; `telephone` : `@Pattern` (8 à 15 chiffres) |
| `ClientRequest` | `nom`, `prenom`, `numPermis` : `@NotBlank` ; `email` : `@NotBlank @Email` ; `dateInscription` : `@PastOrPresent` |
| `EmployeRequest` | `nom`, `prenom` : `@NotBlank` ; `role`, `idAgence` : `@NotNull` |
| `EquipementRequest` | `libelle` : `@NotBlank @Size(max = 100)` |
| `VehiculeRequest` | textes `@NotBlank @Size` ; `tarifJournalier` : `@NotNull @PositiveOrZero @Digits` ; `categorie`, `statut`, `idAgence` : `@NotNull` |
| `ReservationRequest` | `dateDebut`, `dateFin`, `idClient`, `idVehicule` : `@NotNull` ; règle *date de fin ≥ date de début* dans le service |
| `MaintenanceRequest` | `dateDebut`, `idVehicule` : `@NotNull` ; même règle de dates dans le service |
| `ContratRequest` / `ContratUpdateRequest` | `montantTotal` : `@NotNull @PositiveOrZero` ; `idReservation` : `@NotNull` ; `paiements` : `List<@Valid PaiementRequest>` (validation en cascade) |
| `PaiementRequest` | `montant` : `@NotNull @Positive` ; `modePaiement` : `@NotNull` |

`@Valid` sur le paramètre `@RequestBody` déclenche la validation avant l'appel du service ; en cas d'échec,
Spring lève `MethodArgumentNotValidException`.

## 4. Gestion centralisée des erreurs — `GlobalExceptionHandler`

`@RestControllerAdvice` qui étend `ResponseEntityExceptionHandler` : **toutes** les erreurs de **tous** les
contrôleurs sont renvoyées au même format `ErreurResponse` :

```json
{ "statut": 400, "message": "Données invalides", "horodatage": "2026-10-31T02:39:31",
  "erreurs": { "email": "Le format de l'e-mail est invalide", "nom": "Le nom est obligatoire" } }
```

| Exception | Statut | Message |
|---|---|---|
| `MethodArgumentNotValidException` (`@Valid`) | 400 | « Données invalides » + `erreurs` (champ → message) |
| `HttpMessageNotReadableException` | 400 | JSON illisible, date mal formée, valeur d'énumération inconnue |
| `TypeMismatchException` (`/vehicules/abc`) | 400 | « Paramètre invalide : abc » |
| `IllegalArgumentException` (règle métier du service) | 400 | message de la règle |
| `ResourceNotFoundException` | 404 | « Vehicule introuvable avec l'identifiant 9 » |
| Méthode non autorisée (`POST /paiements`) | 405 | via `handleExceptionInternal`, même format |
| `DataIntegrityViolationException` | 409 | valeur unique déjà utilisée / ressource encore référencée |
| Toute autre `Exception` | 500 | « Erreur interne du serveur » (détail dans les logs, jamais exposé au client) |

## 5. Tests

Collection `postman/AutoLoc.postman_collection.json` (environnement `local`) : 108 requêtes, 201 assertions —
CRUD des 9 ressources, réponses DTO, erreurs de validation champ par champ, 404 sur ressource liée inexistante,
cascade Contrat/Paiement, 405.
