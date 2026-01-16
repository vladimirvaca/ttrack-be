# GitHub Copilot Instructions for TTrack Backend

Welcome to the TTrack Backend project! This document provides guidelines and best practices for using GitHub Copilot in this repository. Please follow these instructions to ensure code quality, maintainability, and consistency.

## General Guidelines

- **Write clear, concise, and maintainable code.**
- **Follow the project structure** as outlined in the [README.md](../README.md).
- **Use meaningful names** for variables, methods, and classes.
- **Document public methods and complex logic** with Javadoc comments.
- **Prefer composition over inheritance** where possible.
- **Avoid magic numbers**; use constants instead.
- **Keep methods short and focused** (preferably under 150 lines).
- **Limit method parameters** to a maximum of 7.
- **No wildcard imports**; organize imports by groups.

## Code Style & Checkstyle

- **Adhere to the Checkstyle rules** defined in `src/main/resources/checkstyle/checkstyle.xml`.
- **Key rules:**
  - Max line length: 120 characters
  - Indentation: 4 spaces (no tabs)
  - Class names: PascalCase
  - Method/variable names: camelCase
  - Constants: UPPER_SNAKE_CASE
  - No clone/finalizer methods
  - Proper equals/hashCode implementations
- **Run Checkstyle** before committing:
  - `./gradlew checkstyleMain` (main source)
  - `./gradlew check` (all checks)
- **Fix all Checkstyle violations** before submitting code.

## Testing

- **Write unit tests** for all new features and bug fixes.
- **Use integration and e2e tests** for database and full-stack scenarios.
- **Run tests locally** before pushing:
  - `./gradlew test` (unit tests)
  - `./gradlew integrationTest` (integration tests)
  - `./gradlew e2eTest` (end-to-end tests)

## Git Hooks & Commits

- **Pre-commit hook** runs `./gradlew check` and blocks commits on failure.
- **Do not bypass hooks** unless absolutely necessary.
- **Write descriptive commit messages**.

## Security

- **Never commit secrets or passwords.**
- **Change JWT secrets in production.**
- **Keep dependencies up to date** and check for vulnerabilities.

## Documentation

- **Update the [README.md](../README.md)** with any significant changes.
- **Document new endpoints and configuration options.**

## Helpful Commands

- Build: `./gradlew build`
- Run: `./gradlew run`
- Clean: `./gradlew clean`
- Checkstyle: `./gradlew checkstyleMain`
- All checks: `./gradlew check`
- Install pre-commit hook: `./gradlew installPreCommitGitHook`

## Additional Resources

- [Micronaut Documentation](https://docs.micronaut.io/4.10.4/guide/index.html)
- [Checkstyle Rules](https://checkstyle.org/checks.html)

---

> **By following these instructions, you help maintain a high-quality, secure, and consistent codebase. Thank you!**
