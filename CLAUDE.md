# CLAUDE.md

Study Tracker is a research management web app for tracking studies, assays, and programs. Spring Boot 3.4 (Java 17) backend + React 18 (Vite) frontend, packaged as a single WAR (`web/target/study-tracker.war`).

**Requirements:** JDK 17+, PostgreSQL 12+, OpenSearch 2+ (optional). Node 22 / npm 10 are downloaded automatically by Maven; install them locally only for frontend dev.

## Commands

```bash
# Full build (client module runs npm install + npm run build, then web packages the WAR)
./mvnw clean package -DskipTests

# Backend-only build — skips the client module, but the WAR still bundles
# whatever is currently in client/build/ (stale or missing if never built)
./mvnw clean package -DskipTests -pl web

# Run the app (http://localhost:8080)
./mvnw spring-boot:run -pl web

# Tests (need PostgreSQL — see Testing)
./mvnw test -pl web -Dtest=StudyServiceTests             # single class
./mvnw test -pl web -Dtest="io.studytracker.test.service.**"  # one layer, as CI does

# Manual Flyway (the app also migrates automatically on startup)
./mvnw -Dflyway.configFiles=web/flyway.conf flyway:migrate   # needs web/flyway.conf (from flyway.conf.example)
```

Frontend (run from `client/`):

```bash
npm install
npm run dev     # Vite dev server on :3000 (no proxy to the backend is configured)
npm run build   # build:js (Vite) + build:less, both output to client/build/
npm run lint    # ESLint with --fix (modifies files)
npm test        # Vitest (jsdom); there are currently no frontend tests
```

## Layout

- `client/` — React frontend, its own Maven module (`frontend-maven-plugin`). Build output lands in `client/build/` and is copied into `web/target/classes` at `prepare-package`. Never put built assets in `web/src/main/resources/static`.
- `web/` — Spring Boot app; all backend code is under `web/src/main/java/io/studytracker/`.
- `dev/` — personal scripts and **database backups (`*.sql`) from real environments**. Don't read, modify, or reference them unless asked.
- `docs/` — setup guides (Okta/Entra SAML SSO) and migration notes.

### Backend (`io.studytracker`)

Layered: `model/` (JPA entities) → `repository/` (Spring Data JPA) → `service/` → `controller/`.

- **Controllers:** shared logic lives in abstract base classes in `controller/api/` (`AbstractStudyController`, etc.). Endpoints are in subclasses: `controller/api/internal/` (`*PrivateController`, `/api/internal/**`, used only by the React app) and `controller/api/v1/` (`*PublicController`, `/api/v1/**`, public API with token auth, documented via SpringDoc at `/swagger-ui.html`). When adding behavior shared by both APIs, put it in the abstract base.
- **DTOs:** never expose entities from controllers. Use MapStruct mappers and DTOs in `mapstruct/` (`dto/api/` for the public API, `dto/response/` and `dto/form/` for the internal API).
- **Lombok + MapStruct** both run as annotation processors (configured in `web/pom.xml`). Regenerate with a Maven compile after changing mapper interfaces.
- **Pluggable integrations** sit behind interfaces, and the implementation is chosen by a property: storage (`storage/`: local, Egnyte, S3, SharePoint/OneDrive), ELN (`eln/` + `benchling/`), search (`search/` + OpenSearch), git (`git/` + `gitlab/`), events (`events/`: local or AWS EventBridge), SSO (`security/`: local DB, Okta, Entra ID, SAML). Code against the abstraction, not a concrete client.
- `example/` holds `ExampleDataRunner`, which seeds the database for tests and development.

### Frontend (`client/src/`)

`pages/` (route-level components), `common/` (shared forms, modals, tables), `redux/` (Redux Toolkit), `config/` (Axios client, routes), `hooks/`, `context/`. Uses React Router 6, Formik + Yup, React Bootstrap 5, TanStack Query/Table. Styles are LESS in `src/less/`, compiled separately from the JS build. Import alias: `@` → `src/`.

## Configuration

- `web/src/main/resources/defaults.properties` holds base defaults. `application.properties` (local, gitignored; template at repo root `application.properties.example`) overrides them. `application-{profile}.properties` files hold per-environment settings.
- `application.secret` is required (JWT signing, 32+ chars).
- `spring.jpa.hibernate.ddl-auto=validate`: the schema comes only from Flyway, so every entity change needs a migration.

### Database migrations

Scripts live in `web/src/main/resources/db/migration/` as `V<major.minor>_<NNN>__<description>.sql` (e.g. `V0.9_010__notebook_folders.sql`). Add a new file after the latest one. Never edit an existing migration.

## Testing

- Backend tests are `@SpringBootTest` integration tests using **JUnit 4** (`@RunWith(SpringRunner.class)`, `org.junit.Test`, run through junit-vintage). Match this style; don't mix in JUnit 5.
- Most tests use `@ActiveProfiles({"test", "example"})` and call `exampleDataRunner.populateDatabase()` in `@Before` to reset to known data.
- They need a running PostgreSQL database `study-tracker-test`. Connection details go in `web/src/test/resources/application-test.properties`; all `*.properties` files in that directory are gitignored. `web/flyway.test.conf` points Flyway at the test DB.
- Tests are organized by layer under `web/src/test/java/io/studytracker/test/` (`repository/`, `service/`, `web/`, plus integration packages such as `benchling/`, `egnyte/`, `aws/`, `msgraph/`). Integration-package tests call real external services and need their own profile credentials. Many are `@Ignore`d. Don't run them unless asked.
- CI (`.github/workflows/build-and-test.yml`) runs the repository, service, and web test packages against a Postgres service container.

## Conventions

- Follow the Google Java and JavaScript style guides (see `CONTRIBUTING.md`). Branch names: `<issue-id>-short-summary`.
- Don't commit secrets. Profile property files under `web/src/main/resources/` may contain real credentials, so don't copy values out of them.
