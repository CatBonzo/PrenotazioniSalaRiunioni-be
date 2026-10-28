# Prenotazioni Sala Riunioni - backend

Backend REST per prenotare sale riunioni e postazioni. Frontend: [PrenotazioniSalaRiunioni-fe](https://github.com/CatBonzo/PrenotazioniSalaRiunioni-fe).

## Avvio

1. Database: PostgreSQL con un database `prenotazioni` (in `dev` utente e password di default sono `smartcity`). Un dump è in `backup/PrenotazioniDB.backup` (formato tar, si ripristina con `pg_restore`).
2. Qui: `SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun` (porta 8080).
3. Swagger UI su http://localhost:8080/swagger-ui.html, il contratto OpenAPI su `/api-docs`. Per provare le API protette: `POST /auth/login`, poi il token in "Authorize".

Serve un JDK 17 o più recente; Gradle arriva col wrapper.

## Comandi

| Comando | Cosa fa |
|---|---|
| `./gradlew bootRun` | avvia il backend su :8080 |
| `./gradlew test` | test (oggi solo il caricamento del contesto, serve il database) |
| `./gradlew bootJar` | jar eseguibile in `build/libs/prenotazioni-sala-riunioni-be-0.0.1-SNAPSHOT.jar` |
| `docker build -t prenotazioni-be .` | immagine con Java 17 che espone la porta 8080 |

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

In `dev` Hibernate aggiorna lo schema da solo (`ddl-auto=update`), in `prod` lo controlla soltanto (`validate`).

## API

| Prefisso | Cosa | Chi |
|---|---|---|
| `/auth` | login, registrazione, password dimenticata e reset | tutti |
| `/api/utenti` | utente corrente, modifica del profilo, cancellazione del proprio account | utenti entrati |
| `/api/prenotazioni` | prenotazioni, orari disponibili, prenotazioni del giorno | utenti entrati |
| `/api/prenotazioni/admin`, `/api/prenotazioni/export/giorno/{data}` | prenotazioni per conto di altri, export Excel | `ROLE_ADMIN` |
| `/api/stanze`, `/api/postazioni` | stanze e postazioni | utenti entrati |
| `/api/cose-durata` | "cose durata": voci con un nome collegate alle prenotazioni | utenti entrati |
| `/api/stats` | statistiche delle prenotazioni e delle stanze | utenti entrati |
| `/api/admin` | gestione utenti | `ROLE_ADMIN` |

## Struttura

Pacchetto `com.prenotazioni.exprivia.exprv`:

- `controller`: un controller per risorsa, `StatsController/` per le statistiche.
- `service`: la logica; `EmailService` e `PasswordResetService` per il recupero password.
- `entity`, `repository`: JPA (utenti, ruoli, stanze, postazioni, prenotazioni, cose durata).
- `dto`, `mapper`: oggetti esposti dalle API e mapper MapStruct.
- `security`: Spring Security con JWT (`security/jwt`), regole di accesso in `SecurityConfig`.
- `config`: CORS (`WebConfig`), gestione degli errori (`RestExceptionHandler`), password, async.

Decisioni e coordinamento col frontend: `AGENTS.md`.
