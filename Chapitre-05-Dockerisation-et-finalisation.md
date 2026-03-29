# Chapitre 05 - Dockerisation et finalisation

## 1) Objectif du chapitre

Ce chapitre transforme le projet en livrable executable partout.

Resultat attendu:

- image Docker de l'API Spring Boot,
- orchestration `API + PostgreSQL` via Docker Compose,
- verification complete du flow register/login/me en conteneurs,
- base de deploiement local reproductible.

Etat du chapitre dans le repository:

- `main` contient le resultat final dockerise.
- Les commandes du chapitre permettent de rejouer la validation en local.

---

## 2) Theorie detaillee

## 2.1 Pourquoi dockeriser en fin de parcours

La dockerisation sert a:

- figer un environnement d'execution stable,
- eliminer les ecarts entre machines,
- simplifier la mise en production,
- accelerer l'integration continue.

## 2.2 Multi-stage build

Un Dockerfile multi-stage separe:

1. le build (JDK + Maven wrapper),
2. le runtime (JRE uniquement).

Avantages:

- image finale plus legere,
- surface d'attaque reduite,
- build plus propre et plus rapide avec cache.

## 2.3 Pourquoi docker-compose

Notre application depend de PostgreSQL.

`docker-compose.yml` permet:

- de lancer les services ensemble,
- de declarer variables d'environnement,
- d'ajouter un healthcheck DB,
- de reproduire le setup en une commande.

---

## 3) Pratique step by step

## Etape 1 - Ajouter le Dockerfile

Fichier: `authapp-code/Dockerfile`

Points cle:

- stage `build` pour compiler le jar,
- stage `runtime` pour executer seulement,
- `ENTRYPOINT` propre sur le jar.

## Etape 2 - Ajouter `.dockerignore`

Fichier: `authapp-code/.dockerignore`

But:

- eviter d'envoyer `target/` et fichiers IDE au daemon Docker,
- accelerer les builds,
- reduire la taille du contexte.

## Etape 3 - Ajouter docker-compose

Fichier: `authapp-code/docker-compose.yml`

Services:

- `db`: PostgreSQL 16
- `app`: API Spring Boot

Table de mapping variables -> configuration Spring:

- `DB_URL` -> `spring.datasource.url` (`application-dev.yml`)
- `DB_USERNAME` -> `spring.datasource.username`
- `DB_PASSWORD` -> `spring.datasource.password`
- `JWT_SECRET` -> `app.security.jwt.secret` (`application.yml`)
- `JWT_EXPIRATION_SECONDS` -> `app.security.jwt.expiration-seconds`

Exemple `.env` local (formation):

```env
DB_URL=jdbc:postgresql://db:5432/authapp
DB_USERNAME=authapp
DB_PASSWORD=authapp
JWT_SECRET=replace_with_a_long_base64_secret
JWT_EXPIRATION_SECONDS=3600
```

Le fichier modele est disponible dans le repository:

- `authapp-code/.env.example`

Parametrage:

- `depends_on` avec `condition: service_healthy`,
- variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`,
- variables JWT (`JWT_SECRET` obligatoire),
- port `8080` expose pour le frontend et les tests API.

## Etape 4 - Construire et lancer

Commandes:

```bash
cd authapp-code
docker compose up -d --build
docker compose ps
```

Explication:

- `--build` force la reconstruction de l'image,
- `ps` valide l'etat des conteneurs.

## Etape 5 - Verifier l'API dockerisee

Commandes:

```bash
curl http://localhost:8080/api/health
```

Puis verification du flow complet:

1. register,
2. login,
3. appel `/api/users/me` avec bearer token.

Exemple complet:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"docker-user","email":"docker.user@example.com","password":"StrongPass123"}'

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"docker.user@example.com","password":"StrongPass123"}'
```

Puis reutiliser `data.token` pour appeler:

```bash
curl http://localhost:8080/api/users/me -H "Authorization: Bearer <TOKEN>"
```

## Etape 6 - Verifier la vue frontend

La page est servie depuis `src/main/resources/static`.

Acces:

- `http://localhost:8080/`

Frontend impose respecte:

- HTML,
- CSS,
- JavaScript vanilla,
- Bootstrap CDN.

Attention securite:

- la valeur de secret JWT fournie dans ce TP est reservee a l'apprentissage local,
- en environnement reel, utiliser un secret fort et prive, injecte par vault/secret manager.

---

## 3.1 Quiz rapide (validation)

1. Pourquoi utiliser un Dockerfile multi-stage ?
2. Pourquoi ajouter un healthcheck sur la base de donnees ?
3. Pourquoi ne jamais commiter un secret de production dans Git ?

Corrige synthese:

- Pour separer build/runtime et reduire la taille/surface d'attaque.
- Pour eviter que l'API demarre avant que la base soit prete.
- Pour eviter fuite de credentials et compromission.

## Etape 7 - Arret propre

Commandes:

```bash
docker compose down
```

Optionnel reset complet des donnees locales:

```bash
docker compose down -v
```

---

## 4) TD du chapitre 5

### TD-05 - Checklist de livraison

Travail demande:

1. Definir la checklist pre-demo (containers, logs, endpoints).
2. Definir la checklist post-demo (arret, nettoyage, traces).
3. Proposer 3 ameliorations pour une pre-prod.

Livrable:

- document de readiness operationnelle.

---

## 5) TP du chapitre 5

### TP-05 - Demo finale dockerisee

Taches:

1. Lancer la stack `docker compose up -d --build`.
2. Verifier `/api/health`.
3. Executer register/login/me via API.
4. Verifier la page frontend `/`.
5. Arreter proprement les services.

Definition of Done:

- stack demarre correctement,
- endpoints critiques operationnels,
- frontend accessible,
- processus reproductible documente.

---

## 6) Validation finale projet

- [ ] `./mvnw test` passe
- [ ] `docker compose up -d --build` passe
- [ ] `GET /api/health` retourne `200`
- [ ] register/login/me fonctionnent en conteneurs
- [ ] frontend HTML/CSS/JS + Bootstrap disponible

---

## 7) Cloture

Le projet est maintenant:

- architecture multi-layer,
- securise JWT,
- teste automatiquement,
- dockerise de bout en bout,
- pret pour extensions (refresh token, reset password, roles avances).
