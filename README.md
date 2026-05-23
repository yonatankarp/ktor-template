# ktor-template

A template for bootstrapping new Ktor 3.x web services, with the same operational baseline as `kotlin-spring-boot-template`: Gradle (Kotlin DSL) + CI (GitHub Actions) + Docker + Postgres (Flyway + Exposed) + Testcontainers + OpenAPI spec + C4 diagrams.

## Status

Slice 1: project skeleton + Gradle conventions + `/health` endpoint. Iterating in vertical slices — see commits.

## Prerequisites

- JDK 25
- Docker (for upcoming DB slices)

## Build & run

```shell
./gradlew build              # compile + spotless + test + jacoco
./gradlew run                # local run
./gradlew test               # tests only
./gradlew buildFatJar        # build/libs/ktor-template.jar
./gradlew runFatJar          # build and run the fat jar
```

## Smoke test

Once running:

```shell
curl http://localhost:8080/health   # → OK
```

## Layout

- `gradle/libs.versions.toml` — single source of truth for versions and dependency bundles
- `buildSrc/` — convention plugins (`ktor-template.kotlin-conventions`, `ktor-template.code-metrics`)
- Three Gradle modules, hexagonal architecture, dependency direction enforced at compile time:
  - `ktor-template-domain/` — pure Kotlin, zero framework dependencies
  - `ktor-template-application/` — ports + use cases (depends on `:ktor-template-domain`)
  - `ktor-template-adapters/` — Ktor entry point, input/http adapters, output/persistence adapters, DI wiring (depends on `:ktor-template-application`)
- Source folders under each module follow `src/main/kotlin/com/yonatankarp/ktor/template/<layer>/`
