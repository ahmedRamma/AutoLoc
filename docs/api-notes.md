# Atelier 5 — API REST, tests Postman et documentation Swagger

- Préfixe commun défini une seule fois : `server.servlet.context-path=/api` (port `8081`).
- Swagger UI : <http://localhost:8081/api/swagger-ui.html> — description OpenAPI : <http://localhost:8081/api/v3/api-docs>.
- Collection Postman : `postman/AutoLoc.postman_collection.json` + environnement `postman/local.postman_environment.json`
  (100 requêtes, 132 assertions : cas nominaux, 404, 400, cascade Contrat/Paiement, 405).

## A. Endpoints

Erreurs communes : `404` = `ResourceNotFoundException`, `400` = `IllegalArgumentException` (règle métier, identifiant
fourni à la création) ou JSON illisible, `409` = contrainte de la base (valeur unique déjà utilisée, ressource liée
inexistante ou encore référencée). Corps d'erreur : `{ "statut", "message", "horodatage" }`.

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| `AgenceController` | `GET /api/agences` | `findAll()` | 200 | — |
| | `GET /api/agences/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/agences` | `create(agence)` | 201 | 400 |
| | `PUT /api/agences/{id}` | `update(id, agence)` | 200 | 400, 404 |
| | `DELETE /api/agences/{id}` | `deleteById(id)` | 204 | 404, 409 |
| `EmployeController` | `GET /api/employes` | `findAll()` | 200 | — |
| | `GET /api/employes/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/employes` | `create(employe)` | 201 | 400, 409 |
| | `PUT /api/employes/{id}` | `update(id, employe)` | 200 | 400, 404 |
| | `DELETE /api/employes/{id}` | `deleteById(id)` | 204 | 404 |
| `VehiculeController` | `GET /api/vehicules` | `findAll()` | 200 | — |
| | `GET /api/vehicules/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/vehicules` | `create(vehicule)` | 201 | 400, 409 |
| | `PUT /api/vehicules/{id}` | `update(id, vehicule)` | 200 | 400, 404, 409 |
| | `DELETE /api/vehicules/{id}` | `deleteById(id)` | 204 | 404, 409 |
| `EquipementController` | `GET /api/equipements` | `findAll()` | 200 | — |
| | `GET /api/equipements/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/equipements` | `create(equipement)` | 201 | 400 |
| | `PUT /api/equipements/{id}` | `update(id, equipement)` | 200 | 400, 404 |
| | `DELETE /api/equipements/{id}` | `deleteById(id)` | 204 | 404, 409 |
| `ClientController` | `GET /api/clients` | `findAll()` | 200 | — |
| | `GET /api/clients/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/clients` | `create(client)` | 201 | 400, 409 |
| | `PUT /api/clients/{id}` | `update(id, client)` | 200 | 400, 404, 409 |
| | `DELETE /api/clients/{id}` | `deleteById(id)` | 204 | 404, 409 |
| `ReservationController` | `GET /api/reservations` | `findAll()` | 200 | — |
| | `GET /api/reservations/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/reservations` | `create(reservation)` | 201 | 400, 409 |
| | `PUT /api/reservations/{id}` | `update(id, reservation)` | 200 | 400, 404 |
| | `DELETE /api/reservations/{id}` | `deleteById(id)` | 204 | 404, 409 |
| `MaintenanceController` | `GET /api/maintenances` | `findAll()` | 200 | — |
| | `GET /api/maintenances/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/maintenances` | `create(maintenance)` | 201 | 400, 409 |
| | `PUT /api/maintenances/{id}` | `update(id, maintenance)` | 200 | 400, 404 |
| | `DELETE /api/maintenances/{id}` | `deleteById(id)` | 204 | 404 |
| `ContratController` | `GET /api/contrats` | `findAll()` | 200 | — |
| | `GET /api/contrats/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/contrats` | `create(contrat)` (paiements créés par cascade) | 201 | 400, 409 |
| | `PUT /api/contrats/{id}` | `update(id, contrat)` | 200 | 400, 404 |
| | `DELETE /api/contrats/{id}` | `deleteById(id)` (paiements supprimés par cascade) | 204 | 404 |
| | `POST /api/contrats/{idContrat}/paiements` | `addPaiement(idContrat, paiement)` | 201 | 400, 404 |
| | `DELETE /api/contrats/{idContrat}/paiements/{idPaiement}` | `removePaiement(idContrat, idPaiement)` | 200 | 404 |
| `PaiementController` | `GET /api/paiements` | `findAll()` | 200 | — |
| | `GET /api/paiements/{id}` | `findById(id)` | 200 | 404 |
| | `POST /api/paiements` (non exposé) | — | — | 405 |

## B. Annotations Jackson

> **Mise à jour (DTO)** : ces annotations ont été retirées des entités ; les contrôleurs exposent désormais des DTO (voir `docs/dto-validation-notes.md`). Cette section décrit l'état de l'Atelier 5.

| Association | Annotation retenue | Justification |
|---|---|---|
| `Contrat.paiements` / `Paiement.contrat` | `@JsonManagedReference("contrat-paiements")` / `@JsonBackReference("contrat-paiements")` | Composition : les paiements s'écrivent dans le JSON du contrat (création par cascade) ; Jackson renseigne `paiement.contrat` à la lecture et ne réécrit pas le contrat dans chaque paiement (pas de boucle). |
| `Agence.employes`, `Agence.vehicules` | `@JsonIgnore` (côté inverse) | Employés et véhicules sont créés seuls et désignent leur agence par `{"agence": {"idAgence": 1}}`. |
| `Client.reservations`, `Vehicule.reservations` | `@JsonIgnore` (côté inverse) | La réservation est créée seule et désigne son client et son véhicule. |
| `Equipement.vehicules` | `@JsonIgnore` (côté inverse du `@ManyToMany`) | Le lien est porté par `Vehicule.equipements`. |
| `Reservation.contrat` | `@JsonIgnore` (côté inverse du `@OneToOne`) | Le contrat désigne sa réservation ; l'inverse bouclerait. |
| Côtés propriétaires (`Vehicule.agence`, `Employe.agence`, `Reservation.client`…) | aucune annotation | Ils doivent être lus dans le corps du POST. |
| `Agence`, `Client`, `Vehicule`, `Reservation` (cibles d'un `@ManyToOne`/`@OneToOne` LAZY) | `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})` | Évite `No serializer found … ByteBuddyInterceptor` sur les proxys Hibernate. |

`spring.jpa.open-in-view=true` est déclaré explicitement (provisoire) : la session reste ouverte pendant la
sérialisation, ce qui permet à Jackson de charger les associations LAZY. Ces palliatifs disparaîtront avec les DTO.

**`@JsonBackReference` vs `@JsonIgnore`** : `@JsonBackReference` n'existe qu'en paire avec un
`@JsonManagedReference` ; la propriété n'est pas écrite en sortie mais elle est **renseignée automatiquement à la
lecture** à partir du parent (le paiement reçoit son contrat). `@JsonIgnore` ignore la propriété dans les deux sens,
sans rien reconstituer. C'est pourquoi `@JsonBackReference` ne convient pas à `Reservation.client` : le client
envoyé dans le POST serait ignoré.

## C. Postman et Swagger

| Outil | Apport | Exemple concret dans l'atelier |
|---|---|---|
| Postman | **Tester** : requêtes rejouables, variables d'environnement (`{{baseUrl}}`, identifiants mémorisés), assertions automatiques, exécution de toute la collection. | Scénario de cascade : « Créer un contrat avec ses paiements » (201, 2 paiements) → « Modifier le contrat sans paiements » (les paiements restent) → « Supprimer le contrat » (204) → « Consulter un paiement du contrat supprimé » (404). |
| Swagger UI | **Documenter et explorer** : contrat OpenAPI généré depuis le code, toujours synchronisé ; groupes (`@Tag`), résumés (`@Operation`), codes documentés (`@ApiResponse`), schémas des entités ; *Try it out*. | Section *Schemas* : la structure de `Contrat` (avec `paiements`) est déduite de l'entité ; l'endpoint `DELETE /agences/{id}` affiche ses réponses 204 / 404 / 409. |

## D. Anomalies SonarQube for IDE

| Anomalie | Règle / explication | Correction |
|---|---|---|
| Entités JPA reçues en `@RequestBody` dans les contrôleurs | `java:S4684` — *Persistent entities should not be used as arguments of @RequestMapping methods* : le client peut renseigner des champs qu'il ne devrait pas contrôler (identifiant, associations : *mass assignment*). | Atténué dans les services (`create` refuse un identifiant, `update` ne recopie que les champs modifiables) ; correction définitive avec les DTO (Chapitre 10), comme prévu par l'atelier. |
| *à compléter après « Analyze with SonarQube for IDE » sur `web.controller`* | | |

## E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode ?**
   Le `DispatcherServlet` (contrôleur frontal unique). Il interroge le `HandlerMapping`, qui associe le couple
   verbe HTTP + URL (`@RequestMapping` de la classe + `@GetMapping("/{id}")`… de la méthode) à la méthode du
   contrôleur ; les arguments sont extraits du chemin (`@PathVariable`) ou du corps (`@RequestBody`, via Jackson).

2. **Pourquoi `ContratController` dépend-il de `IContratService` ?**
   Le contrôleur reste mince : la logique métier (garde sur l'id, règles, exceptions) est dans le service ; passer
   directement au repository la contournerait. Dépendre de l'interface plutôt que de `ContratServiceImpl` garde un
   couplage faible (implémentation remplaçable, proxy transactionnel de Spring).

3. **Pourquoi 201, 204, 404 ?**
   201 *Created* signale qu'une ressource a été créée (le corps contient son identifiant) ; 204 *No Content* signale
   un succès sans corps à renvoyer. Une ressource absente est une erreur du client (mauvais identifiant), d'où 404 ;
   500 signifierait une panne du serveur. Le `GlobalExceptionHandler` traduit `ResourceNotFoundException` en 404.

4. **Pourquoi `PaiementController` n'expose-t-il que des GET ?**
   Paiement est l'enfant de la composition Contrat ◆ Paiement : il est créé et supprimé à travers son contrat
   (cascade, `orphanRemoval`). Aucune méthode n'étant mappée sur `POST /api/paiements`, Spring répond
   **405 Method Not Allowed**.

5. **Risques de l'exposition directe des entités ; `@JsonManagedReference` vs `@JsonIgnore`.**
   Couplage de l'API au schéma de la base, champs internes visibles, *mass assignment* (S4684), boucles de
   sérialisation et chargements LAZY (proxys, `LazyInitializationException`, requêtes N+1).
   `Contrat.paiements` est une **composition** : les paiements sont créés dans le JSON du contrat, il faut que Jackson
   relie chaque paiement à son contrat → paire managed/back. `Agence.vehicules` est une simple association : un
   véhicule est créé seul et désigne son agence par son identifiant, la liste inverse est donc simplement ignorée.
