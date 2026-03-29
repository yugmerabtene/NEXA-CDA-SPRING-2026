# Annexe - References et glossaire

## References officielles recommandees

### Spring Boot
- https://docs.spring.io/spring-boot/reference/
- https://spring.io/projects/spring-boot
- https://spring.io/guides

### Spring Security
- https://docs.spring.io/spring-security/reference/
- https://spring.io/projects/spring-security

### JWT / RFC
- https://www.rfc-editor.org/rfc/rfc7519 (JWT)
- https://www.rfc-editor.org/rfc/rfc8725 (JWT Best Current Practices)
- https://www.rfc-editor.org/rfc/rfc7515 (JWS)
- https://www.rfc-editor.org/rfc/rfc7517 (JWK)

### Securite API (OWASP)
- https://owasp.org/API-Security/
- https://owasp.org/API-Security/editions/2023/en/0x11-t10/

### Flyway
- https://documentation.red-gate.com/flyway
- https://documentation.red-gate.com/flyway/flyway-concepts/migrations

### Docker / Compose
- https://docs.docker.com/compose/
- https://docs.docker.com/reference/compose-file/

### PostgreSQL
- https://www.postgresql.org/docs/current/ddl-constraints.html
- https://www.postgresql.org/docs/current/sql-createtable.html

### Strategie de tests
- https://docs.spring.io/spring-boot/reference/testing/
- https://junit.org/junit5/docs/current/user-guide/
- https://java.testcontainers.org/
- https://martinfowler.com/articles/practical-test-pyramid.html

---

## Glossaire rapide

- `Controller`: point d'entree HTTP/REST.
- `Service`: logique metier.
- `Repository`: acces base de donnees.
- `Model`: objet metier persiste (entite JPA).
- `View`: contrat d'entree/sortie API (request/response JSON).
- `Mapper`: conversion entre view et model.
- `JWT`: token signe pour authentifier des appels API stateless.
- `401`: non authentifie.
- `403`: authentifie mais non autorise.
- `Flyway`: outil de migration versionnee de schema SQL.
- `Docker Compose`: orchestration locale multi-conteneurs.
