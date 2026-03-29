# Chapitre 01 - Fondations et architecture multi-layer

## 1) Objectif du chapitre

Ce premier chapitre pose une base solide pour tout le reste de la formation.
L'objectif est de mettre en place rapidement un projet Spring Boot propre, structure en couches, avec un premier endpoint technique et une couche d'exceptions reusable.

En fin de chapitre, vous devez avoir:

- un projet demarrable,
- une architecture multi-layer claire,
- un endpoint `GET /api/health`,
- un format de reponse JSON standard,
- un handler global d'exceptions,
- des tests qui passent.

Etat du chapitre dans le repository:

- `main` contient le resultat final des 5 chapitres.
- Ce chapitre decrit le socle minimal a atteindre a l'etape 1.

---

## 2) Theorie 

## 2.1 Pourquoi Spring Boot

Spring Boot permet de lancer tres vite une application robuste sans ecrire de configuration lourde.

Les avantages concrets:

- auto-configuration selon les dependances detectees,
- demarrage rapide avec serveur embarque,
- conventions communes pour l'equipe,
- ecosysteme mature (Web, Data, Security, Validation, Test).

Pour une formation, cela permet de concentrer l'effort sur l'architecture et le metier, pas sur la plomberie.

## 2.2 Architecture multi-layer (vision claire)

Architecture cible du projet:

- `controller`: recoit les requetes HTTP et retourne des reponses HTTP,
- `service`: contient la logique metier,
- `dao`: abstraction d'acces metier aux donnees (introduite au chapitre 2),
- `repository`: dialogue avec la base,
- `model`: represente les objets persistants,
- `dto`: definit les contrats d'entree/sortie API,
- `mapper`: convertit `dto <-> model`,
- `exceptions`: porte les erreurs metier + la gestion globale,
- `config`: centralise les configurations transverses,
- `common`: composants partages (reponse standard, utilitaires).

Regle d'or: pas de logique metier dans les controllers.

## 2.3 Pourquoi une couche exceptions des le debut

Ne pas centraliser les erreurs au debut cree rapidement:

- des reponses incoherentes,
- des `try/catch` repetitifs,
- une API difficile a consommer.

Avec une couche `exceptions`, vous obtenez:

- des erreurs homogenes,
- des codes HTTP coherents,
- une maintenance plus simple.

---

## 3) Pratique step by step (mise en place rapide)

## Etape 1 - Generer le projet rapidement

Commande utilisee:

```bash
curl -s "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=3.5.7&baseDir=authapp-code&groupId=com.nexa.cda&artifactId=authapp-code&name=authapp-code&description=NEXA%20CDA%20Spring%202026&packageName=com.nexa.cda.authapp&packaging=jar&javaVersion=21&dependencies=web,validation,data-jpa,security,flyway,postgresql,h2" -o authapp-code.zip
```

Explication:

- Java 21 pour rester sur une base moderne et stable,
- dependances deja pretes pour les prochains chapitres (JPA, Security, Flyway).

## Etape 2 - Configurer l'application

Fichier: `authapp-code/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: authapp-code
  profiles:
    default: dev

server:
  port: 8080
```

Explication du code:

- `profiles.default: dev` prepare la separation des environnements,
- `port: 8080` fixe un point d'entree standard pour les tests locaux.

## Etape 3 - Ajouter un contrat de reponse API standard

Fichier: `authapp-code/src/main/java/com/nexa/cda/authapp/common/api/ApiResponse.java`

```java
public record ApiResponse<T>(Instant timestamp, String message, T data) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(Instant.now(), message, data);
    }
}
```

Explication du code:

- le `record` simplifie le code et impose un format immuable,
- `success(...)` evite de dupliquer la construction de reponses.

## Etape 4 - Ajouter le endpoint de sante

Fichier: `authapp-code/src/main/java/com/nexa/cda/authapp/common/controller/HealthController.java`

```java
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.success("Service operational", Map.of("status", "UP")));
    }
}
```

Explication du code:

- endpoint technique simple pour valider le demarrage,
- reponse deja au format standard de l'application.

## Etape 5 - Mettre en place la couche exceptions

Fichier: `authapp-code/src/main/java/com/nexa/cda/authapp/common/exception/BusinessException.java`

```java
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode code;

    public BusinessException(String message, HttpStatus status, ErrorCode code) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
```

Explication du code:

- exception metier generic reusable,
- transporte le code HTTP et un code d'erreur applicatif.

Fichier: `authapp-code/src/main/java/com/nexa/cda/authapp/common/exception/GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                ex.getMessage(),
                ex.getCode().name(),
                List.of()
        );
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                "Unexpected error",
                ErrorCode.INTERNAL_ERROR.name(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

Explication du code:

- centralise tous les retours d'erreurs,
- garantit un format JSON stable pour le frontend.

## Etape 6 - Configurer la securite minimale du chapitre 1

Fichier: `authapp-code/src/main/java/com/nexa/cda/authapp/config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
```

Explication du code:

- en chapitre 1, on ouvre les routes pour accelerer la mise en place,
- la securite JWT stricte sera introduite en chapitre 3.

Note d'alignement repository:

- le code actuel du repository contient deja la version securite JWT stricte du chapitre 3,
- ce bloc est conserve ici comme etat pedagogique de fin de chapitre 1.

## Etape 7 - Tester rapidement

Commandes:

```bash
cd authapp-code
./mvnw test
./mvnw spring-boot:run
curl http://localhost:8080/api/health
```

Explication:

- `test` valide que le socle est stable,
- `spring-boot:run` + `curl` confirment le comportement reel.

---

## 3.1 Quiz rapide (validation)

1. Pourquoi separer `controller` et `service` ?
2. Quel est le role de `@RestControllerAdvice` ?
3. Pourquoi standardiser les reponses JSON des le chapitre 1 ?

Corrige synthese:

- Separation des couches = code testable, lisible et maintenable.
- `@RestControllerAdvice` centralise la gestion des erreurs.
- Un format JSON stable simplifie le frontend et les tests.

---

## 4) TD du chapitre 1

### TD-01 - Concevoir les frontieres de couches

Travail demande:

1. Expliquer le role de chaque couche du projet.
2. Proposer 3 exemples de mauvaises dependances (et pourquoi elles sont interdites).
3. Proposer 3 regles de revue de code pour garantir l'architecture.

Livrable attendu:

- une fiche de regles d'architecture (1 page).

---

## 5) TP du chapitre 1

### TP-01 - Monter un socle projet propre et testable

Taches:

1. Generer le projet Spring Boot.
2. Remplacer `application.properties` par `application.yml`.
3. Ajouter `ApiResponse` et `ApiErrorResponse`.
4. Ajouter `HealthController`.
5. Ajouter `BusinessException` + `GlobalExceptionHandler`.
6. Ajouter la config securite minimaliste.
7. Ajouter un test endpoint `/api/health`.

Definition of Done:

- `/api/health` repond `200`,
- le format JSON est standardise,
- les tests passent,
- la structure multi-layer est en place.

---

## 6) Validation de fin de chapitre

- [ ] Projet demarrable localement
- [ ] Endpoint health disponible
- [ ] Couche exceptions active
- [ ] Test `HealthControllerTest` passe
- [ ] Architecture multi-layer posee pour la suite

---

## 7) Transition vers le chapitre 2

Au chapitre 2, on implementera la premiere fonctionnalite metier complete:

- couche `model` avec `AppUser` et enum `UserRole`,
- couche `repository` JPA,
- endpoint d'inscription,
- validation et erreurs metier dediees.
