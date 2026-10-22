# Sprint 03: V1 Failure Recovery and Resource Hygiene

Status: in progress. Dates/capacity: unassigned. Epic:
[Failure Recovery and Resource Hygiene](../Backlog.md#failure-recovery-and-resource-hygiene). Release: [V1](../V1.md).
Design readiness: selected failure and shutdown contracts are settled. Verification: partial; main sources compile,
but focused tests, complete build evidence, and performance records remain outstanding.

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

Current execution plan: [P10212603](../../planning/P10212603.Sprint03-failure-recovery-and-shutdown.plan.md).

## Remaining work

- Add focused tests for state and permitted recovery after validation, callback, commit, rollback, and begin failures.
- Verify library transaction cleanup independently from caller-owned object/storage changes and side effects.
- Repair the three disabled validation-report tests and add direct failure/recovery assertions for the workflow.
- Verify shutdown timeout, interruption, retry, concurrent close, callback rejection, and direct transaction close.
- Verify normal/failed exits release transaction registry entries, event references, memo data, and prepared overlays.
- Make diagnostics actionable and link remedies; preserve the original failure if cleanup also fails as specified.

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

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn -Dtest=UserCanInitiateTransactions,EvaluationLogicTest test
    mvn -Dtest=TrRegistryTest test

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.

## Non-goals

A global leak dashboard and arbitrary effect compensation.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed requirements, actual commands/versions, passed/skipped totals, five quality
obligations, consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template].

[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
[Housekeeping.spec.md]: ../../specifications/Housekeeping.spec.md
[Housekeeping.design.md]: ../../design/Housekeeping.design.md

[review template]: ../templates/SprintReview.md
