# TTrack Backend

An exercise time-tracking application backend built with Micronaut framework,
featuring JWT authentication via HttpOnly cookies, PostgreSQL database, and comprehensive code quality checks.

## Technologies Used

### Core Framework & Runtime
- **Micronaut 4.10.4** - Modern, JVM-based framework for building microservices
- **Java 21** - Programming language and runtime
- **Gradle 8.x** - Build automation tool
- **Netty** - Reactive HTTP server

### Security & Authentication
- **Micronaut Security JWT** - JWT-based authentication and authorization via cookies
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

**JWT Security (cookie-based):**
```yaml
micronaut:
  security:
    authentication: idtoken
    token:
      bearer:
        enabled: true
      jwt:
        generator:
          access-token:
            expiration: ${JWT_ACCESS_TOKEN_EXPIRATION:3600}   # Default: 1 hour
          refresh-token:
            enabled: true
            secret: ${JWT_REFRESH_SECRET:${JWT_SECRET:"PlsChangeThis!!..."}}
            expiration: ${JWT_REFRESH_TOKEN_EXPIRATION:86400} # Default: 24 hours
        signatures:
          secret:
            generator:
              secret: ${JWT_SECRET:"PlsChangeThis!!PlsChangeThis!!PlsChangeThis!!PlsChangeThis!!"}
      cookie:
        enabled: true
        cookie-name: ${JWT_COOKIE_NAME:access_token}
        cookie-http-only: true
        cookie-secure: ${JWT_COOKIE_SECURE:false}
        cookie-same-site: ${JWT_COOKIE_SAME_SITE:Lax}
        cookie-max-age: ${JWT_COOKIE_MAX_AGE:3600s}
```

All JWT values are driven by environment variables with safe local-dev defaults.
**Change all secrets in production environments.**

#### Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | `PlsChangeThis!!...` | Secret used to sign access tokens (HS256). Must be ≥ 256 bits (32+ chars). |
| `JWT_REFRESH_SECRET` | falls back to `JWT_SECRET` | Dedicated secret for signing refresh tokens. Set this separately for stronger isolation. |
| `JWT_ACCESS_TOKEN_EXPIRATION` | `3600` | Access token lifetime in **seconds** (default: 1 hour). |
| `JWT_REFRESH_TOKEN_EXPIRATION` | `86400` | Refresh token lifetime in **seconds** (default: 24 hours). |
| `JWT_COOKIE_NAME` | `access_token` | Name of the HttpOnly cookie carrying the access token. |
| `JWT_COOKIE_SECURE` | `false` | Set to `true` in production (requires HTTPS). |
| `JWT_COOKIE_SAME_SITE` | `Lax` | SameSite policy (`Lax`, `Strict`, or `None`). |
| `JWT_COOKIE_MAX_AGE` | `3600s` | Cookie max-age (should match `JWT_ACCESS_TOKEN_EXPIRATION`). |

**Public Endpoints:**
- `/swagger/**` - Swagger API documentation
- `/swagger-ui/**` - Swagger UI interface
- `/auth/login` - User authentication (cookie-based, for web clients)
- `/auth/refresh` - Exchange a refresh token for a new access token (cookie flow)
- `/auth/mobile-login` - Mobile user authentication (returns access + refresh tokens in JSON body)
- `/auth/mobile-refresh` - Mobile token refresh (validates refresh token, returns new token pair)
- `/user/create` - User registration

All other endpoints require JWT authentication via the auth cookie.

### Mobile Authentication & Token Refresh

For **mobile clients** that cannot use HttpOnly cookies, the API exposes a JSON-body token flow:

#### `POST /auth/mobile-login`

Authenticates a user and returns **both** an access token and a refresh token in the response body.

**Request:**
```json
{
  "email": "tony.stark@gmail.com",
  "password": "12345"
}
```

**Response `200 OK`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

The client should store both tokens securely (e.g. in encrypted storage). Include the `accessToken` in subsequent requests as `Authorization: Bearer <accessToken>`.

#### `POST /auth/mobile-refresh`

Validates the given refresh token and issues a **new access token** together with a **rotated refresh token** (one-time use). The old refresh token is invalidated on next validation.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response `200 OK`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Returns `401 Unauthorized` if the refresh token is invalid, malformed, or expired.

**Recommended mobile flow:**
1. Call `/auth/mobile-login` → store `accessToken` and `refreshToken`.
2. Use `accessToken` for all API calls (`Authorization: Bearer <token>`).
3. When the API returns `401`, call `/auth/mobile-refresh` with the stored `refreshToken`.
4. Replace both stored tokens with the new pair from the response.
5. Retry the original request with the new `accessToken`.



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

### Versioning

The `bumpVersion` task updates the version in both `version.properties` and the OpenAPI `@OpenAPIDefinition` annotation in `Application.java`.

```bash
./gradlew bumpVersion                       # Patch bump: 0.3.1 -> 0.3.2
./gradlew bumpVersion -Pcomponent=minor     # Minor bump: 0.3.1 -> 0.4.0
./gradlew bumpVersion -Pcomponent=major     # Major bump: 0.3.1 -> 1.0.0
./gradlew bumpVersion -PnewVersion=1.2.3   # Set an explicit version
```

> **Note (PowerShell):** When passing `-PnewVersion=x.y.z`, wrap the argument in single quotes to avoid
> PowerShell splitting on the dots: `./gradlew bumpVersion '-PnewVersion=1.2.3'`

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

## CI/CD Pipeline

This project includes a comprehensive **GitHub Actions CI/CD pipeline** for automated testing, code quality checks, and Docker image building.

### Pipeline Stages

1. **Validate**: Checkstyle code quality checks
2. **Test**: Unit, integration, and E2E tests (run in parallel)
3. **Build**: Docker image creation
4. **Publish**: Push to GitHub Container Registry (automatic on push to main/tags)

### Quick Start with CI/CD

1. **Push to GitHub**: The pipeline runs automatically on push/PR
2. **View Pipeline**: Check progress in GitHub Actions → CI/CD Pipeline
3. **View Results**: Test results and artifacts are published automatically
4. **Pull Image**: 
   ```bash
   docker pull ghcr.io/your-username/ttrack-be:latest
   ```

### Deploy with Docker

Use the provided `docker-compose.ci.yml` for production deployment:

```bash
# Create .env file from example
cp .env.example .env

# Edit .env with your configuration
nano .env

# Deploy the application
docker-compose -f docker-compose.ci.yml up -d
```


## Docker Image

### Available Tags
- `latest` - Latest build from main/master branch
- `{branch-name}` - Latest from a specific branch
- `{branch-name}-{sha}` - Specific commit from a branch
- `v{version}` - Release version (e.g., v1.0.0)
- `{major}.{minor}` - Semantic version tags (e.g., 1.0)
- `{major}` - Major version tag (e.g., 1)

### Image Features
- **Multi-stage build** for optimized size (~200-250 MB)
- **Non-root user** for enhanced security
- **Health checks** included
- **JVM optimized** for containers

## Release Workflow

This section describes how to build, deploy, and release a new version of the TTrack Backend app. The process is **fully automated** via GitHub Actions with **SSH-based production deployment**.

### How the Release Pipeline Works

The **Release Pipeline** (`.github/workflows/release.yml`) is triggered when you push a Git tag matching the pattern `v*` (e.g., `v0.0.13`). It automatically:

1. ✅ **Runs all tests** (unit, integration, E2E) - Deployment halts if tests fail
2. 🏗️ **Builds Docker image** - Multi-stage build, optimized for production
3. 📦 **Pushes to GitHub Container Registry (GHCR)** - Image tagged with version
4. 📝 **Creates GitHub Release** - Auto-generated changelog from commit history
5. 🚀 **Deploys via SSH** - Automatically deploys to your production server using `docker-compose.ci.yml`
6. ✨ **Verifies health** - Confirms containers are running and application is healthy

**Key Features:**
- No manual deployment steps required
- Complete audit trail in GitHub Actions logs
- Automatic rollback by deploying previous versions
- Version validation (tag must match `version.properties`)

### Production Deployment via SSH

The pipeline establishes a **secure SSH connection** to your production server and executes:
```bash
cd /opt/ttrack-be
git checkout v0.0.13
export DOCKER_IMAGE=ghcr.io/your-org/ttrack-be:0.0.13
export JWT_SECRET=<your-access-token-secret>
export JWT_REFRESH_SECRET=<your-refresh-token-secret>
docker-compose -f docker-compose.ci.yml pull
docker-compose -f docker-compose.ci.yml down
docker-compose -f docker-compose.ci.yml up -d
```

Then verifies:
- Containers are running: `docker-compose ps`
- Application is healthy: `curl http://localhost:8080/health`

### Required GitHub Secrets (6 Total)

Before deploying, configure these secrets in **GitHub → Settings → Secrets and variables → Actions**:

> **Note:** `GITHUB_TOKEN` is automatically provided by GitHub Actions — you do **not** need to create it.

#### SSH / Deployment Secrets

| Secret Name | Example Value | Maps to (on server) | Purpose |
|-------------|---------------|---------------------|---------|
| `DEPLOY_SSH_PRIVATE_KEY` | `-----BEGIN OPENSSH PRIVATE KEY-----...` | — | Private SSH key used by the pipeline to connect to the server (Ed25519 or RSA-4096, **no passphrase**) |
| `DEPLOY_SERVER_HOST` | `prod.example.com` | — | Hostname or IP address of the production server |
| `DEPLOY_SSH_USER` | `deploy` | — | Dedicated deployment user on the server (avoid root) |
| `DEPLOY_APP_DIR` | `/opt/ttrack-be` | — | Absolute path on the server where the repo is checked out |

#### Application Secrets

| Secret Name | Example Value | Maps to (docker-compose env var) | Purpose |
|-------------|---------------|----------------------------------|---------|
| `DEPLOY_JWT_SECRET` | `y9KzN4pQ2wX8vL1mR5tJ...` (64+ chars) | `JWT_SECRET` → `micronaut.security.token.jwt.signatures.secret.generator.secret` | Signing key for **access tokens** (HS256). Must be ≥ 32 characters. |
| `DEPLOY_JWT_REFRESH_SECRET` | `aB3cD4eF5gH6iJ7kL8mN...` (64+ chars) | `JWT_REFRESH_SECRET` → `micronaut.security.token.jwt.generator.refresh-token.secret` | Signing key for **refresh tokens**. Use a **different** value from `DEPLOY_JWT_SECRET` for stronger security isolation. |

#### How secrets flow into the application

```
GitHub Secret              →  SSH export variable  →  docker-compose env var  →  Micronaut config key
─────────────────────────────────────────────────────────────────────────────────────────────────────
DEPLOY_JWT_SECRET          →  JWT_SECRET           →  JWT_SECRET              →  micronaut.security.token.jwt.signatures.secret.generator.secret
DEPLOY_JWT_REFRESH_SECRET  →  JWT_REFRESH_SECRET   →  JWT_REFRESH_SECRET      →  micronaut.security.token.jwt.generator.refresh-token.secret
```

> **Optional docker-compose variables** — these have safe defaults and can be left unset for most deployments. Configure them via the server `.env` file or as additional GitHub secrets if needed:
>
> | docker-compose env var | Default | Description |
> |------------------------|---------|-------------|
> | `POSTGRES_DB` | `ttrack-db` | PostgreSQL database name |
> | `POSTGRES_USER` | `ttrack-user` | PostgreSQL username |
> | `POSTGRES_PASSWORD` | `ttrack-password` | PostgreSQL password |
> | `POSTGRES_PORT` | `5432` | Host port mapped to PostgreSQL |
> | `APP_PORT` | `8080` | Host port mapped to the application |
> | `JWT_ACCESS_TOKEN_EXPIRATION` | `3600` | Access token lifetime in seconds |
> | `JWT_REFRESH_TOKEN_EXPIRATION` | `86400` | Refresh token lifetime in seconds |

### Generating Required Secrets

**Generate SSH Key** (recommended: Ed25519):
```bash
ssh-keygen -t ed25519 -f ~/.ssh/ttrack_deploy -C "github-deploy" -N ""
```

**Add public key to server's authorized_keys:**
```bash
cat ~/.ssh/ttrack_deploy.pub | ssh deploy@prod.example.com "cat >> ~/.ssh/authorized_keys"
```

**Generate JWT Secrets** (one for access tokens, one for refresh tokens):
```bash
openssl rand -base64 48   # JWT_SECRET (access token)
openssl rand -base64 48   # JWT_REFRESH_SECRET (refresh token)
```

### Step-by-Step Release Instructions

#### 1. Update Version
Update the version in `version.properties`:
```properties
version=0.0.13
```

Commit the change:
```bash
git add version.properties
git commit -m "chore: bump version to 0.0.13"
git push origin main
```

#### 2. Create and Push Tag
Create a Git tag matching the version:
```bash
git tag -a v0.0.13 -m "Release v0.0.13: Description of changes"
git push origin v0.0.13
```

#### 3. Pipeline Executes Automatically
- GitHub Actions triggers automatically
- Monitor progress in **GitHub → Actions → Release Pipeline**
- Pipeline stages:
  - **Tests** - Runs all test suites (~5-10 min)
  - **Build & Publish** - Builds Docker image and pushes to GHCR (~5-10 min)
  - **Create Release** - Generates GitHub Release with changelog (~1 min)
  - **Deploy** - SSH to server, pulls code/image, restarts containers (~2-3 min)

#### 4. Verify Deployment
After pipeline completes:
```bash
# Check application health
curl https://prod.example.com:8080/health

# Or SSH to server
ssh -i ~/.ssh/ttrack_deploy deploy@prod.example.com
docker-compose -f /opt/ttrack-be/docker-compose.ci.yml ps
docker-compose -f /opt/ttrack-be/docker-compose.ci.yml logs -f app
```

### Rollback to Previous Version

If you need to revert to a previous version:
```bash
# Deploy previous tag
git push origin v0.0.12

# Or manually on server
cd /opt/ttrack-be
git checkout v0.0.12
docker-compose -f docker-compose.ci.yml restart app
```

### Troubleshooting

**SSH Connection Fails:**
- Check public key in server's `~/.ssh/authorized_keys`
- Verify permissions: `chmod 600 ~/.ssh/authorized_keys && chmod 700 ~/.ssh`
- Test manually: `ssh -i ~/.ssh/ttrack_deploy deploy@prod.example.com "echo OK"`

**Deployment Failed:**
- Check GitHub Actions logs for error details
- Verify all 6 secrets are configured correctly
- Ensure server has Docker and Docker Compose installed

**Health Check Failed:**
- Check container logs: `docker-compose logs app`
- Verify application is listening on port 8080
- Check database connectivity

**Version Mismatch:**
- Ensure git tag matches `version.properties` (e.g., tag `v0.0.13` with `version=0.0.13`)

---

## CI/CD Pipeline Summary

### CI Pipeline (on every push/PR)
- **Triggers:** Push to main, develop, feature/*, bugfix/*, hotfix/*, or pull requests
- **Stages:**
  1. Validate (Checkstyle code quality)
  2. Test (unit, integration, e2e in parallel)
  3. Build Docker image
- **For details, see `.github/workflows/ci.yml`**

### Release Pipeline (on tag push v*)
- **Triggers:** When you push a tag matching `v*` (e.g., `git push origin v0.0.13`)
- **Stages:**
  1. ✅ Run Tests - All test suites must pass
  2. 🏗️ Build & Publish - Docker image built and pushed to GHCR
  3. 📝 Create Release - GitHub Release created with changelog
  4. 🚀 Deploy to Production - **SSH to server, pull code/image, restart with docker-compose**
  5. ✨ Verify Health - Confirm containers running and /health endpoint responding
- **Requirements:** 6 GitHub secrets must be configured (see Release Workflow section)
- **For details, see `.github/workflows/release.yml`**

### Artifacts & Outputs
- Test reports (unit, integration, e2e)
- Checkstyle reports
- Docker image (ghcr.io/your-org/ttrack-be:version)
- GitHub Release with changelog
- Deployment logs in GitHub Actions
---

> **Developed with ❤️ by vladimirvaca 👽**
