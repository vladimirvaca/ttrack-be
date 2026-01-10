# TTrack Backend

An exercise time-tracking application backend built with Micronaut framework,
featuring JWT authentication, PostgreSQL database, and comprehensive code quality checks.

## Technologies Used

### Core Framework & Runtime
- **Micronaut 4.10.4** - Modern, JVM-based framework for building microservices
- **Java 21** - Programming language and runtime
- **Gradle 8.x** - Build automation tool
- **Netty** - Reactive HTTP server

### Security & Authentication
- **Micronaut Security JWT** - JWT-based authentication and authorization
- **jjwt 0.12.5** - JSON Web Token implementation
- **BCrypt (jbcrypt 0.4)** - Password hashing library

### Data & Persistence
- **PostgreSQL** - Relational database
- **Hibernate JPA** - ORM framework
- **Micronaut Data Hibernate** - Data access layer
- **HikariCP** - JDBC connection pool
- **Flyway** - Database migration tool

### API Documentation
- **OpenAPI/Swagger** - API documentation and specification
- **Swagger UI** - Interactive API documentation interface

### Code Quality
- **Checkstyle 10.12.7** - Static code analysis tool
- **Lombok** - Boilerplate code reduction

### Serialization & Validation
- **Jackson** - JSON serialization/deserialization
- **Micronaut Validation** - Bean validation

## Prerequisites

- Java 21 or higher
- Docker (for PostgreSQL database)
- Gradle (wrapper included)

## Configuration and Local Setup

### 1. Database Setup

Start the PostgreSQL database using Docker Compose:

```bash
docker-compose up -d
```

This will create a PostgreSQL container with the following configuration:
- **Database**: ttrack-db
- **User**: ttrack-user
- **Password**: ttrack-password
- **Port**: 5432
- **Volume**: Data persisted in `../ttracker-db`

### 2. Application Configuration

The application configuration is located in `src/main/resources/application.yml`. Key configurations:

**Database Connection:**
```yaml
datasources:
  default:
    url: jdbc:postgresql://localhost:5432/ttrack-db
    username: ttrack-user
    password: ttrack-password
```

**JWT Security:**
```yaml
micronaut:
  security:
    token:
      jwt:
        generator:
          access-token:
            expiration: 3600  # 1 hour
        signatures:
          secret:
            generator:
              secret: "PlsChangeThis!!PlsChangeThis!!PlsChangeThis!!PlsChangeThis!!"
```

⚠️ **Important**: Change the JWT secret in production environments!

**Public Endpoints:**
- `/swagger/**` - Swagger API documentation
- `/swagger-ui/**` - Swagger UI interface
- `/auth/login` - User authentication
- `/user/create` - User registration

All other endpoints require JWT authentication.

### 3. Build the Project

```bash
./gradlew build
```

This will:
- Compile the Java source code
- Run tests
- Execute Checkstyle validation
- Install the pre-commit git hook

### 4. Run the Application

```bash
./gradlew run
```

The application will start on the default port (usually 8080). Access Swagger UI at:
```
http://localhost:8080/swagger-ui/
```

## Checkstyle - Code Quality

The project uses Checkstyle to enforce code quality standards and maintain consistent code style.

### Configuration

- **Version**: 10.12.7
- **Config File**: `src/main/resources/checkstyle/checkstyle.xml`
- **Failure Policy**: Build fails on violations (`ignoreFailures = false`)

### Key Style Rules

- **Line Length**: Maximum 120 characters
- **Indentation**: 4 spaces (no tabs)
- **Naming Conventions**:
  - Classes: PascalCase
  - Methods/Variables: camelCase
  - Constants: UPPER_SNAKE_CASE
- **Imports**: No wildcards, ordered by groups
- **Method Length**: Maximum 150 lines
- **Parameters**: Maximum 7 per method
- **Code Quality**: No magic numbers, no clone/finalizer, proper equals/hashCode

### Running Checkstyle

Run Checkstyle manually:
```bash
./gradlew checkstyleMain
```

Run all checks (including tests):
```bash
./gradlew check
```

View Checkstyle reports:
```
build/reports/checkstyle/main.html
```

## Git Hooks

The project includes a pre-commit hook that automatically validates code quality before commits.

### Pre-Commit Hook

**Location**: `pre-commit` (root directory)

**Installation**: The hook is automatically installed when you run:
```bash
./gradlew build
```
or
```bash
./gradlew compileJava
```

The Gradle task `installPreCommitGitHook` copies the pre-commit script to `.git/hooks/pre-commit` and makes it executable.

**Hook Behavior**:
1. Runs `./gradlew check` before each commit
2. Executes all tests
3. Runs Checkstyle validation
4. If any check fails, the commit is blocked
5. Returns exit code 1 on failure, 0 on success

**Manual Installation**:
If needed, manually install the hook:
```bash
./gradlew installPreCommitGitHook
```

**Bypass Hook** (not recommended):
```bash
git commit --no-verify -m "commit message"
```

## Available Gradle Tasks

### Build & Run
```bash
./gradlew build          # Build the project
./gradlew run            # Run the application
./gradlew clean          # Clean build artifacts
```

### Testing
```bash
./gradlew test            # Run unit tests only (fast, no Testcontainers)
./gradlew integrationTest # Run integration tests (database layer)
./gradlew e2eTest         # Run end-to-end tests (full application context)
```

### Code Quality
```bash
./gradlew check          # Run checks in whole project included only unit tests
./gradlew checkstyleMain # Run checkstyle on main source
./gradlew checkstyleTest # Run checkstyle on test source
```

### Docker
```bash
./gradlew dockerBuild         # Build Docker image
./gradlew dockerfileNative    # Generate native Docker image
```

### Database
Flyway migrations run automatically on application startup. Migration scripts are located in:
```
src/main/resources/db/migration/
```

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui/
- **OpenAPI Spec**: http://localhost:8080/swagger/

## Project Structure

```
ttrack-be/
├── src/main/java/com/rvladimir/
│   ├── Application.java                    # Main application entry point
│   ├── constants/                          # Application constants
│   ├── domain/                             # JPA entities
│   │   └── User.java
│   ├── repository/                         # Data repositories
│   │   └── UserRepository.java
│   ├── service/                            # Business logic interfaces
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── dto/                            # Data Transfer Objects
│   │   ├── impl/                           # Service implementations
│   │   └── mapper/                         # Entity-DTO mappers
│   └── web/
│       ├── error/                          # Exception handlers
│       └── rest/                           # REST controllers
│           ├── AuthResource.java
│           └── UserResource.java
├── src/main/resources/
│   ├── application.yml                     # Application configuration
│   ├── checkstyle/checkstyle.xml          # Checkstyle rules
│   └── db/migration/                       # Flyway migration scripts
├── build.gradle                            # Gradle build configuration
├── docker-compose.yml                      # PostgreSQL container setup
└── pre-commit                              # Git pre-commit hook script
```

## Development Workflow

1. **Start Database**: `docker-compose up -d`
2. **Make Changes**: Edit code following Checkstyle rules
3. **Run Locally**: `./gradlew run`
4. **Run Tests**: `./gradlew test`
5. **Validate Code**: `./gradlew check`
6. **Commit**: Git pre-commit hook runs automatically
7. **View API**: Access Swagger UI for API testing

## Additional Resources

- [Micronaut Documentation](https://docs.micronaut.io/4.10.4/guide/index.html)
- [Micronaut Security JWT](https://micronaut-projects.github.io/micronaut-security/latest/guide/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Checkstyle Rules](https://checkstyle.org/checks.html)

> **Developed with ❤️ by vladimirvaca 👽**
