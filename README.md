# Spring & SpringBoot NEXA-CDA-2026

Programme intensif de 5 jours base sur un fil rouge: creation d'une application Spring Boot avec inscription, connexion JWT, architecture multi-layer stricte, tests automatises et dockerisation complete.

## Objectif de la formation

Construire une application backend professionnelle, de zero a la livraison, en appliquant des standards d'architecture, de qualite logicielle, de securite et d'industrialisation.

## Fil rouge

Application cible: `AuthApp`

- Inscription utilisateur
- Connexion utilisateur
- Endpoint protege profil utilisateur (`/me`)
- Gestion des roles (`USER`, `ADMIN`)
- Dockerisation complete (`API + PostgreSQL`)

Stack frontend imposee pour l'integration du projet:

- HTML
- CSS
- JavaScript (vanilla)
- Bootstrap (sans framework frontend supplementaire)

Modeles et enum metier obligatoires du fil rouge:

- `AppUser` (modele utilisateur principal)
- `UserRole` (enum des roles: `USER`, `ADMIN`)

## Architecture de reference (multi-layer)

Les chapitres appliquent en continu cette architecture:

- `controller`: exposition HTTP/REST
- `service`: logique metier
- `repository`: acces persistence
- `model`: objets metier persistants
- `enum`: types metier fermes (exemple: `UserRole`)
- `dto`: contrats entree/sortie API
- `view`: contrats d'entree/sortie pour le frontend (HTML/CSS/JS + Bootstrap)
- `mapper`: conversion `dto <-> model`
- `exceptions`: exceptions metier + gestion globale
- `validation`: regles de validation d'entree
- `security`: authentification, autorisation, JWT
- `config`: configuration applicative et technique
- `common`: reponses API communes, utilitaires transverses

## Sommaire des 5 jours

### Jour 1 - Fondations Spring Boot et architecture multi-layer

1. Objectifs pedagogiques du jour
2. Spring Framework vs Spring Boot
3. Architecture multi-layer: roles, frontieres, dependances autorisees
4. Structuration du projet par packages
5. Configuration applicative (`application.yml`, profils)
6. Contrat de reponse API unifie (`ApiResponse`)
7. Couche `exceptions` niveau 1
   - `BusinessException`
   - `GlobalExceptionHandler` (`@RestControllerAdvice`)
8. Endpoint technique `GET /api/health`
9. TD-01: conception architecture
10. TP-01: mise en place du squelette complet
11. Checklist de validation fin de journee

### Jour 2 - Persistence, modele utilisateur et inscription

1. Objectifs pedagogiques du jour
2. Modelisation du domaine utilisateur (`AppUser` + `UserRole` enum)
3. JPA/Hibernate: mapping, contraintes, cycle de vie
4. PostgreSQL et migrations Flyway
5. Couche `repository` et requetes metier
6. DTO d'inscription et validation (`@Valid`, Bean Validation)
7. Couche `mapper` pour l'inscription
8. Service d'inscription (normalisation email, hash password)
9. Endpoint `POST /api/auth/register`
10. Couche `exceptions` niveau 2
    - `EmailAlreadyUsedException`
    - mapping HTTP `400` et `409`
11. TD-02: specification fonctionnelle inscription
12. TP-02: inscription persistante de bout en bout
13. Checklist de validation fin de journee

### Jour 3 - Connexion, JWT et securite applicative

1. Objectifs pedagogiques du jour
2. Spring Security: principes et pipeline
3. Authentification stateless et JWT
4. Couche `security`
   - `SecurityConfig`
   - `JwtService`
   - `JwtAuthenticationFilter`
5. Endpoint `POST /api/auth/login`
6. Endpoint protege `GET /api/users/me`
7. Roles et autorisations bases sur l'enum `UserRole` (`USER`, `ADMIN`)
8. Couche `exceptions` niveau 3
   - `InvalidCredentialsException`
   - gestion `401` et `403`
9. TD-03: matrice d'autorisation
10. TP-03: flow complet register -> login -> me
11. Checklist de validation fin de journee

### Jour 4 - Qualite logicielle, tests et robustesse

1. Objectifs pedagogiques du jour
2. Strategie de tests (unitaires + integration)
3. Tests unitaires de la couche `service`
4. Tests d'integration API (MockMvc)
5. Tests securite (token absent/invalide/expire)
6. Tests de la couche `exceptions` (format et codes)
7. Verification des contrats JSON (succes et erreur)
8. Revue architecture: respect strict des couches
9. TD-04: plan de tests projet
10. TP-04: suite de tests automatises complete
11. Checklist de validation fin de journee

### Jour 5 - Dockerisation, execution complete et finalisation

1. Objectifs pedagogiques du jour
2. Docker fundamentals pour backend Java
3. Dockerfile multi-stage (build + runtime)
4. Docker Compose (`API + PostgreSQL`)
5. Variables d'environnement et profils runtime
6. Verification bout en bout en environnement containerise
7. Checklist de livraison
8. Demonstration finale du fil rouge
9. TD-05: readiness deploiement local
10. TP-05: execution dockerisee + validation finale
11. Bilan et axes d'evolution

## Livrables attendus en fin de formation

- Projet Spring Boot structure en architecture multi-layer
- API REST securisee (inscription + connexion + endpoint protege)
- Modele de roles via enum `UserRole` exploite en securite
- Couche `exceptions` complete et standardisee
- Suite de tests automatises (unitaires + integration)
- Projet dockerise et reproductible
- Documentation technique claire et exploitable

## Criteres de qualite transverses

- Separation stricte des responsabilites entre couches
- Aucune logique metier dans les controllers
- Aucune exposition de donnees sensibles
- Contrats API stables et uniformes
- Tests executes avant chaque livraison de chapitre
- Commits reguliers, lisibles et orientes valeur

## Suite du repository

Les chapitres detailles seront ajoutes en fichiers a la racine:

- `Chapitre-01-Fondations-et-architecture-multi-layer.md`
- `Chapitre-02-Persistence-et-inscription.md`
- `Chapitre-03-Connexion-et-JWT.md`
- `Chapitre-04-Tests-et-qualite.md`
- `Chapitre-05-Dockerisation-et-finalisation.md`
