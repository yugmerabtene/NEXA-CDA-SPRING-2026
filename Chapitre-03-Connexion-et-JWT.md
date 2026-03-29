# Chapitre 03 - Connexion et JWT (Security + DTO + Frontend Bootstrap)

## 1) Objectif du chapitre

Ce chapitre rend l'application authentifiable et protegee.

Resultat attendu:

- endpoint `POST /api/auth/login` operationnel,
- token JWT genere et valide,
- endpoint protege `GET /api/users/me`,
- frontend HTML/CSS/JavaScript + Bootstrap capable de tester le flow,
- tests d'integration verts.

Etat du chapitre dans le repository:

- `main` contient le resultat final du chapitre et des chapitres suivants.
- Les extraits ci-dessous representent la cible pedagogique de l'etape JWT.

---

## 2) Theorie detaillee

### 2.1 Pourquoi JWT

JWT est adapte a une API REST stateless:

- le serveur ne stocke pas de session,
- chaque requete transporte son token,
- facile a scaler horizontalement.

### 2.2 Pipeline Spring Security avec JWT

Pipeline du chapitre:

1. `POST /api/auth/login` valide les credentials.
2. Le backend retourne un JWT signe.
3. Le frontend stocke ce token en memoire.
4. Le frontend envoie `Authorization: Bearer <token>`.
5. `JwtAuthenticationFilter` valide le token et remplit le contexte securite.
6. Les endpoints proteges deviennent accessibles.

Mapping direct dans le code:

- authentification login: `authapp-code/src/main/java/com/nexa/cda/authapp/auth/service/AuthService.java`
- generation token: `authapp-code/src/main/java/com/nexa/cda/authapp/security/JwtService.java`
- verification bearer token: `authapp-code/src/main/java/com/nexa/cda/authapp/security/JwtAuthenticationFilter.java`
- regles d'acces HTTP: `authapp-code/src/main/java/com/nexa/cda/authapp/config/SecurityConfig.java`

### 2.3 Separation des couches (important)

- `dto` pour `LoginRequestDto`/`LoginResponseDto`/`MeResponseDto`,
- `service` pour authentifier et generer token,
- `security` pour parser/valider token,
- `controller` pour exposer l'API.

---

## 3) Pratique step by step

### Etape 1 - Ajouter les dependances JWT

Fichier: `authapp-code/pom.xml`

Dependances ajoutees:

- `jjwt-api`
- `jjwt-impl`
- `jjwt-jackson`

Explication:

- API pour coder proprement,
- implementation runtime,
- support JSON des claims.

### Etape 2 - Ajouter les proprietes JWT

Fichier: `authapp-code/src/main/resources/application.yml`

```yaml
app:
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration-seconds: ${JWT_EXPIRATION_SECONDS:3600}
```

Explication:

- cle et expiration externalisees,
- en `dev`, un fallback local est defini dans `application-dev.yml` pour la formation,
- override possible via variables d'environnement.

### Etape 3 - Construire la couche security

Classes ajoutees:

- `SecurityProperties`
- `JwtService`
- `CustomUserDetailsService`
- `JwtAuthenticationFilter`

Le role de chaque classe:

- `JwtService`: genere et valide les tokens,
- `CustomUserDetailsService`: charge l'utilisateur depuis la base,
- `JwtAuthenticationFilter`: lit le bearer token sur chaque requete,
- `SecurityProperties`: injecte la configuration.

### Etape 4 - Durcir `SecurityConfig`

`SecurityConfig` a ete mis a jour pour:

- passer en `STATELESS`,
- autoriser seulement:
  - `/api/health`
  - `/api/auth/register`
  - `/api/auth/login`
  - resources frontend (`/`, `/index.html`, `/app.js`, `/app.css`)
- proteger le reste,
- brancher `JwtAuthenticationFilter`.

### Etape 5 - Ajouter les DTO login

Classes ajoutees:

- `auth/dto/LoginRequestDto`
- `auth/dto/LoginResponseDto`

Explication:

- la couche dto garde un contrat API explicite,
- facilite l'integration frontend vanilla JS.

### Etape 6 - Ajouter la logique login dans `AuthService`

Flow implemente:

1. normaliser email,
2. authentifier via `AuthenticationManager`,
3. charger user,
4. generer JWT,
5. retourner `LoginResponseDto`.

Erreur metier:

- en credentials invalides -> `InvalidCredentialsException` (`401`).

### Etape 7 - Exposer `POST /api/auth/login`

`AuthController` expose maintenant:

- `POST /api/auth/register`
- `POST /api/auth/login`

### Etape 8 - Ajouter endpoint protege `/api/users/me`

Classes ajoutees:

- `user/dto/MeResponseDto`
- `user/service/UserService`
- `user/controller/UserController`

Le controller lit `Authentication`, delegue au service, et renvoie un JSON standard.

### Etape 9 - Frontend impose (HTML/CSS/JS + Bootstrap)

Fichiers ajoutes:

- `authapp-code/src/main/resources/static/index.html`
- `authapp-code/src/main/resources/static/app.css`
- `authapp-code/src/main/resources/static/app.js`

Ce frontend permet:

- inscription,
- connexion,
- chargement du profil `/api/users/me` avec token.

Choix securite frontend du cours:

- le token est garde en memoire JavaScript (`let accessToken = null`),
- il n'est pas persiste en `localStorage` dans ce TP,
- ce choix limite l'exposition en cas d'attaque XSS persistante.

### Etape 10 - Tester

Commande:

```bash
cd authapp-code
./mvnw test
```

Tests d'integration verifies:

- register ok,
- login + token ok,
- `/api/users/me` protege,
- login invalide -> `401`.

Comportements HTTP importants:

- login invalide: `401` + `INVALID_CREDENTIALS`,
- endpoint protege sans token: `401` + `UNAUTHORIZED`,
- endpoint protege avec token invalide: `401` + `UNAUTHORIZED`.

Exemple de payload JWT (dechiffre):

```json
{
  "username": "nexa-user",
  "role": "USER",
  "sub": "nexa.user@example.com",
  "iat": 1774800000,
  "exp": 1774803600
}
```

### Etape 11 - Exemple complet fonctionnel (SecurityConfig + JwtService)

Fichier: `authapp-code/src/main/java/com/nexa/cda/authapp/config/SecurityConfig.java` (extrait central)

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable()) // API stateless, pas de formulaire serveur
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(restAuthenticationEntryPoint) // Reponse JSON 401
                    .accessDeniedHandler(restAccessDeniedHandler)) // Reponse JSON 403
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/index.html", "/app.js", "/app.css", "/api/health", "/api/auth/register", "/api/auth/login").permitAll()
                    .anyRequest().authenticated()) // Tout le reste est protege
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

Fichier: `authapp-code/src/main/java/com/nexa/cda/authapp/security/JwtService.java` (extrait central)

```java
public String generateToken(String subject, Map<String, Object> extraClaims) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(properties.getExpirationSeconds());

    return Jwts.builder()
            .claims(extraClaims) // role + username
            .subject(subject) // email utilisateur
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt)) // expiration obligatoire
            .signWith(signingKey()) // Signature HMAC avec secret env
            .compact();
}

public boolean isTokenValid(String token, String expectedSubject) {
    Claims claims = extractClaims(token);
    return expectedSubject.equals(claims.getSubject()) && claims.getExpiration().after(new Date());
}
```

Explication complete de l'exemple:

- `SecurityConfig` definit clairement routes publiques et routes protegees.
- `JwtService` encapsule generation et validation pour eviter la duplication.
- Le couple filtre + service JWT garantit une authentification stateless robuste.

---

### 3.1 Quiz rapide (validation)

1. Difference entre `401` et `403` ?
2. Pourquoi un JWT doit avoir une date d'expiration ?
3. Quel composant lit le header `Authorization` dans ce projet ?

Corrige synthese:

- `401`: non authentifie; `403`: authentifie mais non autorise.
- Limiter la fenetre d'exploitation d'un token vole.
- `JwtAuthenticationFilter`.

---

## 4) TD du chapitre 3

### TD-03 - UX de securite cote frontend Bootstrap

Travail demande:

1. Definir les messages UI pour `401` et `403`.
2. Definir une strategie de stockage token (memoire vs localStorage) et justifier.
3. Definir un schema de deconnexion simple.

---

## 5) TP du chapitre 3

### TP-03 - Authentification complete et testee

Taches:

1. Ajouter couche security JWT.
2. Implementer login.
3. Proteger `/api/users/me`.
4. Brancher frontend vanilla JS + Bootstrap.
5. Lancer les tests automatiques.

Definition of Done:

- login retourne un token,
- `/api/users/me` inaccessible sans token,
- `/api/users/me` accessible avec token valide,
- UI de test frontend fonctionnelle,
- tests verts.

---

## 6) Validation de fin de chapitre

- [ ] JWT genere et valide
- [ ] `POST /api/auth/login` operationnel
- [ ] `GET /api/users/me` protege
- [ ] frontend HTML/CSS/JS + Bootstrap fonctionnel
- [ ] tests automatises passes

---

## 7) Transition vers le chapitre 4

Le chapitre 4 renforcera la qualite:

- strategie de tests plus large,
- tests supplementaires de robustesse,
- securisation des contrats API.
