# Allow creating constraints with TypeKey

This ExecPlan is a living document. The sections `Progress`, `Surprises & Discoveries`, `Decision Log`, and `Outcomes & Retrospective` must be kept up to date as work proceeds.

See `/docs/dev/auto/Plans.md` for the ExecPlan contract and keep this document aligned with those requirements.

## Purpose / Big Picture

Users should be able to declare invariants, effects, and transaction lifecycle rules for generic types and custom transaction types by providing a `TypeKey<T>` instead of only a `Class<T>`. After this change, a user can configure a context or transaction with rules like `context().invariant(typeKey)` or `transaction().begin(typeKey, ...)`, and have those rules fire correctly when events are emitted with the same `TypeKey`. The behavior should be demonstrable by tests that configure a generic type (for example `List<String>`) and a custom `Transaction` subclass using `TypeKey` and then observe that the configured rules execute during a transaction.

## Progress

- [x] (2026-03-01 18:01Z) Created ExecPlan for allowing TypeKey-based constraints and lifecycle rules.
- [x] (2026-03-01 18:09Z) Audited API surface and confirmed `Invariant`, `Effect`, `Life`, and builder entry points already support `TypeKey`.
- [x] (2026-03-01 18:11Z) Kept API unchanged (no missing overloads found); focused implementation on missing specification coverage.
- [x] (2026-03-01 18:13Z) Added spec-level invariant coverage for `TypeKey<List<String>>` in `UserCanConfigureClassRules`.
- [x] (2026-03-01 18:14Z) Added spec-level lifecycle side-effect coverage for custom transaction type using `TypeKey<ChildTransaction>` in `UserCanConfigureCustomTransactions`.
- [x] (2026-03-01 18:16Z) Ran full test suite with `mvn -q test` and confirmed passing.

## Surprises & Discoveries

- Observation: A parallel Maven invocation produced false failures (`You need to configure at least one context`) across spec tests.
  Evidence: Running `mvn -q -Dtest=... test` concurrently with `mvn -q test` caused singleton state interference via `Ex.instance(...)`; sequential `mvn -q test` passed.

## Decision Log

- Decision: Keep all changes additive and avoid breaking existing `Class<T>` overloads.
  Rationale: The feature is an extension for generic types; compatibility with existing API usage must be preserved.
  Date/Author: 2026-03-01 / Codex Planner
- Decision: Do not change production code because the TypeKey API surface was already complete on `origin/master`.
  Rationale: The implementation gap was test/spec verification, not missing constructors or builders.
  Date/Author: 2026-03-01 / Codex Planner

## Outcomes & Retrospective

The feature intent is now validated by specification tests without changing library runtime code. The library already supported `TypeKey` for invariants, effects, and lifecycle rules. The completed work adds explicit end-to-end proof for two previously unverified claims: invariant evaluation for generic `TypeKey<List<String>>` and lifecycle begin-side effects for a custom transaction type via `TypeKey<ChildTransaction>`. Full suite validation passed with a sequential run.

## Context and Orientation

The core rule types live in `/src/main/java/com/taitl/existential/constraints/Invariant.java`, `/src/main/java/com/taitl/existential/constraints/Effect.java`, and `/src/main/java/com/taitl/existential/constraints/Life.java`. These classes are responsible for rule configuration and currently expose constructors for `Class<T>` and, in some places, `TypeKey<T>`. The `TypeKey` type itself is in `/src/main/java/com/taitl/existential/keys/TypeKey.java` and is used throughout the library to represent generic or fully qualified types. Builders for fluent configuration are in `/src/main/java/com/taitl/existential/builders/ContextBuilder.java`, `/src/main/java/com/taitl/existential/builders/TransactionBuilder.java`, `/src/main/java/com/taitl/existential/builders/InvariantBuilder.java`, and `/src/main/java/com/taitl/existential/builders/EffectBuilder.java`. These builders are the primary end-user entry points for configuring constraints and effects, and are where missing `TypeKey` overloads would be most user-visible.

Specification claims live in `/docs/dev/Specification.md` and tests that back those claims live in `/src/test/java/com/taitl/existential/specs`. The existing specs already include a test for configuring class rules with a `TypeKey`, but there is no explicit coverage for `TypeKey`-based invariants or transaction lifecycle rules for custom transaction types. The plan below closes that gap and ensures the spec’s “+” status corresponds to real tests.

## Plan of Work

First, review the current API surface in `Invariant`, `Effect`, and `Life` and in their builders to identify where `TypeKey` is already supported and where it is not. Focus on how a user configures rules through `ContextBuilder` and `TransactionBuilder`, because those are the most common entry points. Ensure each rule type supports a `TypeKey<T>` path that does not require falling back to `Class<T>` and that this path is reachable from both context-level and transaction-level configuration. If any overloads or constructor variants are missing, add them alongside existing `Class<T>` overloads and keep validation logic consistent with existing `sane(...)` checks.

Next, update builder code so that `TypeKey` variants are accepted in fluent chains and are retained through to the constructed `Invariant`, `Effect`, or `Life` instances. This may require adjusting builder constructors or helper methods, or adding new builder entry points if a class currently only supports `Class<T>` for a particular rule type. Pay special attention to transaction lifecycle methods in `TransactionBuilder` to ensure `TypeKey<T>` overloads exist and are wired to `Life<T>` with the correct `typeKey`.

Then, add specification tests that demonstrate end-to-end behavior. The tests should be placed under `/src/test/java/com/taitl/existential/specs` and should cover:

A context-level invariant configured with `TypeKey<List<String>>` that validates during commit when an event with that `TypeKey` is emitted. A context-level effect configured with a `TypeKey` should be used as a reference if needed, but ensure there is explicit invariant coverage. A transaction lifecycle handler (begin/commit/rollback/checkpoint) configured with `TypeKey<CustomTransaction>` that runs for a transaction instance of that custom type. Use a small custom `Transaction` subclass inside the test to keep scope tight.

Finally, update `/docs/dev/Specification.md` to ensure the relevant claims are marked as complete only if backed by tests, and update the Readme if it should mention TypeKey-based configuration of rules beyond just type key formatting. Run the test suite to verify the behavior and document the expected passing output and new test names in the plan.

## Concrete Steps

Work from the repository root `/Users/chaos/.codex/worktrees/c9e2/taitl-existential`.

1. Inspect rule classes and builders for `TypeKey` support. Use `rg -n "TypeKey"` in the constraint and builder packages and confirm which overloads already exist.
2. Implement any missing `TypeKey` overloads in `ContextBuilder`, `TransactionBuilder`, `InvariantBuilder`, `EffectBuilder`, or `Life` wiring so that users can construct rules with `TypeKey` without downcasting or manual field setting.
3. Add or update tests in `/src/test/java/com/taitl/existential/specs` to cover:
   - Invariant configuration for a generic type using `TypeKey<List<String>>` and event emission with that same `TypeKey`.
   - Transaction lifecycle handler configuration using `TypeKey<CustomTransaction>` and confirmation that the handler runs for that transaction type.
4. Update `/docs/dev/Specification.md` to ensure the `TypeKey`-related claims are prefixed with `+` only when the tests exist and pass. If a new claim is needed for lifecycle rules, add it and back it with a test.
5. Run `mvn -q test` and note that the new spec tests fail before the change and pass after.

Expected command examples (run from repo root):

    rg -n "TypeKey" src/main/java/com/taitl/existential/constraints src/main/java/com/taitl/existential/builders
    mvn -q test

## Validation and Acceptance

Acceptance is reached when a user can configure invariants, effects, and lifecycle rules using `TypeKey` and observe correct behavior through tests. Run `mvn -q test` and confirm all tests pass. Specifically confirm that the new spec tests for `TypeKey`-based invariants and lifecycle handlers fail before the change and pass after it. The behavior should be observable by seeing the configured handler execute (for example, by incrementing an `AtomicInteger` or by the absence of a validation error when a `TypeKey`-based invariant is configured and satisfied).

## Idempotence and Recovery

The steps are additive and safe to re-run. If a change is incorrect, revert the affected file edits and re-run the same tests. No migrations or destructive operations are required.

## Artifacts and Notes

Key evidence from this implementation:

    mvn -q test
    [process exit code 0]

Implemented spec tests:

    UserCanConfigureClassRules.configureRulesWithTypeKeyForGenericType
    UserCanConfigureCustomTransactions.lifecycleRulesWithTypeKeyForCustomTransaction

## Interfaces and Dependencies

The final API surface should include `TypeKey<T>` entry points for constraint and lifecycle configuration. At minimum, ensure these signatures (or their equivalents) exist and are used by tests:

- `public Invariant(TypeKey<T> typeKey)` in `/src/main/java/com/taitl/existential/constraints/Invariant.java`.
- `public Effect(TypeKey<T> typeKey)` in `/src/main/java/com/taitl/existential/constraints/Effect.java`.
- `public Life(TypeKey<T> typeKey)` in `/src/main/java/com/taitl/existential/constraints/Life.java`.
- `public <T> InvariantBuilder<T> invariant(TypeKey<T> typeKey)` in `/src/main/java/com/taitl/existential/builders/ContextBuilder.java` and `/src/main/java/com/taitl/existential/builders/TransactionBuilder.java`.
- `public <T> EffectBuilder<T> effect(TypeKey<T> typeKey)` in `/src/main/java/com/taitl/existential/builders/ContextBuilder.java` and `/src/main/java/com/taitl/existential/builders/TransactionBuilder.java`.
- `public <T extends Transaction> TransactionBuilder begin(TypeKey<T> typeKey, Consumer<? super T> action)` (and equivalent lifecycle methods) in `/src/main/java/com/taitl/existential/builders/TransactionBuilder.java`.

If any of these are missing, add them without removing the existing `Class<T>` overloads, and preserve behavior by wiring the overloads to the same internal `TypeKey` path.

Plan update note (2026-03-01): Implemented by adding missing spec tests and validating with full suite; moved plan to done.
