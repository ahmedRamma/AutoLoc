# Patron de conception Prototype — AutoLoc

## 1. Le patron en une phrase

**Prototype** (GoF, patron de *création*) : on crée un nouvel objet **en copiant un objet existant** (le
*prototype*), au lieu de le construire de zéro. C'est l'objet lui-même qui sait se copier (`copier()`) : le code
client n'a pas besoin de connaître tous ses attributs.

**Cas AutoLoc : dupliquer un véhicule.** Une agence reçoit souvent plusieurs véhicules identiques (même marque,
modèle, catégorie, tarif, équipements). Au lieu de tout ressaisir, on prend un véhicule existant comme modèle et on
ne fournit que ce qui change : **l'immatriculation**.

## 2. État du projet avant l'ajout

Recherche dans `src/` de `@Scope("prototype")`, `prototype`, `clone()`, `Cloneable`, `ObjectProvider`, `copy()`,
constructeur de copie, `toBuilder` : **aucun résultat**. Le patron n'existait pas ; il a été ajouté.

## 3. Classes et méthodes concernées (code exact)

| Rôle dans le patron | Fichier | Lignes |
|---|---|---|
| Interface **Prototype** | `src/main/java/tn/esprit/autoloc/domain/Prototype.java` | 1–16 |
| **Prototype concret** : `Vehicule implements Prototype<Vehicule>` | `src/main/java/tn/esprit/autoloc/domain/Vehicule.java` | 36 (déclaration), 79–102 (`copier()`) |
| **Client** du prototype : `dupliquer(...)` | `src/main/java/tn/esprit/autoloc/service/VehiculeServiceImpl.java` | 75–86 |
| Contrat du service | `src/main/java/tn/esprit/autoloc/service/IVehiculeService.java` | 21 |
| Endpoint REST | `src/main/java/tn/esprit/autoloc/web/controller/VehiculeController.java` | 90–104 |
| DTO de la requête | `src/main/java/tn/esprit/autoloc/web/dto/VehiculeDuplicationRequest.java` | 1–14 |

### 3.1 L'interface `Prototype<T>` — `domain/Prototype.java`

```java
public interface Prototype<T> {

    /**
     * @return un nouvel objet, non persisté, initialisé à partir de celui-ci
     */
    T copier();
}
```

### 3.2 Le prototype concret — `domain/Vehicule.java`

```java
public class Vehicule implements Prototype<Vehicule> {
    // ... attributs de l'entité ...

    @Override
    public Vehicule copier() {
        Vehicule copie = new Vehicule();
        copie.setMarque(marque);
        copie.setModele(modele);
        copie.setCategorie(categorie);
        copie.setTarifJournalier(tarifJournalier);
        copie.setStatut(StatutVehicule.DISPONIBLE);
        copie.setAgence(agence);
        copie.setEquipements(new HashSet<>(equipements));
        return copie;
    }
}
```

| Attribut | Dans la copie | Pourquoi |
|---|---|---|
| `marque`, `modele`, `categorie`, `tarifJournalier` | **copiés** | C'est ce qui définit « le même modèle ». |
| `agence` | **même référence** (copie superficielle) | Entité de référence partagée : le nouveau véhicule est dans la même agence. |
| `equipements` | **nouvel ensemble** contenant les mêmes équipements | Les équipements (GPS…) sont partagés, mais la collection est indépendante : en modifier une ne modifie pas l'autre. |
| `statut` | remis à `DISPONIBLE` | Un véhicule neuf n'est ni loué ni en maintenance. |
| `idVehicule` | `null` | Attribué par MySQL à l'enregistrement (nouvelle ligne, pas une mise à jour). |
| `immatriculation` | `null` puis fournie par le client | Unique : elle ne peut pas être copiée. |
| `reservations` | vide | Historique propre au véhicule d'origine. |

### 3.3 Le client — `service/VehiculeServiceImpl.java`

```java
@Override
@Transactional
public VehiculeResponse dupliquer(Long id, VehiculeDuplicationRequest request) {
    Vehicule modele = findOrThrow(id);           // 1. le prototype (404 s'il n'existe pas)
    Vehicule copie = modele.copier();            // 2. le prototype se copie lui-même
    copie.setImmatriculation(request.immatriculation()); // 3. seule donnée qui change
    return vehiculeMapper.toResponse(vehiculeRepository.save(copie)); // 4. INSERT, réponse DTO
}
```

### 3.4 L'endpoint — `web/controller/VehiculeController.java`

```java
@Operation(summary = "Dupliquer un véhicule (patron Prototype)")
@PostMapping("/{id}/duplication")
@ResponseStatus(HttpStatus.CREATED)
public VehiculeResponse dupliquer(@PathVariable Long id,
                                  @Valid @RequestBody VehiculeDuplicationRequest request) {
    return vehiculeService.dupliquer(id, request);
}
```

```java
// web/dto/VehiculeDuplicationRequest.java
public record VehiculeDuplicationRequest(
        @NotBlank(message = "L'immatriculation de la copie est obligatoire")
        @Size(max = 20, message = "L'immatriculation ne doit pas dépasser 20 caractères")
        String immatriculation
) {
}
```

## 4. Schéma

```
 Postman                Controller              Service                        Vehicule (prototype)        MySQL
   │ POST /vehicules/1/duplication               │                                    │                       │
   │ {"immatriculation":"200 TU 1234"}           │                                    │                       │
   │──────────────────────▶│ dupliquer(1, req)   │                                    │                       │
   │                       │────────────────────▶│ findOrThrow(1) ───────────────────────── SELECT id=1 ─────▶│
   │                       │                     │◀───────────── véhicule n°1 (modèle) ─────────────────────── │
   │                       │                     │ modele.copier() ──────────────────▶│                       │
   │                       │                     │                                    │ new Vehicule()        │
   │                       │                     │                                    │ + marque, modèle,     │
   │                       │                     │                                    │   tarif, agence,      │
   │                       │                     │◀──────────────────── copie ────────│   équipements         │
   │                       │                     │ copie.setImmatriculation("200 TU 1234")                    │
   │                       │                     │ save(copie) ─────────────────────────────── INSERT ───────▶│
   │◀──── 201 Created ─────│◀── VehiculeResponse │                     (nouvel id, même modèle)               │
```

```
        «interface»
      Prototype<T>
     + copier() : T
            ▲
            │ implements
        Vehicule  ◀──── utilise ──── VehiculeServiceImpl.dupliquer()
     + copier()
```

## 5. Démonstration pendant la présentation

Application lancée (IntelliJ), Postman avec l'environnement `local`, collection `AutoLoc` déjà exécutée une fois
(dossier **Véhicules** : requêtes « Dupliquer un véhicule (patron Prototype) », « … inexistant - 404 »,
« … sans immatriculation - 400 »).

**Étape 1 — montrer le modèle** : `GET {{baseUrl}}/api/vehicules/1`

```json
{ "idVehicule": 1, "immatriculation": "TU407681885616", "marque": "Kia", "modele": "Picanto",
  "categorie": "CITADINE", "tarifJournalier": 70.0, "statut": "DISPONIBLE",
  "idAgence": 1, "nomAgence": "AutoLoc Centre-Ville",
  "equipements": [ { "idEquipement": 1, "libelle": "GPS intégré" } ] }
```

**Étape 2 — dupliquer** : `POST {{baseUrl}}/api/vehicules/1/duplication`

```json
{ "immatriculation": "200 TU 1234" }
```

Réponse **201 Created** (exemple obtenu pendant les tests ; l'identifiant généré varie chez vous) :

```json
{ "idVehicule": 16, "immatriculation": "200 TU 1234", "marque": "Kia", "modele": "Picanto",
  "categorie": "CITADINE", "tarifJournalier": 70.0, "statut": "DISPONIBLE",
  "idAgence": 1, "nomAgence": "AutoLoc Centre-Ville",
  "equipements": [ { "idEquipement": 1, "libelle": "GPS intégré" } ] }
```

➜ **Nouvel identifiant, nouvelle immatriculation, tout le reste copié.**

**Étape 3 — les cas d'erreur** :

| Requête | Réponse |
|---|---|
| Même `POST` une 2ᵉ fois (immatriculation déjà utilisée) | **409** — immatriculation unique |
| `POST /api/vehicules/9999999/duplication` | **404** — « Vehicule introuvable avec l'identifiant 9999999 » |
| Corps `{ "immatriculation": "" }` | **400** — `erreurs.immatriculation` : « L'immatriculation de la copie est obligatoire » |

Version `curl` (port 8081) :

```bash
curl -X POST http://localhost:8081/api/vehicules/1/duplication \
     -H "Content-Type: application/json" \
     -d '{"immatriculation":"200 TU 1234"}'
```

## 6. Points à expliquer à l'oral

- **Pourquoi pas `clone()` / `Cloneable` ?** `Object.clone()` est protégé, lève une exception contrôlée, fait une
  copie superficielle silencieuse et contourne les constructeurs ; SonarQube le signale (`java:S2975`,
  *Remove this "clone" implementation*). Avec une entité JPA, il copierait aussi l'`id` → `save()` ferait un
  **UPDATE** de l'original au lieu d'un **INSERT**. Une méthode `copier()` explicite choisit attribut par
  attribut ce qui est copié.
- **Copie superficielle vs profonde** : l'agence et les équipements sont partagés (référence), mais la collection
  d'équipements est nouvelle. On ne copie pas l'agence elle-même : ce serait créer une nouvelle agence.
- **Prototype GoF ≠ `@Scope("prototype")` de Spring** : le scope Spring `prototype` signifie seulement que le
  conteneur crée un **nouveau bean à chaque demande** (au lieu du singleton par défaut) — il ne copie aucun objet.
  Le patron GoF, lui, crée un objet **en copiant un modèle**. Dans AutoLoc, tous les beans (services,
  contrôleurs, mappers) restent des singletons sans état (Atelier 4) ; le Prototype s'applique aux **objets
  métier**.
- **Avantages** : création rapide d'objets semblables, moins de saisie et d'erreurs, la logique de copie est au
  même endroit (dans l'objet) ; ajouter un attribut au véhicule = mettre à jour `copier()` uniquement.
