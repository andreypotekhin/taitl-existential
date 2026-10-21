# Sprint 09: V2 Reporting and Housekeeping

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Status: planned. Dates and capacity: not assigned. Epic: [V2-E04](../Backlog.md#v2-e04).
Release: [V2](../V2.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Receive useful violation details and release transaction/global resources under documented ownership rules.

## Entry conditions and dependencies

[Sprint 08](Sprint08_V2EffectsAndCustomEvents.md) is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [Housekeeping specification](../../../../specifications/Housekeeping.spec.md) and
[design](../../../../design/Housekeeping.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Specify violation descriptions, error numbers, description requirements, and troubleshooting links.
- Define cleanup for every normal and failed lifecycle exit, including library close and abandoned transactions.
- Specify cache ownership/invalidation/boundedness before completing currently unavailable cache operations.
- Define observable leak reporting and test retained references without relying on deterministic garbage collection.

## Acceptance

- Violation reports preserve the promised details and useful remedies, including partial failure cases.
- Bounded repeated operations release transaction entities, memo state, and prepared overlays as specified.
- Caching/leak-reporting claims have direct evidence or remain explicit release blockers.

## Demo and validation

From the repository root:

    mvn -Dtest=ExistentialExceptionTest,TrRegistryTest,TypeKeyCacheTest test

Inspect assertion results and demonstrate the relevant Night City-backed scenario where applicable.
Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Record transaction throughput and retained-state counts separately, under repeatable failure and normal workloads.
Store comparable samples under [performance](../../../../performance/Readme.md), linking the relevant record here.

## Risks

A timing benchmark cannot prove absence of leaks; cached entities and failed lifecycle exits need direct inspection.

## Non-goals

New monitoring/dashboard APIs and speculative cache optimizations outside the admitted specification.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../../../../project/templates/SprintReview.md). A planned charter is not a completed sprint.

[wrap-01]: ../../../../project/ReleaseRebaseline.md
