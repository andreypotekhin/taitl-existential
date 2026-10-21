# Sprint 06: V2 Events and Entity Constraints

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Status: planned. Dates and capacity: not assigned. Epic: [V2-E01](../Backlog.md#v2-e01).
Release: [V2](../V2.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Express and enforce entity, evolution, cross-entity, and subclass constraints through documented events.

## Entry conditions and dependencies

V1 has met its [release exit criteria](../V1.md#exit-criteria); review all carried-forward V2 prerequisites.
Use the [agent process](../../../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [EventsAndConstraints specification](../../../../specifications/EventsAndConstraints.spec.md) and
[design](../../../../design/EventsAndConstraints.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Review the V2 entry state after V1 closure and finalize event/constraint acceptance cases.
- Cover fields, third-party classes, collections, transitions/ports, and additional event values.
- Specify parent/subclass/interface/generic matching and test both supported and rejected combinations.
- Verify memo requirements, event input validation, deduplication, and constraint enforcement end to end.

## Acceptance

- Every V2 entity/evolution/subclass claim has user-facing positive and negative evidence.
- Before/after null rules and matching behavior are explicit, reproducible, and documented.
- Cross-entity predicate constraints work; All/Exists relationship completion remains owned by Sprint 07.

## Demo and validation

From the repository root:

    mvn -Dtest=UserCanSendEntityEvents,UserCanUseMemoForBiEvents test

Inspect assertion results and demonstrate the relevant Night City-backed scenario where applicable.
Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Measure entity/event cardinalities, split/memo overhead, and inherited handler lookup.
Store comparable samples under [performance](../../../../performance/Readme.md), linking the relevant record here.

## Risks

Mutable entity identity and generic matching can change which constraints execute.

## Non-goals

Custom-event extension APIs and new quantifier/index forms.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../../../../project/templates/SprintReview.md). A planned charter is not a completed sprint.

[wrap-01]: ../../../../project/ReleaseRebaseline.md
