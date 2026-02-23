# Implement library configuration loading and user-facing documentation

This execution plan is a living document. The sections `Progress`, `Surprises & Discoveries`, `Decision Log`, 
and `Outcomes & Retrospective` must be kept up to date as work proceeds.

This document is maintained in accordance with
`/Users/chaos/Files/Dev/Code/taitl-existential/docs/dev/auto/Plans.md`.

## Purpose / Big Picture

After this change, library options can be configured from either an env-selected file or a classpath fallback
resource. The startup orchestration is owned by `ExistentialInit`, and loading/parsing remains delegated to
`ConfigureLibrary`. End users can discover and troubleshoot configuration in `docs/Usage.md` and
`Troubleshooting.md`.

## Progress

- [x] (2026-02-19 01:56Z) Reviewed planning contract and library configuration specification.
- [x] (2026-02-19 01:56Z) Chosen source order: env-file first, classpath fallback second.
- [x] (2026-02-19 01:56Z) Chosen orchestrator: `ExistentialInit` owns startup orchestration.
- [x] (2026-02-19 01:56Z) Chosen docs scope: update `docs/Usage.md` and `Troubleshooting.md`.
- [x] (2026-02-19 02:00Z) Implemented `ConfigureLibrary` loading, validation, and flag application.
- [x] (2026-02-19 02:00Z) Wired startup auto-configuration through `ExistentialInit.startup()`.
- [x] (2026-02-19 02:00Z) Added classpath fallback resource `src/main/resources/existential.properties`.
- [x] (2026-02-19 02:00Z) Added/updated tests for file, classpath, env selection, and failure modes.
- [x] (2026-02-19 02:00Z) Updated user-facing docs and marked specification claims complete.
- [ ] Run full build and resolve any remaining failures.

## Surprises & Discoveries

- Observation: `ConfigureLibrary` already existed as a placeholder, so the safest path was completing this class
  instead of introducing a new loader type.
  Evidence: file contained only TODO comments before implementation.

## Decision Log

- Decision: Keep `Ex` and `Existential` public API unchanged for source selection.
  Rationale: startup auto-configuration was explicitly preferred.
  Date/Author: 2026-02-19 / User + Codex

- Decision: `ExistentialInit` orchestrates startup configuration and delegates execution to `ConfigureLibrary`.
  Rationale: keeps startup orchestration centralized while preserving loader single responsibility.
  Date/Author: 2026-02-19 / User + Codex

- Decision: Use `EXISTENTIAL_CONFIG_FILE` and classpath fallback `existential.properties`.
  Rationale: deterministic source order without external file auto-creation.
  Date/Author: 2026-02-19 / User + Codex

## Outcomes & Retrospective

Implementation is complete in code and docs, pending final full build verification.

## Context and Orientation

Relevant files:
- `src/main/java/com/taitl/ex/core/existential/ExistentialInit.java`
- `src/main/java/com/taitl/ex/logic/library/ConfigureLibrary.java`
- `src/main/java/com/taitl/existential/Existential.java`
- `src/main/resources/existential.properties`
- `src/test/java/com/taitl/existential/specs/library_configuration/UserCanConfigureLibrary.java`
- `src/test/java/com/taitl/ex/logic/library/ConfigureLibraryTest.java`
- `docs/Usage.md`
- `Troubleshooting.md`
- `docs/dev/Specification.md`

## Plan of Work

Implement startup orchestration in `ExistentialInit.startup()`, with `ConfigureLibrary.configure()` selecting either
an env-specified properties file or classpath fallback. Validate keys and values strictly, fail fast with messages
that link to troubleshooting. Add tests for positive and negative paths. Update user-facing docs with quickstart and
minimal troubleshooting flow. Mark completed specification stories with `+`.

## Concrete Steps

Working directory:
`/Users/chaos/Files/Dev/Code/taitl-existential`

Commands:

    mvn -Dtest=com.taitl.existential.specs.library_configuration.UserCanConfigureLibrary test
    mvn -Dtest=com.taitl.ex.logic.library.ConfigureLibraryTest test
    mvn -q test
    mvn clean install

## Validation and Acceptance

Acceptance criteria:
- Env-selected config file updates behavior flags.
- Classpath fallback applies when env var is not set.
- Invalid source/key/value fails with troubleshooting-linked messages.
- `Library configuration` stories in `docs/dev/Specification.md` are marked complete.
- Full build and tests pass.

## Idempotence and Recovery

Loader is idempotent for repeated startup with unchanged input. Recovery from failures is by fixing config source,
key names, or values, then rerunning startup.

## Artifacts and Notes

Default classpath resource:

    behavior.rules.requireDescriptions=false

Troubleshooting anchor used by exceptions:

    /Troubleshooting.md#library-configuration-load-failure

## Interfaces and Dependencies

No new third-party dependencies were added. Existing public APIs remain unchanged. Internal startup behavior now
routes through `ExistentialInit` into `ConfigureLibrary`.

Revision note (2026-02-19): Added final implementation details and documentation scope decisions, and aligned the
plan with completed code edits.
