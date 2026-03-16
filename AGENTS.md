# AGENTS.md — TTrack Backend

Exercise time-tracking REST API built with **Micronaut 4.10.4 + Java 21 + PostgreSQL**.

---

## Architecture

Strict four-layer stack — never skip layers or inject lower-layer beans upward:

```
web/rest/          →  @Controller, HttpResponse, @Body/@PathVariable
service/           →  interface (public API contract)
service/impl/      →  @Singleton @Transactional implementation
repository/        →  interface extends JpaRepository<Entity, Long>
domain/            →  @Entity JPA classes
```

**DTOs** live in `service/dto/` (Create*, *DTO). **Hand-written mappers** live in `service/mapper/` (`@Singleton`, no MapStruct). Controllers never touch entities directly; services never return entities.

### Domain Model

```
User  1──*  TrainingSession  1──*  SessionExercise  *──1  Exercise
                                                           (catalog)
TypeOfExercise (enum) classifies both Exercise and SessionExercise
```

All JPA entities use `schema = TtrackConstants.TTRACK_SCHEMA` (`"ttrack"`). All metric fields on `SessionExercise` are nullable to support every exercise modality.

---

## Key Patterns

### Entities
Every entity gets exactly these Lombok + JPA annotations — no exceptions:
```java
@Serdeable @Entity @Data @NoArgsConstructor @AllArgsConstructor
@Table(name = "...", schema = TtrackConstants.TTRACK_SCHEMA)
```
IDs use `@GeneratedValue(strategy = GenerationType.IDENTITY)`.

### Service Implementations
```java
@Singleton
@Transactional
public class FooServiceImpl implements FooService {
    private static final String USER_NOT_FOUND = "User not found";   // constants here, not in entity
    // constructor injection only — no @Autowired/@Inject on fields
}
```

### Error Handling
Throw `ValidationException(message, field, errorCode)` from any service layer. `ValidationExceptionHandler` catches it and returns a structured `400` with `ValidationErrorResponse`. Do not throw `HttpStatusException` from service impls except in `AuthServiceImpl` (auth-specific 401 flows).

### Controllers
Use `@RequiredArgsConstructor` (or `@AllArgsConstructor`) + `@Slf4j`. Log `info` on entry with IDs, `warn` on empty/failed results. Annotate every endpoint with `@Operation`, `@ApiResponse` (Swagger).

### Auth — Two Flows
| Flow | Endpoint | Token delivery |
|------|----------|----------------|
| Web (browser) | `/auth/login`, `/auth/refresh` | HttpOnly cookie `access_token` |
| Mobile | `/auth/mobile-login`, `/auth/mobile-refresh` | JSON body `{ accessToken, refreshToken }` |

`AuthServiceImpl` uses a `DUMMY_PASSWORD_HASH` constant to run BCrypt even when the user is not found (timing-attack prevention).

### Database Migrations
Flyway scripts in `src/main/resources/db/migration/`. Naming: `V{semver}__{description}.sql` (e.g. `V0.7.0__session_exercise_duration_notes_exercise_type.sql`). Always add a new migration file; never alter existing ones.

---

## Test Structure

Three separate Gradle source sets — each serves a different scope:

| Source set | Location | DB | Pattern |
|---|---|---|---|
| `test` | `src/test/` | H2 in-memory | `@MicronautTest` + `@MockBean` via Mockito |
| `integrationTest` | `src/integrationTest/` | Testcontainers PostgreSQL | Repository-level, real DB |
| `e2eTest` | `src/e2eTest/` | Testcontainers PostgreSQL | Full HTTP stack, real DB |

**Integration/E2E test boilerplate** (copy exactly):
```java
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FooTest implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();  // shared singleton

    @Override
    public Map<String, String> getProperties() {
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword(),
            "datasources.default.driverClassName", postgres.getDriverClassName()
        );
    }

    @AfterEach
    void cleanup() { repository.deleteAll(); }   // manual cleanup — no tx rollback
}
```

Use `TestDataFactory.createUser(email)` (in both `integrationTest` and `e2eTest` test utilities) to avoid duplicating User construction. Use `assertj` (`assertThat`) — not JUnit assertions.

Unit tests mock the service layer with `@MockBean`:
```java
@MockBean(FooService.class)
FooService fooService() { return mock(FooService.class); }
```

---

## Developer Workflows

```bash
# Start local DB
docker-compose up -d

# Run application
./gradlew run

# Tests (run in order)
./gradlew test                   # unit tests (H2, fast)
./gradlew integrationTest        # repository tests (Testcontainers)
./gradlew e2eTest                # full-stack HTTP tests (Testcontainers)

# Code quality (pre-commit hook runs this automatically)
./gradlew checkstyleMain
./gradlew check                  # compiles + tests + checkstyle

# Version management (updates version.properties AND Application.java annotation)
./gradlew bumpVersion                      # patch bump
./gradlew bumpVersion -Pcomponent=minor
./gradlew bumpVersion -PnewVersion=1.2.3
```

Pre-commit and pre-push hooks are installed automatically on `compileJava`. They block commits/pushes when `./gradlew check` fails.

---

## Checkstyle Rules (enforced, breaks CI)

- Max line length: **120 chars** (imports/packages exempt)
- Indentation: **4 spaces, no tabs**
- No wildcard imports; imports grouped by: `com.rvladimir`, blank, `io.micronaut`, blank, `jakarta`, blank, `java`, blank, third-party
- Constants: `UPPER_SNAKE_CASE` declared `private static final` inside the class that uses them
- No `clone()` / `finalize()` methods
- `equals()` must always be paired with `hashCode()`

---

## Key Files Reference

| Path | Purpose |
|------|---------|
| `src/main/java/com/rvladimir/constants/TtrackConstants.java` | Single shared constant: DB schema name |
| `src/main/java/com/rvladimir/web/error/` | `ValidationException` + `ValidationExceptionHandler` + `ValidationErrorResponse` |
| `src/main/resources/application.yml` | All configuration, env-var overrides for JWT/DB |
| `src/main/resources/db/migration/` | Flyway versioned SQL migrations |
| `src/main/resources/checkstyle/checkstyle.xml` | Full Checkstyle ruleset |
| `src/integrationTest/java/com/rvladimir/test/PostgresTestContainer.java` | Shared singleton Testcontainer |
| `src/integrationTest/java/com/rvladimir/test/TestDataFactory.java` | Test entity builder helpers |
| `version.properties` | Single source of truth for semver — edit via `bumpVersion` task only |

