# Sprint 03: V1 Failure Recovery and Resource Hygiene

Status: planned. Dates/capacity: unassigned. Epic:
[Failure Recovery and Resource Hygiene](../Backlog.md#failure-recovery-and-resource-hygiene). Release: [V1](../V1.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

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

## Remaining work

- Specify state and permitted recovery after validation, callback, commit, and rollback failure on admitted paths.
- Distinguish library transaction cleanup from caller-owned object/storage changes; avoid implying automatic undo.
- Repair the three disabled validation-report tests and add direct failure/recovery assertions for the workflow.
- Verify normal/failed exits release transaction registry entries, event references, and selected before-state data.
- Make diagnostics actionable and link remedies; preserve the original failure if cleanup also fails as specified.

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
