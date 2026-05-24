# ktor-template

[build-badge]: https://github.com/yonatankarp/ktor-template/actions/workflows/build.yml/badge.svg
[build-state]: https://github.com/yonatankarp/ktor-template/actions/workflows/build.yml
[codeql-badge]: https://github.com/yonatankarp/ktor-template/actions/workflows/codeql.yml/badge.svg
[codeql-state]: https://github.com/yonatankarp/ktor-template/actions/workflows/codeql.yml
[license-badge]: https://img.shields.io/badge/License-MIT-yellow.svg
[license-link]: https://opensource.org/licenses/MIT
[ktor-badge]: https://img.shields.io/badge/Ktor-3-orange?logo=ktor&logoColor=white
[ktor-link]: https://ktor.io/
[kotlin-badge]: https://img.shields.io/badge/Kotlin-2-7F52FF?logo=kotlin&logoColor=white
[kotlin-link]: https://kotlinlang.org/
[jdk-badge]: https://img.shields.io/badge/JDK-25-007396?logo=openjdk&logoColor=white
[jdk-link]: https://openjdk.org/
[gradle-badge]: https://img.shields.io/badge/Gradle-9-02303A?logo=gradle&logoColor=white
[gradle-link]: https://gradle.org/
[postgres-badge]: https://img.shields.io/badge/Postgres-18-336791?logo=postgresql&logoColor=white
[postgres-link]: https://www.postgresql.org/
[exposed-badge]: https://img.shields.io/badge/Exposed-1-7F52FF?logo=jetbrains&logoColor=white
[exposed-link]: https://github.com/JetBrains/Exposed
[kotest-badge]: https://img.shields.io/badge/Kotest-6-3DA639
[kotest-link]: https://kotest.io/

| **Type**     | **Status**                                                                                  |
|--------------|---------------------------------------------------------------------------------------------|
| CI pipelines | [![Build][build-badge]][build-state] [![CodeQL][codeql-badge]][codeql-state]                |
| Stack        | [![Ktor][ktor-badge]][ktor-link] [![Kotlin][kotlin-badge]][kotlin-link] [![JDK][jdk-badge]][jdk-link] [![Gradle][gradle-badge]][gradle-link] [![Postgres][postgres-badge]][postgres-link] [![Exposed][exposed-badge]][exposed-link] [![Kotest][kotest-badge]][kotest-link] |
| License      | [![License: MIT][license-badge]][license-link]                                              |

Pinned versions live in [`gradle/libs.versions.toml`](./gradle/libs.versions.toml) — badges show the major-version line.

## Purpose

A template for bootstrapping new Ktor web services with the same operational baseline as [`kotlin-spring-boot-template`](https://github.com/yonatankarp/kotlin-spring-boot-template), so a new service has CI, Docker, Postgres, observability, and architectural discipline from minute one instead of after the first sprint.

For the C4 diagram of the architecture see [`docs/c4/README.md`](./docs/c4/README.md).

## What's inside

- Ktor server (Netty engine) on JDK 25, Kotlin
- Multi-module Gradle build with hexagonal architecture enforced at compile time:
  - `ktor-template-domain/` — pure Kotlin, zero framework dependencies
  - `ktor-template-application/` — input/output ports + use cases, framework-free
  - `ktor-template-adapters/` — Ktor routes, Exposed catalogs, observability subscribers, DI wiring
- HikariCP + Flyway + Exposed (DSL, `newSuspendedTransaction`) against Postgres
- `ContentNegotiation` (kotlinx-serialization JSON), `StatusPages`-ready
- Observability: `CallId` (X-Request-Id with UUID generation), `CallLogging` (with MDC + configurable quiet-paths), Micrometer + Prometheus `/metrics` endpoint with JVM and process meter binders
- Kotest + MockK test stack; non-overlapping `test_unit` / `test_integration` bundles so domain stays container-free even in tests
- Testcontainers Postgres in `ktor-template-adapters` integration tests
- One demo feature flowing through all three layers: `GET /greetings/random` against a seeded `greeting` table
- Self-contained CI: `build`, `codeql`, `generate-c4-diagram` workflows; Dependabot auto-merge for semver-minor/patch
- Dockerfile (multi-stage, `eclipse-temurin:25-jre-alpine`, USER 65534)
- `docker-compose.yml` (Postgres only — app runs via `./gradlew run` against the container)
- `bin/init.py` rename script (self-destructs after running)

## Setup

After cloning, run:

```shell
./bin/init.py
```

You'll be asked for:
1. The new port (or press Enter for `8080`)
2. The new component name (e.g. `order-service`) — replaces the `ktor-template` slug across build files, CI, Docker, README
3. The new Kotlin package (e.g. `com.acme.orders`) — replaces `com.yonatankarp.ktor.template` across all source files and renames the matching directories

The script self-destructs `bin/` once finished. Commit the result before you start adding features.

### Enable CI pipelines

After pushing the renamed repo to GitHub:
- Set `REVIEWER_GITHUB_TOKEN` in repository secrets, exposed to both Actions and Dependabot
- Create a branch ruleset for `main` (and one for Dependabot)
- Enable auto-merge on the repo (Settings → General → Pull Requests → Allow auto-merge)

## Getting started

### Prerequisites

- JDK 25
- Docker (for Postgres + integration tests)

### Build

```shell
./gradlew build
```

Runs compile + Spotless (ktlint) + tests + JaCoCo on every module.

### Run locally

Start Postgres:

```shell
docker compose up -d
```

Start the app (uses `application-dev.yaml` with dev defaults that match `docker-compose.yml`):

```shell
./gradlew run
```

Smoke test:

```shell
curl http://localhost:8080/health             # OK
curl http://localhost:8080/greetings/random   # {"language":"en","message":"Hello, World!"}
curl http://localhost:8080/metrics | head -30 # Prometheus exposition format
```

### Production-shaped run

`application.yaml` has no defaults — production deploys must supply env vars:

```shell
DB_URL=jdbc:postgresql://... \
DB_USERNAME=... \
DB_PASSWORD=... \
KTOR_ENV=prod \
java -jar ktor-template-adapters/build/libs/ktor-template.jar
```

### Tests

```shell
./gradlew test          # all modules
./gradlew :ktor-template-domain:test          # domain only (no Docker needed)
./gradlew :ktor-template-application:test     # application only (no Docker)
./gradlew :ktor-template-adapters:test        # adapters (Testcontainers spins up Postgres)
```

### Code style

Spotless + ktlint enforce style on every build:

```shell
./gradlew spotlessApply   # apply style fixes
./gradlew spotlessCheck   # check without applying
```

## Built with

- [Kotlin](https://kotlinlang.org/)
- [Ktor](https://ktor.io/) (Netty engine)
- [Exposed](https://github.com/JetBrains/Exposed) — JDBC DSL with coroutine support
- [Flyway](https://flywaydb.org/) — migrations
- [HikariCP](https://github.com/brettwooldridge/HikariCP) — connection pool
- [Micrometer](https://micrometer.io/) — metrics
- [Kotest](https://kotest.io/) + [MockK](https://mockk.io/) — testing
- [Testcontainers](https://testcontainers.com/) — integration tests
- [Gradle](https://gradle.org/) + [`io.ktor.plugin`](https://ktor.io/docs/server-fatjar.html)
- [Docker](https://www.docker.com/)
- [GitHub Actions](https://docs.github.com/en/actions)

## License

[MIT](LICENSE)

## Author

**Yonatan Karp-Rudin** — [github.com/yonatankarp](https://github.com/yonatankarp)
