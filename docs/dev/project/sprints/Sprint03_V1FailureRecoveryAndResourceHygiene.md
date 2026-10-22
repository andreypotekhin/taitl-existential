# Sprint 03: V1 Failure Recovery and Resource Hygiene

Status: complete. Dates/capacity: unassigned. Epic:
[Failure Recovery and Resource Hygiene](../Backlog.md#failure-recovery-and-resource-hygiene). Release: [V1](../V1.md).
Design readiness: selected failure and shutdown contracts are settled. Verification: complete for the selected V1 scope.

## Goal and user outcome

Recover predictably from rejected or failed operations and release the representative workflow resources.

## Dependencies and readiness

Review [Sprint 02](Sprint02_V1RelationshipsAndEvolution.md) for actual dependencies and evidence. Allow independent
investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and technical
dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].
- [Housekeeping.spec.md] and [Housekeeping.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

Completed execution plan: [P10212603](../../past/planning/P10212603.Sprint03-failure-recovery-and-shutdown.plan.md).

## Completed scope

Failed begin, rejected commit, checkpoint retry, rollback failure aggregation, transaction state release, shutdown
timeout/interruption/retry, callback-close rejection, and concurrent draining have direct acceptance evidence. The
three report tests are active and pass. Runtime-index, memo, and prepared-overlay cleanup have focused retained-state
tests. Troubleshooting links identify the recovery actions.

Sprint 02 already corrected deterministic runtime event/type index clearing on successful completion and explicit
rollback, including retained closed handles, and added operation-aware validation/endpoint diagnostics. Extend that
evidence rather than treating it as a complete library-close guarantee. Specify remaining memo/prepared-overlay
release and cleanup retry/idempotence on failure. Broader cache bounds/concurrency and observable leak reporting
remain in their V2/global owners; neither timings nor GC expectations establish those guarantees.

## Acceptance

- Success, violation, failure recovery, explicit rollback, and close paths have reproducible expected outcomes.
- The three report tests execute and pass, and repeated failed operations do not accumulate transaction state.
- Required diagnostic context and recovery guidance match the documented workflow.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure failure/cleanup paths separately and inspect retained-state counts; timing cannot prove resource safety. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Final root build from the repository root:

    mvn clean install

Observed: 454 tests, 0 failures/errors/skips; Checkstyle and PMD pass; JaCoCo analyzes 234 classes and passes the
configured coverage checks. Rejected-commit rollback and fresh-transaction recovery timing are recorded with the
[Night City workflow samples](../../performance/runs/20261022T183337Z.NightCityConsumerWorkflows.md).

## Non-goals

A global leak dashboard and arbitrary effect compensation.

## Review

Completed for the selected V1 failure-recovery and shutdown behavior. Broader global cache bounds, GC/leak detection,
and a leak dashboard remain outside this sprint.

[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
[Housekeeping.spec.md]: ../../specifications/Housekeeping.spec.md
[Housekeeping.design.md]: ../../design/Housekeeping.design.md

[review template]: ../templates/SprintReview.md
