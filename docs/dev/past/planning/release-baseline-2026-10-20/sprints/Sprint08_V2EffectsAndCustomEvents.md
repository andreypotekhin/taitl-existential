# Sprint 08: V2 Effects and Custom Events

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Status: planned. Dates and capacity: not assigned. Epic: [V2-E03](../Backlog.md#v2-e03).
Release: [V2](../V2.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Attach effects and custom events with explicit timing, ordering, intent interactions, and failure behavior.

## Entry conditions and dependencies

[Sprint 07](Sprint07_V2QuantifiersAndRelationships.md) is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [IntentsAndEffects specification](../../../../specifications/IntentsAndEffects.spec.md) and
[design](../../../../design/IntentsAndEffects.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Resolve custom event declaration/registration, identity/collisions, splitting, and handler matching.
- Specify effect order across stages, contexts, transactions, and derived events.
- Cover partial handler failure, custom transaction event types, and custom-event intent enforcement.
- Document caller ownership of side effects and the lack of automatic rollback for arbitrary actions.

## Acceptance

- A custom event can be declared, emitted, matched, and rejected when invalid according to its specification.
- Effects execute at the specified stage/count/order, including failure and combined-event cases.
- Registration/split/intent behavior has full specification tests and corrective diagnostics.

## Demo and validation

From the repository root:

    mvn -Dtest=UserCanHandleCombinedEventEffects,UserCanConfigureIntents,ExecuteHandlersTest test

Inspect assertion results and demonstrate the relevant Night City-backed scenario where applicable.
Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Measure dispatch with no-op handlers separately from handler work and custom-event splitting.
Store comparable samples under [performance](../../../../performance/Readme.md), linking the relevant record here.

## Risks

Global event-type registration and arbitrary user callbacks can couple operations or leave partial effects.

## Non-goals

Distributed effect orchestration and automatic compensation/atomicity guarantees.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../../../../project/templates/SprintReview.md). A planned charter is not a completed sprint.

[wrap-01]: ../../../../project/ReleaseRebaseline.md
