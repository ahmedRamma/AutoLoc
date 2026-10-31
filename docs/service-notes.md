# Atelier 4 — IoC, injection de dépendances et couche Service

## A. Modes d'injection comparés

| Critère | Constructeur (`final` + `@RequiredArgsConstructor`) | Attribut (`@Autowired private …`) |
|---|---|---|
| Champ `final` possible ? | Oui | Non (le conteneur l'affecte après la construction) |
| Dépendance visible dans la signature de la classe ? | Oui, paramètre du constructeur | Non, cachée dans un champ privé |
| Utilisable avec `new` hors conteneur sans dépendance `null` ? | Oui : impossible de construire l'objet sans lui fournir son repository | Non : `new ContratServiceImpl()` laisse `contratRepository` à `null` (`NullPointerException` au premier appel) |
| Signalé par SonarQube for IDE ? | Non | Oui — `java:S6813` *Field dependency injection should be avoided* |

Convention AutoLoc : injection par constructeur, champ `final`, dépendance typée par une interface.

## B. Services

Tous les services sont annotés `@Service`, sans état (seuls des champs `final` et des constantes), et reçoivent
leur repository par constructeur (généré par Lombok `@RequiredArgsConstructor`, sans `@Autowired`).

| Service | Dépendances injectées | Mode | Justification / règle de gestion |
|---|---|---|---|
| `ContratServiceImpl` | `IContratRepository` | Constructeur | Racine de la composition Contrat ◆ Paiement : montant total négatif refusé ; les paiements sont ajoutés / retirés via le contrat ; suppression en cascade des paiements. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur | Lecture seule (`findById`, `findAll`) : création et suppression passent par le Contrat. |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur | Nom de l'agence obligatoire. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur | Nom, prénom et rôle obligatoires. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur | Tarif journalier obligatoire et non négatif. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur | Libellé obligatoire (le modèle n'a pas d'attribut quantité). |
| `ClientServiceImpl` | `IClientRepository` | Constructeur | Nom, e-mail et numéro de permis obligatoires. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur | Date de fin non antérieure à la date de début. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur | Date de fin non antérieure à la date de début (le modèle n'a pas d'attribut coût). |

Règles communes : `create` refuse un identifiant non `null` ; `findById` et `deleteById` lèvent
`ResourceNotFoundException` si l'entité est absente ; `update` charge l'existant puis recopie les champs
modifiables. Les contrôleurs (`web.controller`) reçoivent à leur tour l'interface `I…Service` par constructeur.

## C. Messages d'erreur du conteneur

**Message 1 — `NoSuchBeanDefinitionException` (aucun bean `IContratService`)**
Bean concerné : `ContratController`, qui demande un `IContratService` dans son constructeur.
Causes probables côté `ContratServiceImpl` : l'annotation `@Service` a été oubliée, ou la classe est placée
hors du package racine `tn.esprit.autoloc` (le scan ne la détecte pas).
Correction : ajouter `@Service` et/ou déplacer la classe dans `tn.esprit.autoloc.service`.

**Message 2 — `NoUniqueBeanDefinitionException` (2 beans : `emailNotificateur`, `smsNotificateur`)**
Deux implémentations du même type sont candidates pour le paramètre du constructeur de `NotificationController`.
- Correction 1 : `@Primary` **sur la classe** de l'implémentation à utiliser par défaut (ex. `EmailNotificateur`).
- Correction 2 : `@Qualifier("smsNotificateur")` **sur le paramètre du constructeur** pour choisir explicitement le bean.

**Message 3 — dépendance circulaire `clientServiceImpl` ⇄ `reservationServiceImpl`**
Avec l'injection par constructeur, pour créer `clientServiceImpl` il faut d'abord `reservationServiceImpl`, qui
exige lui-même `clientServiceImpl` : aucun des deux ne peut être construit en premier, le conteneur échoue au
démarrage (il ne peut pas injecter un objet à moitié construit, contrairement à l'injection par attribut).
Piste : supprimer le cycle par la conception — faire dépendre les services des **repositories** plutôt que
l'un de l'autre, ou extraire la responsabilité commune dans un troisième service dont les deux dépendent.

## D. Anomalies SonarQube for IDE

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Injection par attribut `@Autowired private IContratRepository contratRepository` (expérience de l'étape 2) | `java:S6813` — *Field dependency injection should be avoided* : dépendance cachée, champ non `final`, `null` hors conteneur. | Retour à l'injection par constructeur (`private final` + `@RequiredArgsConstructor`). |
| `throw new RuntimeException(...)` dans `ContratServiceImpl.findById` (expérience de l'étape 4) | `java:S112` — *Generic exceptions should never be thrown* : l'appelant ne peut pas distinguer l'erreur. | Exception métier dédiée `ResourceNotFoundException` (traduite en HTTP 404 par `GlobalExceptionHandler`). |
| Appel d'une méthode `@Transactional` via `this` (`update` appelait `findById`) | `java:S6809` — l'appel interne ne passe pas par le proxy Spring : l'annotation `@Transactional` de la méthode appelée est ignorée. | Recherche extraite dans une méthode privée `findOrThrow(id)`, utilisée par `findById`, `update` et les autres méthodes. |

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**
   Il n'apparaît nulle part dans notre code : c'est le conteneur Spring IoC qui, au démarrage
   (`SpringApplication.run`), détecte la classe grâce à `@Service` et au scan, puis appelle son constructeur
   (par réflexion) en lui passant le proxy de `IContratRepository`.

2. **Pourquoi un contrôleur dépend-il de `IContratService` et non de `ContratServiceImpl` ?**
   Pour dépendre d'une abstraction (couplage faible) : on peut changer d'implémentation, en fournir une de test,
   ou en ajouter une autre (`@Primary` / `@Qualifier`) sans modifier le contrôleur. Spring peut aussi y
   substituer un proxy (transactions).

3. **Pourquoi un service singleton doit-il rester sans état ?**
   Une seule instance est partagée par tous les threads (toutes les requêtes HTTP simultanées). Exemple de bug :
   un attribut `private Contrat contratCourant;` affecté au début d'une requête peut être écrasé par une requête
   concurrente ; l'utilisateur A modifie ou paie alors le contrat de l'utilisateur B.

4. **Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` ?**
   L'objet reçu est incomplet : le passer à `save` (merge) écraserait toutes les colonnes avec ses valeurs, y
   compris `null`, et avec `orphanRemoval` une liste `paiements` vide supprimerait tous les paiements du contrat.
   En chargeant puis en recopiant seulement les champs modifiables, on protège l'identifiant et les associations.

5. **`@Component` / `@Bean`, `@Primary` / `@Qualifier`, et `IPaiementService` ?**
   - `@Component` se pose sur une **classe** que l'on écrit : elle est détectée par le scan. `@Bean` se pose sur
     une **méthode** d'une classe `@Configuration` qui construit l'objet elle-même : utile pour une classe de
     bibliothèque que l'on ne peut pas annoter, ou une construction particulière.
   - `@Primary` se pose sur la classe et désigne le candidat **par défaut** ; `@Qualifier("nom")` se pose au
     point d'injection et choisit un bean **précis** (il l'emporte sur `@Primary`).
   - `Paiement` est l'enfant de la **composition** Contrat ◆ Paiement : il n'existe pas sans son contrat. Pour
     garder la cohérence (montants, cascade, `orphanRemoval`), seul le Contrat crée ou retire un paiement ;
     `IPaiementService` se limite donc à la lecture.
