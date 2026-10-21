# Sprint 07: V2 Quantifiers and Relationships

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Status: planned. Dates and capacity: not assigned. Epic: [V2-E02](../Backlog.md#v2-e02).
Release: [V2](../V2.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Use All/Exists, collections, and maintained indexes to express relationships between in-memory entities.

## Entry conditions and dependencies

[Sprint 06](Sprint06_V2EventsAndEntityConstraints.md) is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [QuantifiersAndIndexes specification](../../../../specifications/QuantifiersAndIndexes.spec.md) and
[design](../../../../design/QuantifiersAndIndexes.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Reconcile public index names with existing specification terminology and map tests in unused-named packages.
- Specify All/Exists conditions, empty/missing inputs, cardinality, map/set behavior, and composed All-Exists.
- Add specification scenarios for same-class and cross-class relationships and index maintenance on evolution.
- Compare indexed and collection paths including index-maintenance cost and failure behavior.

## Acceptance

- Collection and index forms produce the same required logical result for supported scenarios.
- Empty, missing-key, multi-match, conditional, and mutation cases have explicit expected behavior.
- Documentation distinguishes relationship indexes from internal event/handler indexing.

## Demo and validation

From the repository root:

    mvn -Dtest=AllTest,ExistsTest,SingleIndexTest,MultiIndexTest,SingleJoinTest,MultiJoinTest test

Inspect assertion results and demonstrate the relevant Night City-backed scenario where applicable.
Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Benchmark representative cardinalities and both maintenance and lookup; retain raw samples and scenario parameters.
Store comparable samples under [performance](../../../../performance/Readme.md), linking the relevant record here.

## Risks

Lookup-only timing can hide index-maintenance cost; unit tests alone do not prove transaction-level correctness.

## Non-goals

Persistent/distributed indexes and expression/theorem-solving expansion.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../../../../project/templates/SprintReview.md). A planned charter is not a completed sprint.

[wrap-01]: ../../../../project/ReleaseRebaseline.md
