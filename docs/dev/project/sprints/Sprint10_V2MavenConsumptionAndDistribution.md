# Sprint 10: V2 Maven Consumption and Distribution

Status: planned. Dates and capacity: not assigned. Epic: [V2-E05](../Backlog.md#v2-e05).
Release: [V2](../V2.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Consume the correct artifact from a separate Maven project using documented coordinates and prerequisites.

## Entry conditions and dependencies

[Sprint 09](Sprint09_V2ReportingAndHousekeeping.md) is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [Distribution specification](../../specifications/Distribution.spec.md) and
[design](../../design/Distribution.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Define normal/assembled artifact expectations, source/Javadoc metadata, and consumer dependencies.
- Create independent consumer evidence against the installed artifact without the library test/example classpath.
- Document publishing namespace/account, signing, credentials, destination, and compatibility prerequisites.
- Separate local consumption readiness from actual authorized external publication and record its status honestly.

## Acceptance

- A separate consumer resolves and runs the documented public workflow with no example/test classes on its classpath.
- Normal and assembled artifacts have the intended classes and dependency metadata.
- Publication remains blocked until required external prerequisites and any release authorization are satisfied.

## Demo and validation

From the repository root:

    mvn clean install

Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Rerun transaction benchmarks only if packaging/runtime dependency changes affect their execution.
Store comparable samples under [performance](../../performance/Readme.md), linking the relevant record here.

## Risks

Local install is not Maven Central availability; repository credentials and namespace ownership may be external
blockers.

## Non-goals

Publishing credentials in documentation and treating this planning task as external publication authorization.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../templates/SprintReview.md). A planned charter is not a completed sprint.
