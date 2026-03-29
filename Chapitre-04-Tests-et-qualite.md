# Chapitre 04 - Tests et qualite (Unitaires + Integration + Contrats)

## 1) Objectif du chapitre

Ce chapitre consolide la qualite logicielle du projet.

Resultat attendu:

- des tests unitaires sur les couches `service`,
- des tests d'integration sur l'API securisee,
- des tests de contrat de reponse erreur/succes,
- un test de livraison frontend (HTML/CSS/JS + Bootstrap),
- une base de regression fiable avant Docker (chapitre 5).

---

## 2) Theorie detaillee

## 2.1 Pourquoi tester a plusieurs niveaux

Un seul type de test ne suffit pas.

On utilise ici 3 niveaux:

- **Unitaire**: valide une classe isolee (rapide, precis).
- **Integration API**: valide controllers + security + DB + serialization.
- **Contrat de vue**: valide le rendu minimal de la page frontend.

Cette combinaison reduit fortement le risque de regression.

## 2.2 Difference unitaire vs integration

- Unitaire:
  - mocks,
  - pas de serveur complet,
  - cible la logique metier pure.
- Integration:
  - contexte Spring charge,
  - `MockMvc` simule HTTP,
  - verifie le comportement reel observable.

## 2.3 Ce qu'on veut proteger avant production

Dans ce projet, les chemins critiques sont:

1. inscription,
2. connexion,
3. acces a `/api/users/me`,
4. affichage frontend minimal.

## 2.4 Contexte d'execution des tests

Le profil de test utilise `application-test.yml`:

- H2 en mode PostgreSQL,
- migrations Flyway executees,
- secret JWT de test fixe.

Ce choix permet des tests rapides, deterministes et proches du comportement SQL cible.

---

## 3) Pratique step by step

## Etape 1 - Ajouter des tests unitaires sur `AuthService`

Fichier: `authapp-code/src/test/java/com/nexa/cda/authapp/auth/service/AuthServiceUnitTest.java`

Scenarios testes:

- register succes,
- register email deja utilise,
- login succes,
- login echec credentials.

Extrait:

```java
when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));
assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
```

Explication:

- on valide le mapping d'une erreur technique de securite vers une erreur metier stable pour l'API.

## Etape 2 - Ajouter des tests unitaires sur `UserService`

Fichier: `authapp-code/src/test/java/com/nexa/cda/authapp/user/service/UserServiceUnitTest.java`

Scenarios testes:

- recuperation profil utilisateur,
- utilisateur inexistant -> `UserNotFoundException`.

Explication:

- cette couche doit rester purement metier, testable sans demarrer tout Spring.

## Etape 3 - Renforcer les tests d'integration security

Fichier principal deja present:

- `authapp-code/src/test/java/com/nexa/cda/authapp/auth/controller/AuthSecurityFlowIntegrationTest.java`

Flux valide:

- register -> login -> me,
- acces refuse sans token,
- login invalide -> `401` + `INVALID_CREDENTIALS`.

Explication:

- c'est le filet de securite principal pour le coeur du projet.

## Etape 4 - Tester la vue frontend Bootstrap

Fichier ajoute:

- `authapp-code/src/test/java/com/nexa/cda/authapp/common/controller/FrontendViewIntegrationTest.java`

Ce test verifie:

- `GET /index.html` retourne `200`,
- la page contient Bootstrap CDN,
- le formulaire d'inscription est present,
- `GET /app.js` est bien servi.

Explication:

- ce test confirme que la "view" HTML livree est bien servie et exploitable.

## Etape 5 - Executer toute la suite

Commande:

```bash
cd authapp-code
./mvnw test
```

Commandes ciblees pour debug rapide:

```bash
./mvnw -Dtest=AuthServiceUnitTest test
./mvnw -Dtest=AuthSecurityFlowIntegrationTest test
```

Attendu:

- build success,
- tous les tests passent,
- pas de regression sur register/login/me/frontend.

---

## 3.1 Quiz rapide (validation)

1. Pourquoi combiner tests unitaires et integration ?
2. Que valide `FrontendViewIntegrationTest` ?
3. Quel test protege le flux complet auth securise ?

Corrige synthese:

- Les unitaires isolent la logique; les integrations valident le comportement reel.
- La disponibilite des ressources frontend critiques (`/index.html`, `/app.js`).
- `AuthSecurityFlowIntegrationTest`.

---

## 4) TD du chapitre 4

### TD-04 - Strategie de couverture pragmatique

Travail demande:

1. Classer les cas critiques en unitaire/integration.
2. Proposer un seuil de couverture cible realiste.
3. Definir les tests obligatoires avant merge.

Livrable:

- matrice de tests priorisee.

Matrice risque -> test existant:

- risque "auth casse": `AuthSecurityFlowIntegrationTest`
- risque "contrat register/login": `AuthControllerIntegrationTest`
- risque "regles metier service": `AuthServiceUnitTest`, `UserServiceUnitTest`
- risque "vue indisponible": `FrontendViewIntegrationTest`

---

## 5) TP du chapitre 4

### TP-04 - Industrialiser les tests du projet

Taches:

1. Ecrire/maintenir les tests unitaires des services.
2. Completer les tests d'integration API securisee.
3. Ajouter un test de vue frontend.
4. Faire passer `./mvnw test` en continu.

Definition of Done:

- tests unitaires verts,
- tests integration verts,
- test frontend vert,
- aucune regression sur les endpoints critiques.

---

## 6) Validation de fin de chapitre

- [ ] `AuthServiceUnitTest` passe
- [ ] `UserServiceUnitTest` passe
- [ ] `AuthSecurityFlowIntegrationTest` passe
- [ ] `AuthControllerIntegrationTest` passe
- [ ] `FrontendViewIntegrationTest` passe
- [ ] `./mvnw test` global passe

---

## 7) Transition vers le chapitre 5

Le chapitre 5 dockerise l'application et valide les memes flows en environnement containerise.
