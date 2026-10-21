# Sprint 11: V2 Hardening and Closeout

Status: planned. Dates and capacity: not assigned. Epic: [V2-E06](../Backlog.md#v2-e06).
Release: [V2](../V2.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Close V2 with reproducible feature, retained-V1, resource, performance, and independent-consumer evidence.

## Entry conditions and dependencies

[Sprint 10][link-01] is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [Distribution specification](../../specifications/Distribution.spec.md) and
[design](../../design/Distribution.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Run the full clean build, retained V1 suite, and all V2 acceptance scenarios without new feature scope.
- Verify artifact contents, independent consumption, publication status, diagnostics, and resource ownership.
- Reconcile every roadmap row, requirement status, explicit deferral, and release-blocking external dependency.
- Collect comparable topic benchmarks and write the V2 release review with exact commands and observed results.

## Acceptance

- V2 and retained V1 acceptance cases pass; every required skip/unavailable result is resolved before its claim closes.
- Distribution claims match actual publication/consumption evidence, not merely prepared metadata.
- Release documents, traceability, benchmark records, and archived plans agree on completed and remaining work.

## Demo and validation

From the repository root:

    mvn clean install

Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.
Also run `mvn -Pbenchmarks test`, inspect both library JARs, and link the release-specific evidence.

## Performance

Review comparable topic baselines, explain material changes, and record missing measurements as incomplete evidence.
Store comparable samples under [performance](../../performance/Readme.md), linking the relevant record here.

## Risks

A local test suite may omit consumer behavior and cleanup under failure; use distinct evidence for each claim.

## Non-goals

All new features and opportunistic API expansion.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../templates/SprintReview.md). A planned charter is not a completed sprint.

[link-01]: Sprint10_V2MavenConsumptionAndDistribution.md
