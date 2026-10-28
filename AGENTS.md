# AGENTS.md - backend Prenotazioni Sala Riunioni

Registro vivo delle decisioni. Si aggiorna a ogni passo, con il perché. Le decisioni superate si barrano.

## 1. Contesto

Backend REST per la prenotazione di sale riunioni e postazioni. Il frontend è in CatBonzo/PrenotazioniSalaRiunioni-fe e ha il suo AGENTS.md, con il registro degli attriti tra le due parti. Il codice viene da reddooneee/PrenotazionePostazioni-Exprivia (cartella `backend/exprv`, Spring Boot 3.4 con Maven) ed è stato portato a Spring Boot 4.1 e Gradle il 2026-10-28. Il repository di origine non si tocca: le modifiche si fanno solo qui.

## 2. Regole di lavoro

- Lingua della documentazione e dei messaggi: italiano.
- Un ramo e una pull request verso `main` per ogni passo; il merge lo fa solo Mahima.
- Versioni: sempre le ultime stabili.
- Nessuna credenziale nel codice: database, JWT e posta solo da variabili d'ambiente o da `application-secrets.properties` (ignorato da git).
- Le regole di accesso stanno nel backend (`SecurityConfig`): il frontend nasconde, il backend decide.
- Se cambia un endpoint, si controlla che il frontend lo chiami ancora nello stesso modo e si aggiorna il registro dei contratti nel suo AGENTS.md.

## 3. Decisioni

| Data | Decisione | Perché |
|---|---|---|
| 2026-10-28 | Backend importato da PrenotazionePostazioni-Exprivia e portato da Spring Boot 3.4.4 a 4.1.2 | Mahima ha chiesto di aggiornare il progetto all'ultima versione stabile |
| 2026-10-28 | Build con Gradle 9.8.2 (wrapper nel repository) al posto di Maven | Richiesta di Mahima. Il jar semplice è spento: serve solo `bootJar`, usato anche dal Dockerfile |
| 2026-10-28 | Java 17 come livello di compilazione (`options.release = 17`), compila con qualsiasi JDK 17 o più recente | È il minimo di Spring Boot 4 e quello del progetto di origine; l'immagine Docker usa Temurin 17 |
| 2026-10-28 | `spring-boot-starter-web` diventa `spring-boot-starter-webmvc`; Jackson 3 in `JwtAuthenticationEntryPoint`; JSpecify `@NonNull` nel filtro JWT | Cambi richiesti da Spring Boot 4 e Spring Framework 7 |
| 2026-10-28 | jjwt da 0.11.5 a 0.13.0, `JwtTokenProvider` riscritto con la nuova API; token firmati HS512, durata 24 ore (`app.jwt-expiration-milliseconds`) | La vecchia API di jjwt è stata tolta; HS512 chiede una chiave di almeno 64 byte |
| 2026-10-28 | springdoc 3.0.3 (Swagger UI su `/swagger-ui.html`, contratto su `/api-docs`), MapStruct 1.6.3, Apache POI 5.5.1 | Versioni compatibili con Spring Boot 4 |
| 2026-10-28 | Credenziali di database, mail e JWT spostate in variabili d'ambiente (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `MAIL_USERNAME`, `MAIL_PASSWORD`) | Il repository di origine le aveva in chiaro (anche una password per le app di Gmail): qui non entrano nel codice |
| 2026-10-28 | Due profili: `dev` (database locale, `ddl-auto=update`) e `prod` (database remoto da variabili, `ddl-auto=validate`); `prod` è il default | Scelta del progetto di origine: in produzione Hibernate non cambia lo schema |
| 2026-10-28 | Sessione senza stato con token Bearer; ruoli `ROLE_USER`, `ROLE_ADMIN`, `ROLE_BUILDMAN` nella tabella `authority` | Scelta del progetto di origine, tenuta durante l'aggiornamento |
| 2026-10-28 | Documentazione sul modello di BuonaCaccia-fe: README breve (avvio, comandi, configurazione, API, struttura) e questo registro | Mahima ha chiesto di prendere spunto dalla documentazione di Buonacaccia frontend |

## 4. Da sistemare

Problemi trovati leggendo il codice durante l'aggiornamento. Non sono stati corretti per non cambiare il comportamento insieme alle versioni.

| Data | Problema | Esito |
|---|---|---|
| 2026-10-28 | In `SecurityConfig` la riga `.anyRequest().authenticated()` è commentata: un endpoint non elencato è aperto a tutti | Aperto: riattivarla prima di andare online, come dice il commento nel codice |
| 2026-10-28 | `GET /api/prenotazioni/postazioni/9/orari-disponibili` è pubblico, solo per la postazione 9 (residuo di una prova) | Aperto: toglierlo |
| 2026-10-28 | Il CORS (`WebConfig`) ammette solo `http://localhost:4200` | Aperto: renderlo configurabile per la produzione |
| 2026-10-28 | Il frontend chiama endpoint che qui non esistono (`/api/postazioni/stanza/{id}`, `/piano/{piano}`, `/disponibili/{data}`, `DELETE /api/utenti/elimina/{id}`) | Aperto: vedi il registro dei contratti del frontend |
| 2026-10-28 | Il ruolo si chiama `ROLE_BUILDMAN` qui e `ROLE_BUILDM` nel frontend | Aperto: allineare i nomi |
| 2026-10-28 | L'unico test (`ExprvApplicationTests`) carica il contesto e ha bisogno di un PostgreSQL acceso | Aperto: aggiungere test dei servizi e un database di prova (per esempio Testcontainers) |
| 2026-10-28 | Nomi non uniformi negli endpoint (`creaStanza`, `aggiornastanza`, `crea_utente`, `/utente/{id}` e `/users`) e negli enum (`stato_prenotazione`, `tipo_stanza`) | Aperto: da rinominare insieme al frontend, se Mahima lo vuole |

## 5. Prossimi passi

Da decidere con Mahima: chiudere i punti aperti della sezione 4, partendo da quelli di sicurezza (`anyRequest`, endpoint di prova, CORS).
