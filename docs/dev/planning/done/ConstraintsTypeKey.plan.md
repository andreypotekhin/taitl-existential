# Allow constraints with TypeKey

This ExecPlan is a living document. The sections `Progress`, `Surprises & Discoveries`, `Decision Log`, and `Outcomes & Retrospective` must be kept up to date as work proceeds.

This plan is maintained according to `/docs/dev/auto/Plans.md`.

## Purpose / Big Picture

Users can already configure invariants, effects, and transaction lifecycle rules by supplying a `Class<T>` or by constructing the rule objects manually. This works for non-generic types only. After this change, users can specify a `TypeKey<T>` when configuring constraints, effects, and lives so that generic types (for example `List<Order>` or `Document<Json>`) are fully represented in configuration. The configuration indexes will then key handlers by event type plus the `TypeKey`, so runtime events will correctly resolve to the configured handlers. In the same change, the key-holder classes under `/src/main/java/com/taitl/existential/keys/` should move toward immutability by declaring their `key` fields `final` where construction patterns permit it. A user can verify this by configuring a rule with a generic `TypeKey`, running a transaction event for that type, and observing that the handler fires or the invariant is validated.

## Progress

- [x] (2026-02-25 03:06Z) Read planned suggestion, relevant builders, and indexing logic to anchor this ExecPlan.
- [x] (2026-02-25 03:46Z) Added `Evs.typeKey()` and stored optional `TypeKey` in `Invariant`, `Effect`, and `Life`; threaded typed overloads through context/transaction builders.
- [x] (2026-02-25 03:47Z) Updated `IndexConfig` traversal to carry the current rule-set `TypeKey` and construct typed `EventKey`s for entity and transaction handlers.
- [x] (2026-02-25 03:48Z) Hardened key classes by making `EventKey.key`, `RuntimeKey.key`, and `TypeKey.key` final; refactored `TypeKey` key formatting into constructor-time `createKey(...)`.
- [x] (2026-02-25 03:52Z) Added/updated tests for builder propagation, typed indexing, key immutability changes, and TypeKey constructor variants (reflection, class, string); added a spec scenario for generic TypeKey configuration.
- [x] (2026-02-25 03:55Z) Ran `mvn test` successfully for the full project.

## Surprises & Discoveries

- Observation: Configuration indexing currently constructs `EventKey` from the handler instance only, which ignores type information and mismatches runtime `EventKey` creation.
  Evidence: `/src/main/java/com/taitl/ex/logic/configuration/indexes/actions/IndexConfig.java` shows a comment noting the missing `TypeKey` when creating `EventKey`.

- Observation: Adding `EventKey.valueOf(Class<?>, TypeKey<?>)` made the existing `EventKey.valueOf(null, typeKey)` test call ambiguous.
  Evidence: Test compilation failed until `/src/test/java/com/taitl/existential/keys/EventKeyTest.java` casted `null` to `Event<String>`.

- Observation: `UserCanConfigureClassRules` failed when run as a targeted class selection but passed during full-suite execution (`mvn test`), indicating order-dependent behavior in the existing spec harness.
  Evidence: `mvn -q -Dtest=...UserCanConfigureClassRules test` reported “You need to configure at least one context”, while `mvn -q test` exited successfully.

## Decision Log

- Decision: Store a `TypeKey<T>` directly on each rule set (`Invariant`, `Effect`, `Life`) and expose it through the `Evs` interface so the evaluator can access it while indexing.
  Rationale: Indexing runs at the `Evs` level and needs a stable, explicit source of the subject type; storing it on the rule set keeps the data close to where rules are defined and avoids unsafe reflection on generic parameters.
  Date/Author: 2026-02-25 / Codex

- Decision: Keep backward compatibility by allowing `typeKey()` to return `null` and falling back to current `EventKey.valueOf(ev)` behavior when a key is missing.
  Rationale: Existing configurations that do not specify a type should continue to work, and this provides a safe migration path while new TypeKey overloads are added.
  Date/Author: 2026-02-25 / Codex

- Decision: Include opportunistic immutability cleanup in `com.taitl.existential.keys` by making `key` fields `final` where constructor flow allows it, and treat constructor reshaping as part of this feature only if behavior remains unchanged.
  Rationale: The new `TypeKey` work touches key construction paths and tests anyway, so this is a low-cost time to improve immutability guarantees and reduce mutation surface in core key types.
  Date/Author: 2026-02-25 / Codex

## Outcomes & Retrospective

Implemented typed configuration for constraints/effects/lives by propagating `TypeKey` through rule sets, builders, and configuration indexing. The key-class immutability follow-up was completed in the same change by making all `key` fields in `com.taitl.existential.keys` final and removing mutable `TypeKey` key assignment after construction.

Validation is strong on unit/integration coverage for the new behavior: typed builder propagation, typed indexing, and constructor variants (reflection default/full-name, class-based, and string-based) are now exercised. A spec scenario for configuring generic rules with `TypeKey` was added, but the spec harness also exhibited an existing order-dependent issue when run as an isolated target; full project `mvn test` passed.

## Context and Orientation

Rules are configured through builder classes under `/src/main/java/com/taitl/existential/builders/`, primarily `ContextBuilder`, `TransactionBuilder`, `InvariantBuilder`, and `EffectBuilder`. These builders create rule sets (`Invariant`, `Effect`, `Life`) defined in `/src/main/java/com/taitl/existential/invariants/Invariant.java`, `/src/main/java/com/taitl/existential/effects/Effect.java`, and `/src/main/java/com/taitl/existential/transactions/Life.java`. Rule sets implement `Evs<T>` and are gathered into contexts (`/src/main/java/com/taitl/existential/configs/Context.java`) and transactions (`/src/main/java/com/taitl/existential/configs/Transaction.java`).

Configuration indexing happens in `/src/main/java/com/taitl/ex/logic/configuration/indexes/actions/IndexConfig.java`, which traverses all configured `Evs` and constructs `EventKey` entries for lookup. Runtime events are keyed via `EventKey.valueOf(event, typeKey)` in `/src/main/java/com/taitl/ex/logic/indexing/IndexingLogic.java`, so the configuration path must use the same event type + `TypeKey` combination to match. `TypeKey` lives in `/src/main/java/com/taitl/existential/keys/TypeKey.java` and supports representing generic types.

The user story and specification additions belong in `/docs/dev/Specification.md`, and specification-backed tests live under `/src/test/java/com/taitl/existential/specs/`. Builder behavior can be verified in `/src/test/java/com/taitl/existential/builders/` and indexing behavior in `/src/test/java/com/taitl/ex/logic/`.

## Plan of Work

First, extend `Evs` to expose an optional `TypeKey` by adding a default `typeKey()` method that returns `null`. Update `Invariant`, `Effect`, and `Life` to store a `TypeKey<T>` field with getter and setter, plus constructors accepting `TypeKey<T>` and `Class<T>` (the latter should translate to `TypeKey.valueOf(Class<?>)`). This lets manual construction and builder-created instances carry type information.

Next, thread `TypeKey` through the builders. `ContextBuilder` and `TransactionBuilder` should add overloads `invariant(TypeKey<T> typeKey)` and `effect(TypeKey<T> typeKey)` that mirror the existing `Class<T>` overloads, and set the type key on the underlying builder or target. `InvariantBuilder` and `EffectBuilder` should accept a `TypeKey<T>` in their constructors or via a fluent setter and apply it to the target rule set before `build()` returns. For transaction lifecycle rules, add a `Life` factory path that can accept a `TypeKey<T extends Transaction>` so that `TransactionBuilder` can optionally build lives with a type key when explicit transaction types are desired. Ensure `TransactionBuilder.begin/commit/rollback/checkpoint` continues to work and assigns a type key if one is provided or inferred.

Then, fix configuration indexing to use the new type key. In `IndexConfig.TraverseContext`, override `visit(Evs<T> evs)` so it captures the current `TypeKey` and makes it available while visiting the contained `Ev` instances. Add a small helper that maps handler classes (`OnCreate`, `OnRead`, `OnBegin`, etc.) to their corresponding event types (`Create`, `Read`, `Begin`, etc.). Use this mapping plus the captured `TypeKey` to build `EventKey` values (for example `EventKey.valueOf(new Create<>(), typeKey)` or an equivalent class-based constructor if preferred). If no `TypeKey` is available, fall back to the existing `EventKey.valueOf(ev)` behavior to preserve compatibility. This change should align configuration-time keys with runtime keys produced in `IndexingLogic`.

In parallel with the above, refactor key classes under `/src/main/java/com/taitl/existential/keys/` to declare `key` fields `final` where possible, especially `EventKey` and `RuntimeKey`, and evaluate `TypeKey` feasibility. If `TypeKey` cannot be made `final` without disproportionate constructor churn, document the blocker in `Surprises & Discoveries` and keep the change scoped to the classes that can be safely hardened.

After code changes, update documentation and specifications. Add a new user story to `/docs/dev/Specification.md` under configuration workflows stating that users can configure constraints/effects/lives using a `TypeKey` for generic types. Add a spec test under `/src/test/java/com/taitl/existential/specs/` that configures an invariant or effect using a `TypeKey` and asserts that the handler fires for a matching runtime event. Add or extend builder tests in `/src/test/java/com/taitl/existential/builders/` to ensure the new overloads set the type key correctly, and add an indexing test (likely under `/src/test/java/com/taitl/ex/logic/`) to assert that `IndexConfig` creates `EventKey` values that match runtime keys when `TypeKey` is provided. The `TypeKey` tests added or updated for this work must exercise both reflection-based constructors (anonymous subclass variants, including the `boolean useFullName` variant where relevant) and class-/string-based constructors to confirm consistent behavior across construction styles.

Finally, run the full test suite with Maven from the repository root and record the results in this plan. Any failures should be triaged and fixed before marking the plan complete.

## Concrete Steps

1. From the repository root, add `typeKey()` support to `/src/main/java/com/taitl/existential/evaluables/Evs.java` and update `Invariant`, `Effect`, and `Life` with `TypeKey` storage, constructors, and accessors.

2. Add `TypeKey` overloads to `/src/main/java/com/taitl/existential/builders/ContextBuilder.java` and `/src/main/java/com/taitl/existential/builders/TransactionBuilder.java`, and update `/src/main/java/com/taitl/existential/builders/InvariantBuilder.java` and `/src/main/java/com/taitl/existential/builders/EffectBuilder.java` so they set the target `TypeKey` during construction or build.

3. Update `/src/main/java/com/taitl/ex/logic/configuration/indexes/actions/IndexConfig.java` to capture the `TypeKey` when visiting `Evs` and to construct `EventKey` using the corresponding event type plus `TypeKey`, with a fallback to the old behavior when no key exists.

4. Refactor `/src/main/java/com/taitl/existential/keys/` classes to declare `key` fields `final` where safe, preserving behavior and constructor coverage.

5. Add or update tests in `/src/test/java/com/taitl/existential/builders/`, `/src/test/java/com/taitl/ex/logic/`, and `/src/test/java/com/taitl/existential/specs/` to validate `TypeKey`-based configuration behavior and to cover reflection-based plus class/string `TypeKey` constructors.

6. Update `/docs/dev/Specification.md` to include the new user story about configuring constraints/effects/lives with `TypeKey` and mark the story as completed once tests pass.

7. Run tests from the repository root:

   mvn test

   Expected: all tests pass. If any fail, capture the failure summary in `Surprises & Discoveries` and fix before completing this plan.

## Validation and Acceptance

Acceptance is met when a configuration that uses `ContextBuilder.invariant(TypeKey<T>)` or `ContextBuilder.effect(TypeKey<T>)` results in event handlers being invoked for runtime events of the same `TypeKey`, and when transaction lifecycle handlers configured with a `TypeKey<T extends Transaction>` fire for matching transaction instances. The relevant `com.taitl.existential.keys` classes have `final key` fields wherever feasible without behavior changes, and tests explicitly cover reflection-based `TypeKey` construction as well as class- and string-based construction paths. Unit tests and spec tests must pass, and the full `mvn test` suite must complete successfully with no failures.
