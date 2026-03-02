# Implement EvaluationLogic.evaluate workflow

This ExecPlan is a living document. The sections `Progress`, `Surprises & Discoveries`, `Decision Log`, and `Outcomes & Retrospective` must be kept up to date as work proceeds.

This plan is maintained according to `/docs/dev/auto/Plans.md`.

## Purpose / Big Picture

`EvaluationLogic.evaluate()` is currently a stub, so validation-stage event handlers and invariant-style handlers are not executed during checkpoint/commit. After this change, the validation stage will iterate runtime events recorded in a transaction, expand each event into elementary events, resolve configured handlers through `EventField`, execute them in declaration order, append constraint violations to `ValidationReport`, and immediately propagate non-violation handler failures. A user can verify this by configuring an invariant and an effect for a create event, sending an event, and observing the effect run while violations are collected (and unexpected handler errors abort validation).

## Progress

- [x] (2026-02-26 20:35Z) Read approved suggestion, planning guidance, and the evaluation/indexing/handler code paths.
- [x] (2026-02-26 20:35Z) Created TODO item `docs/dev/todo/T02262601.implement-EvaluationLogic.evaluate.md` and initialized this ExecPlan.
- [x] (2026-02-26 20:38Z) Implemented `EvaluationLogic.evaluate()` event iteration, event splitting, `MultiKey` lookup, handler dispatch (single + bi-event), and exception routing to `ValidationReport` vs propagation.
- [x] (2026-02-26 20:40Z) Added `EvaluationLogicTest` coverage for effect execution + violation collection and partial-report preservation on propagated handler failure.
- [x] (2026-02-26 20:41Z) Ran `mvn -q -Dtest=EvaluationLogicTest test` successfully.
- [x] (2026-02-26 20:41Z) Ran `mvn -q test`; suite is blocked by unrelated existing failures in `EventKeyTest` and `RuntimeKeyTest` (details recorded below).
- [ ] Move completed plan/TODO/suggestion artifacts to their `done` directories.

## Surprises & Discoveries

- Observation: `EvaluationLogic.evaluate()` currently returns `void` without a `throws` clause, but the required behavior includes propagating non-violation handler failures, which are checked `ExistentialException`s.
  Evidence: `ExecuteHandler.handle(...)`, `OnMutate.handle(...)`, and `OnPort.handle(...)` all declare `throws ExistentialException`.

- Observation: `EventField.get(...)` threw `NullPointerException` when a split runtime event included event keys with no configured handlers (a normal case for `MultiKey` lookups).
  Evidence: New evaluator tests failed in `EventField.get(...)` when only `Create<T>` was configured for a split `Port<T>` event; `configuredHandlers.get(eventKey)` returned `null` and `handlers.addAll(set)` dereferenced it.

- Observation: Full-suite failures are currently present outside the evaluator work in key tests.
  Evidence: `mvn -q test` failed in `com.taitl.existential.keys.EventKeyTest.valueOfFullObject` (nested type-key full-name formatting mismatch) and `com.taitl.existential.keys.RuntimeKeyTest.validateRequiresKeyAndEntity` (NPE in probe constructor path).

## Decision Log

- Decision: Allow `EvaluationLogic.evaluate(...)` to throw `ExistentialException` and propagate that signature through `ValidationLogic.run(...)`.
  Rationale: The suggestion explicitly requires throwing non-violation handler exceptions, and checked exceptions are the library's established error channel for handler failures.
  Date/Author: 2026-02-26 / Codex

- Decision: Add a protected `eventField(Tr)` seam in `EvaluationLogic` to keep `evaluate(...)` testable without relying on the unrelated config-finalization path used by `ex.begin(...)`.
  Rationale: Isolated tests for this feature should exercise evaluator behavior directly and remain deterministic even while configuration finalization has its own standalone-test issues.
  Date/Author: 2026-02-26 / Codex

## Outcomes & Retrospective

Implemented the `EvaluationLogic.evaluate()` workflow requested by `S02262601`: runtime encountered events are iterated, split into elementary runtime keys, grouped into a `MultiKey`, resolved through `EventField`, and executed as event handlers. Constraint violations are appended to `ValidationReport`; non-violation handler failures are propagated immediately, leaving a partial report when applicable.

The work also fixed an enabling bug in `EventField.get(...)` where missing handlers for some split event keys caused a `NullPointerException`. This is required for normal `Port -> [Create/Write/CU/... ]` evaluation when only a subset of those events has configured handlers.

Validation for the new behavior is strong at the targeted level (`EvaluationLogicTest` passes). Full project `mvn -q test` was executed but is currently blocked by unrelated existing failures in key tests; those failures are documented in `Surprises & Discoveries`.

## Context and Orientation

Runtime events are indexed in `/src/main/java/com/taitl/ex/logic/indexing/data/EncounteredUniqueEvents.java` as `RuntimeKey<?>` values on `Tr.runtimeIndexes()`. Each `RuntimeKey` combines an event type plus a runtime entity object. `EvaluationLogic` (`/src/main/java/com/taitl/ex/logic/evaluation/EvaluationLogic.java`) runs during validation (`/src/main/java/com/taitl/ex/logic/validation/ValidationLogic.java`) on checkpoint/commit.

Configured rules are indexed into `Config.indexes().eventField()` (`/src/main/java/com/taitl/ex/logic/configuration/indexes/data/EventField.java`) and retrieved by `MultiKey`, which is a comma-joined list of event keys representing the split elementary events for a single runtime event. Event splitting is implemented in `/src/main/java/com/taitl/ex/logic/evaluation/logic/EventSplitter.java` (wrapped by `SplitEvent`).

Handlers in this codebase are `Ev<?>` entries, usually implementations of `EventHandler<?>` (for single-value events) or `BiEventHandlerWithSideEffects<?>` (for two-value events like mutate/port). Single-value `On<T>` handlers use `ExecuteHandler.handle(...)`; `OnMutate` and `OnPort` expose `handle(t0, t1)` directly. Constraint-like immutable handlers may throw condition failures that should be recorded in `ValidationReport`, while other handler failures should abort evaluation.

## Plan of Work

Implement the evaluator by iterating `tr.runtimeIndexes().encounteredUniqueEvents.stream()`. For each encountered runtime key, use `SplitEvent` to produce split runtime keys and derive a `MultiKey` from their `EventKey`s. Resolve configured `Ev<?>` entries from the operation config's `EventField` and execute only event handlers. For single-value handlers (`On<?>` or `EventHandlerWithSideEffects<?>` implementations based on `On`), execute with the split runtime key entity. For bi-event handlers (`OnMutate`, `OnPort` via `BiEventHandlerWithSideEffects<?>`), extract `t0`/`t1` from the split runtime key event when it is a `BiEvent<?>` and invoke the bi-handler.

Add exception routing helpers in `EvaluationLogic` so constraint violations are added to `ValidationReport` and all other `ExistentialException`s are rethrown. Include a conservative rule for immutable bi-handlers, which currently report condition failures as `EventHandlerException` with no cause and a "condition is not met" message.

Update `ValidationLogic.run(...)` to declare `throws ExistentialException` so propagated evaluator failures can escape the validation stage immediately, while `ValidationStageExceptions` are still thrown when the report contains collected violations.

Add tests (integration-style against a real `Existential` instance) to verify: (1) a configured effect runs during evaluation, (2) an invariant failure is collected into `ValidationReport` without immediate throw, and (3) a non-violation handler failure is propagated and may leave a partial report.

## Concrete Steps

1. Edit `/src/main/java/com/taitl/ex/logic/evaluation/EvaluationLogic.java` to implement event iteration/splitting/lookup/handler execution and exception routing.
2. Edit `/src/main/java/com/taitl/ex/logic/validation/ValidationLogic.java` to propagate checked exceptions from the evaluator.
3. Add tests in `/src/test/java/com/taitl/ex/logic/evaluation/` covering success, report collection, and propagation semantics.
4. Run from repository root:

   mvn -q test

   Result in this run: command executed, but failed due to unrelated pre-existing key test failures (`EventKeyTest`, `RuntimeKeyTest`).

5. Update this plan with final progress, discoveries, and outcomes; move it to `/docs/dev/planning/done/` and move the matching TODO/suggestion files to their `done` directories.

## Validation and Acceptance

Acceptance is met when `EvaluationLogic.evaluate()` executes configured handlers for runtime events captured in a transaction, collects constraint violations into `ValidationReport`, and propagates non-violation handler exceptions. The behavior must be demonstrated by automated tests, and the full Maven test suite (`mvn -q test`) must pass.

Behavioral acceptance for the evaluator implementation is met by `/src/test/java/com/taitl/ex/logic/evaluation/EvaluationLogicTest.java`. Full-suite acceptance remains blocked by unrelated key test failures described above.

## Idempotence and Recovery

The code and test changes are additive and safe to rerun. If a test fails after partial implementation, rerun the targeted test class while iterating, then rerun `mvn -q test` before completion. The documentation artifact moves are simple file moves; if interrupted, the files can be moved again without content loss.

## Artifacts and Notes

This plan is intentionally scoped to the `EvaluationLogic.evaluate()` stub and its immediate validation-stage integration point.

## Interfaces and Dependencies

No new dependencies are required. The implementation should use the existing types and helpers: `Tr`, `EncounteredUniqueEvents`, `SplitEvent`, `MultiKey`, `EventField`, `ValidationReport`, `On`, `ExecuteHandler`, `BiEventHandlerWithSideEffects`, and `ExistentialException` subclasses.

Revision note (2026-02-26 / Codex): Updated the plan after implementation to record completed steps, the `EventField` null-handler discovery/fix, targeted and full-suite test results, and the remaining unrelated test blockers.
