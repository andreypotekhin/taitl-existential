# Sprint 05: V1 Hardening and Closeout

Status: planned. Dates/capacity: unassigned. Epic: [V1 Hardening and Closeout](../Backlog.md#v1-hardening-and-closeout).
Release: [V1](../V1.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Close V1 with a robust, documented, independently consumed representative workflow and performance evidence.

## Dependencies and readiness

Review [Sprint 04](Sprint04_V1NightCityArtifactAndPerformance.md) for actual dependencies and evidence. Allow
independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and
technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration.spec.md] and [LibraryAndConfiguration.design.md].
- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].
- [QuantifiersAndIndexes.spec.md] and [QuantifiersAndIndexes.design.md].
- [Housekeeping.spec.md] and [Housekeeping.design.md].
- [Distribution.spec.md] and [Distribution.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Remaining work

- Run complete regression and representative acceptance suites; admit no new features.
- Verify correctness, actionable errors, failure recovery, resource hygiene, and performance against the designs.
- Check that coverage data/checks execute, required tests are enabled, and both library artifact contents are correct.
- Rebuild the independent Night City consumer and collect current comparable benchmark/resource evidence.
- Reconcile V1 requirements, docs, traceability, blockers, deferrals, and the release retrospective.

## Acceptance

- Every V1 capability meets its specified acceptance and five quality obligations with current evidence.
- No required disabled/skipped check is represented as passing; no release blocker remains.
- Local dependency consumption and V1 evidence are reproducible without remote publication.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Recheck workload/complexity assumptions and baseline comparisons; explain material changes before closure. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs. The independent
consumer command is planned as `mvn -f examples/night_city/pom.xml verify` after library installation. It becomes
runnable when Sprint 04 creates Night City's POM and its executable acceptance demonstration. Run relevant
benchmark/resource scenarios and record retained-regression, artifact, and consumer evidence.

## Non-goals

All new feature scope and opportunistic API expansion.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed requirements, actual commands/versions, passed/skipped totals, five quality
obligations, consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template].

[LibraryAndConfiguration.spec.md]: ../../specifications/LibraryAndConfiguration.spec.md
[LibraryAndConfiguration.design.md]: ../../design/LibraryAndConfiguration.design.md
[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
[QuantifiersAndIndexes.spec.md]: ../../specifications/QuantifiersAndIndexes.spec.md
[QuantifiersAndIndexes.design.md]: ../../design/QuantifiersAndIndexes.design.md
[Housekeeping.spec.md]: ../../specifications/Housekeeping.spec.md
[Housekeeping.design.md]: ../../design/Housekeeping.design.md
[Distribution.spec.md]: ../../specifications/Distribution.spec.md
[Distribution.design.md]: ../../design/Distribution.design.md

[review template]: ../templates/SprintReview.md
