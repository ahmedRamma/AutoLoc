# Atelier 3 — Couche Repository Spring Data JPA

## 1. Choix des interfaces

Toutes les interfaces sont dans `tn.esprit.autoloc.repository`, préfixées par `I`, sans annotation
`@Repository` (le proxy est généré par Spring Data au démarrage).

| Interface | Étend | Justification |
|---|---|---|
| `IContratRepository` | `JpaRepository<Contrat, Long>` | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. Racine de la composition Contrat ◆ Paiement. |
| `IPaiementRepository` | `JpaRepository<Paiement, Long>` | Lecture des paiements. Création / suppression uniquement via le `Contrat` (composition). |
| `IAgenceRepository` | `JpaRepository<Agence, Long>` | CRUD + listes ; tri et pagination utiles pour lister les agences par ville. |
| `IEmployeRepository` | `JpaRepository<Employe, Long>` | CRUD + listes ; pagination des employés d'une agence. |
| `IVehiculeRepository` | `JpaRepository<Vehicule, Long>` | CRUD + tri / pagination du catalogue (par tarif, catégorie…). |
| `IEquipementRepository` | `JpaRepository<Equipement, Long>` | CRUD + listes pour le référentiel des équipements. |
| `IClientRepository` | `JpaRepository<Client, Long>` | CRUD + pagination de la liste des clients. |
| `IReservationRepository` | `JpaRepository<Reservation, Long>` | CRUD + tri par dates de réservation. |
| `IMaintenanceRepository` | `JpaRepository<Maintenance, Long>` | CRUD + historique trié des maintenances. |

**À retenir**

- `CrudRepository.findAll()` renvoie un `Iterable` ; `ListCrudRepository` / `JpaRepository` renvoient une `List`.
- Depuis Spring Data 3, `PagingAndSortingRepository` n'étend plus `CrudRepository` : `JpaRepository` réunit les deux.
- `deleteById` / `delete` passent par le contexte de persistance (cascade et `orphanRemoval` appliqués) ;
  `deleteAllInBatch` / `deleteAllByIdInBatch` les contournent : à ne pas utiliser sur `Contrat`.

## 2. Couche service

Une interface `I<Entite>Service` et une classe `<Entite>ServiceImpl` par entité, dans `tn.esprit.autoloc.service`.
CRUD complet implémenté pour deux entités :

| Service | Méthodes |
|---|---|
| `IVehiculeService` | `retrieveAllVehicules`, `retrieveVehicule`, `addVehicule`, `updateVehicule`, `removeVehicule` |
| `IContratService` | `retrieveAllContrats`, `retrieveContrat`, `addContrat`, `updateContrat`, `removeContrat`, `addPaiementToContrat`, `removePaiementFromContrat` |

- `findById` renvoie un `Optional` : un identifiant inconnu lève une `EntityNotFoundException`.
- `deleteById` ne lève plus d'exception si l'id n'existe pas (Spring Data 3) : l'existence est vérifiée avant.
- Les mises à jour rechargent l'entité puis copient les champs simples, pour ne pas écraser
  les associations (une liste `paiements` vide supprimerait tous les paiements via `orphanRemoval`).

## 3. Anomalies SonarQube for IDE

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
|---|---|---|
| Import générique `jakarta.persistence.*` dans `Vehicule` | `java:S2208` — *Wildcard imports should not be used* : masque les dépendances réelles et peut créer des conflits de noms. | Remplacé par des imports explicites. |
| Méthode `contextLoads()` vide dans `AutolocApiApplicationTests` | `java:S1186` — *Methods should not be empty* : une méthode vide doit expliquer pourquoi. | Ajout d'un commentaire expliquant que le test vérifie le démarrage du contexte. |
| *Call transactional methods via an injected dependency instead of directly via 'this'* — 4 occurrences dans `ContratServiceImpl`, 1 dans `VehiculeServiceImpl` | `java:S6809` — un appel interne (`this.retrieveContrat(...)`) ne passe pas par le proxy Spring : l'annotation `@Transactional` de la méthode appelée est ignorée. | Recherche extraite dans une méthode privée non transactionnelle (`findContratOrThrow`, `findVehiculeOrThrow`), utilisée par toutes les méthodes du service. |
