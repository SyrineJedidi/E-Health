# patient-service (eHealth)

Microservice **Patient** de la plateforme hospitalière **eHealth**. Il expose une API REST pour la gestion des patients (CRUD, recherche, dossier médical agrégé avec les rendez-vous via **appointment-service**).

## Prérequis

- **Java 17**
- **Maven** : pas obligatoire dans le PATH — ce projet inclut le **Maven Wrapper** (`mvnw` / `mvnw.cmd`) qui télécharge Maven automatiquement au premier lancement.
- **Eureka Server** sur `http://localhost:8761` (recommandé pour l’enregistrement et l’appel Feign vers les autres services)
- **Config Server** sur `http://localhost:8888` (optionnel : l’import est `optional`, l’application démarre sans lui)

## Versions

| Technologie   | Version   |
|---------------|-----------|
| Java          | 17        |
| Spring Boot   | 3.2.0     |
| Spring Cloud  | 2023.0.0  |

## Démarrage

### Avec Eureka (recommandé)

1. Démarrer **eureka-server** (`8761`).
2. Démarrer **appointment-service** (`8083`) si vous souhaitez des rendez-vous réels dans le dossier médical.
3. Dans ce répertoire (PowerShell / CMD Windows) :

```powershell
.\mvnw.cmd spring-boot:run
```

Sous Linux ou macOS : `./mvnw spring-boot:run`

Le service écoute sur le port **8091** par défaut (`SERVER_PORT` pour surcharger) et s’enregistre sur Eureka.

### Sans Eureka (local / tests)

Pour éviter les erreurs de connexion au serveur d’annuaire :

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--eureka.client.enabled=false"
```

> Sans Eureka, **Feign** ne résout pas `appointment-service` par nom : les appels au dossier médical peuvent échouer ou utiliser le **fallback** (liste vide, message *Rendez-vous indisponibles*).

### Sans Config Server

Aucune action requise : `spring.config.import` pointe vers un config server **optionnel**.

## URLs utiles

| Ressource        | URL |
|------------------|-----|
| API              | `http://localhost:8091` |
| Console H2       | `http://localhost:8091/h2-console` (JDBC URL : `jdbc:h2:mem:patientdb`, user `sa`, mot de passe vide) |
| OpenAPI (JSON)   | `http://localhost:8091/v3/api-docs` |
| Swagger UI       | `http://localhost:8091/swagger-ui.html` |
| Actuator health  | `http://localhost:8091/actuator/health` |

## Endpoints REST

Toutes les réponses sont encapsulées dans `ApiResponse` : `{ "success": true|false, "message": "...", "data": ..., "timestamp": "..." }`.

| Méthode | URL | Description | Exemple de réponse (champ `data`) |
|---------|-----|-------------|-------------------------------------|
| GET | `/api/patients` | Liste tous les patients | `[ { "id": 1, "nom": "...", ... } ]` |
| GET | `/api/patients/{id}` | Détail d’un patient | `{ "id": 1, "nom": "Bernard", ... }` |
| POST | `/api/patients` | Création (corps JSON validé) | Patient créé |
| PUT | `/api/patients/{id}` | Mise à jour (champs non nuls du JSON appliqués) | Patient mis à jour |
| DELETE | `/api/patients/{id}` | Suppression | `null` |
| GET | `/api/patients/search?nom=` | Recherche par nom (contient, insensible à la casse) | Liste de patients |
| GET | `/api/patients/{id}/dossier` | Dossier médical : patient + rendez-vous | `{ "patient": {...}, "rendezVous": [...], "message": "Dossier complet" \| "Rendez-vous indisponibles" }` |
| POST | `/api/patients/batch` | Corps : `[1,2,3]` — patients correspondants | Liste de `PatientDTO` |

**Exemple POST création**

```http
POST /api/patients
Content-Type: application/json

{
  "nom": "Petit",
  "prenom": "Julie",
  "email": "julie.petit@example.com",
  "telephone": "0600000000",
  "adresse": "1 rue Test",
  "dateNaissance": "2000-01-15",
  "groupeSanguin": "B+"
}
```

## Communication avec appointment-service

- Le client **OpenFeign** `AppointmentClient` appelle `GET /api/rendezvous/patient/{patientId}` sur le service enregistré sous le nom **`appointment-service`** (port **8083** dans l’écosystème eHealth).
- En cas d’indisponibilité ou d’erreur, **`AppointmentClientFallback`** retourne une liste vide et journalise un avertissement ; le dossier médical indique alors *Rendez-vous indisponibles*.
- Le **fallback Feign** (`AppointmentClientFallback`) est pris en charge via **Resilience4j** (`spring-cloud-starter-circuitbreaker-resilience4j`) et `feign.circuitbreaker.enabled=true` (recommandé avec Spring Cloud OpenFeign 4+).

## Données de démo

Le fichier `data.sql` insère **5 patients** au démarrage (base H2 en mémoire, `ddl-auto: create-drop`).

## Licence

Projet pédagogique / équipe eHealth.
