# Prenotazioni Sala Riunioni - Backend

Backend REST per la prenotazione delle sale riunioni e delle postazioni.

## Tecnologie

- Java 17+
- Spring Boot 4.1 (Spring Security 7, Spring Data JPA con Hibernate 7, Jackson 3)
- PostgreSQL
- Gradle 9 (wrapper incluso)
- JWT (jjwt 0.13), MapStruct, Apache POI, springdoc-openapi 3

## Avvio

```bash
./gradlew bootRun
```

Build del jar eseguibile:

```bash
./gradlew bootJar
java -jar build/libs/prenotazioni-sala-riunioni-be-0.0.1-SNAPSHOT.jar
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Configurazione

Le credenziali non sono nel codice: si passano come variabili d'ambiente (oppure in `src/main/resources/application-secrets.properties`, ignorato da git).

| Variabile | Descrizione | Default |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` o `dev` | `prod` |
| `DB_URL` | URL JDBC del database | solo in `dev`: `jdbc:postgresql://localhost:5432/prenotazioni` |
| `DB_USERNAME` | Utente del database | solo in `dev`: `smartcity` |
| `DB_PASSWORD` | Password del database | solo in `dev`: `smartcity` |
| `JWT_SECRET` | Chiave per firmare i token (almeno 64 byte per HS512) | chiave di sviluppo |
| `MAIL_USERNAME` | Account Gmail per il recupero password | vuoto |
| `MAIL_PASSWORD` | Password per le app di Gmail | vuoto |

Un dump del database è in `backup/PrenotazioniDB.backup` (formato tar, ripristinabile con `pg_restore`).
