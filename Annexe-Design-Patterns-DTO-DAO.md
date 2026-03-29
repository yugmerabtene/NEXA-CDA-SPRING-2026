# Annexe - Design Patterns DTO et DAO

## 1) Pourquoi ces deux patterns dans ce projet

Le projet suit une architecture multi-layer pour garder le code lisible, testable et evolutif.
Les patterns `DTO` et `DAO` jouent deux roles differents et complementaires:

- `DTO` protege le contrat API (ce que le client envoie/recoit)
- `DAO` protege la logique metier des details techniques de persistence

---

## 2) Pattern DTO (Data Transfer Object)

## 2.1 Definition

Un DTO est un objet de transfert de donnees dedie aux echanges entre couches (souvent controller <-> service <-> client HTTP).

Dans ce projet, les DTO sont des `record` Java situes dans:

- `authapp-code/src/main/java/com/nexa/cda/authapp/auth/dto`
- `authapp-code/src/main/java/com/nexa/cda/authapp/user/dto`

Exemples:

- `RegisterRequestDto`
- `RegisterResponseDto`
- `LoginRequestDto`
- `LoginResponseDto`
- `MeResponseDto`

## 2.2 Utilite concrete

- Eviter d'exposer directement les entites JPA (`model`) a l'exterieur.
- Controler finement ce que le frontend recoit.
- Attacher les validations d'entree (`@Email`, `@NotBlank`, `@Size`) au contrat API.
- Stabiliser l'API meme si le schema de base evolue.

## 2.3 Ce que DTO n'est pas

- Ce n'est pas une entite persistence.
- Ce n'est pas un objet metier riche.
- Ce n'est pas la representation UI HTML.

---

## 3) Pattern DAO (Data Access Object)

## 3.1 Definition

DAO est une abstraction orientee metier pour acceder aux donnees, au-dessus des details de persistence.

Dans ce projet:

- interface: `authapp-code/src/main/java/com/nexa/cda/authapp/user/dao/UserDao.java`
- implementation: `authapp-code/src/main/java/com/nexa/cda/authapp/user/dao/UserDaoJpa.java`
- support technique: `UserRepository` (Spring Data JPA)

## 3.2 Utilite concrete

- Decoupler la couche service de Spring Data.
- Faciliter les tests unitaires (mock d'un `UserDao` simple).
- Remplacer plus facilement l'implementation d'acces aux donnees (JPA, SQL natif, API externe) sans casser le service.

## 3.3 DAO vs Repository

- `Repository` (Spring Data) = abstraction technique de persistence.
- `DAO` = abstraction d'acces metier adoptee par l'application.

Ici, `UserDaoJpa` adapte `UserRepository` vers les besoins du service.

---

## 4) Pourquoi combiner DTO + DAO

Ensemble, ces patterns rendent l'architecture plus robuste:

- DTO limite le couplage avec le client API.
- DAO limite le couplage avec le moteur de persistence.
- La logique metier au centre (`service`) reste stable.

---

## 5) Regles de revue de code (pratiques)

- Un controller ne retourne pas une entite JPA.
- Un service depend d'un DAO, pas directement d'un detail technique si abstraction metier existe.
- Les validations d'entree vivent dans les DTO.
- Les conversions DTO <-> Model passent par un mapper dedie.

---

## 6) Extrait de flux complet dans ce projet

1. `AuthController` recoit un `RegisterRequestDto`
2. `AuthService` applique les regles metier
3. `UserDao` gere l'acces donnees
4. `AuthDtoMapper` convertit `model` -> `RegisterResponseDto`
5. Le controller retourne une reponse API stable au frontend
