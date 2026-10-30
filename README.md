# Prenotazioni Sala Riunioni - Backend

Backend REST per la prenotazione delle sale riunioni e delle postazioni.

## Tecnologie

- Java 17+
- Spring Boot 4.1 (Spring Security 7, Spring Data JPA con Hibernate 7, Jackson 3)
- PostgreSQL
- Gradle 9 (wrapper incluso)
- JWT (jjwt 0.13), MapStruct, Apache POI, springdoc-openapi 3

## Avvio

Serve Docker per il database. Il primo avvio crea il database `prenotazioni` e ci ripristina `backup/PrenotazioniDB.backup`:

```bash
docker compose up -d     # Postgres su localhost:5432 (utente e password: smartcity)
./gradlew bootRun        # profilo dev, si collega al Postgres di Docker
```

Per avviare anche il backend in un container:

```bash
docker compose --profile app up -d --build
```

Comandi utili sul database:

```bash
docker compose exec db psql -U smartcity -d prenotazioni   # console SQL
docker compose down          # ferma il container, i dati restano nel volume
docker compose down -v       # cancella anche i dati: al prossimo avvio riparte dal backup
```

Per collegarsi da un client (DBeaver, pgAdmin, IntelliJ): host `localhost`, porta `5432`, database `prenotazioni`, utente `smartcity`, password `smartcity`. Nome database, utente, password e porta si cambiano con le variabili `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` e `DB_PORT` (anche in un file `.env` accanto a `docker-compose.yml`).

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
| `SPRING_PROFILES_ACTIVE` | `dev` (Postgres di Docker, `ddl-auto=update`) o `prod` (`ddl-auto=validate`, `DB_*` obbligatorie) | `dev` |
| `DB_URL` | URL JDBC del database | solo in `dev`: `jdbc:postgresql://localhost:5432/prenotazioni` |
| `DB_USERNAME` | Utente del database | solo in `dev`: `smartcity` |
| `DB_PASSWORD` | Password del database | solo in `dev`: `smartcity` |
| `JWT_SECRET` | Chiave per firmare i token (almeno 64 byte per HS512) | chiave di sviluppo |
| `MAIL_USERNAME` | Account Gmail per il recupero password | vuoto |
| `MAIL_PASSWORD` | Password per le app di Gmail | vuoto |

Un dump del database è in `backup/PrenotazioniDB.backup` (formato tar): `docker compose` lo ripristina in automatico con lo script `docker/postgres/initdb/01-restore-backup.sh`.
