# Sprint 01: V1 Night City Core Workflow

Status: complete. Dates/capacity: unassigned. Epic: [Night City Core Workflow](../Backlog.md#night-city-core-workflow).
Release: [V1](../V1.md).
Design readiness: selected transaction, event, recovery, and annotated-operation behavior is recorded in the governing
design/specification. Verification: selected acceptance and required build checks passed; broader V1 closeout remains.

## Goal and user outcome

Follow a documented Night City cat creation and relocation operation from rule configuration through events to
successful or rejected commit. Keep lifecycle control on Tr while retaining string IDs for event helpers. Rejected
commits detach from event lookup and remain explicitly rollbackable through the caller-held handle. Annotated operation
executions each own their transaction handle.

## Dependencies and readiness

The planning foundation and selected slice decisions are available. Implementation follows
[the Sprint 01 execution plan](../../planning/P10202603.Transaction-handles-and-Op.plan.md). Broader stage behavior
remains assigned to later work.

## Governing designs and specifications

- [LibraryAndConfiguration.spec.md] and [LibraryAndConfiguration.design.md].
- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Remaining work

- Complete the executable cat creation and relocation walkthrough at one operation context, including a successful
  create, black-at-birth rejection, missing-dwelling rejection, allowed relocation, and color-change rejection.
- Cover explicit rollback after failed commit, event-ID detachment, report isolation, and concurrent `@Op` execution.
- Restore JaCoCo collection, run the full installation checks, inspect disabled tests, and record comparable performance
  samples.

## Acceptance

- An explicit walkthrough maps behavior to named requirement sections and executable acceptance cases.
- Valid and invalid operations demonstrate validation at commit and explicit failure recovery.
- Coverage/check output and comparable performance samples are recorded before closeout.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure the selected begin/event/commit path and state workload/cardinality; retain comparable run records. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn -Dtest=UserCanSendEntityEvents,UserCanConfigureEntities test

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.

## Non-goals

Independent-instance isolation, wildcard precedence, custom events, and general stage/effect customization.

## Plan and review

The [execution plan](../../planning/P10202603.Transaction-handles-and-Op.plan.md) records implementation progress and
verification commands. Review completed 2026-10-21 at base revision `735e465001f165582b81bd2b308f0f05ac915b6d` with a
dirty working tree. `mvn clean install` passed on Amazon Corretto 17.0.15 and Maven 3.9.10: 395 tests, 0
failures/errors, and 3 disabled report tests skipped; Checkstyle reported 0 violations; JaCoCo generated execution
data for 231 classes and passed its configured check; PMD, Failsafe verification, and local installation completed.
The library JAR contains no Night City or specification-test classes. AspectJ output confirmed both annotated
acceptance methods were advised.

Correctness is covered by direct success/rejection and overlapping-operation tests. Failed commits vacate string-ID
lookup, allow an explicit rollback, and isolate later validation reports. Rollback always disposes transaction-local
state even when a callback fails; rollback callbacks run only on explicit rollback. The dated transaction benchmark
record contains three comparable samples. Local installation does not complete V1's independent Night City consumer
sprint. The three disabled evaluation-report tests remain follow-up work for Sprint 03.

[review template]: ../templates/SprintReview.md
[performance record]: ../../performance/runs/20261021T150123-0700.TransactionHandles.md

[LibraryAndConfiguration.spec.md]: ../../specifications/LibraryAndConfiguration.spec.md
[LibraryAndConfiguration.design.md]: ../../design/LibraryAndConfiguration.design.md
[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
