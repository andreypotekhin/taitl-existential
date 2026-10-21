# Sprint 01: V1 Library Access and Configuration

Status: planned. Dates and capacity: not assigned. Epic: [V1-E01](../Backlog.md#v1-e01).
Release: [V1](../V1.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Configure independent library instances and use predictable configuration sources without cross-instance state.

## Entry conditions and dependencies

The documentation/planning foundation is available; release feature work has not been declared complete.
Use the [agent process](../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [LibraryAndConfiguration specification](../../specifications/LibraryAndConfiguration.spec.md) and
[design](../../design/LibraryAndConfiguration.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Review LC library-access/loading requirements against the existing specification tests; retain verified behavior.
- Add acceptance cases for two independently configured instances and multiple operations/contexts/rules.
- Resolve configuration-source precedence, behavioral flags, freeze timing, and configuration/injection concurrency.
- Reconcile Setup JDK guidance with compiler release 11 and document tested environments without expanding claims.

## Acceptance

- Two instances with conflicting rules do not share transaction/configuration state.
- Configuration file/resource/flag paths and invalid input have reproducible positive and negative tests.
- Every V1 library/configuration claim has requirement-level evidence or an explicit release blocker.

## Demo and validation

From the repository root:

    mvn -Dtest=UserCanAccessLibrary,UserCanConfigureLibrary test

Inspect assertion results and demonstrate the relevant Night City-backed scenario where applicable.
Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Configuration and instance startup are measured separately from the transaction hot path.
Store comparable samples under [performance](../../performance/Readme.md), linking the relevant record here.

## Risks

Global Creator injection and singleton replacement may couple tests; do not mistake test cleanup for instance isolation.

## Non-goals

New configuration strategy APIs, hot reload, and new supported-JDK promises.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../templates/SprintReview.md). A planned charter is not a completed sprint.
