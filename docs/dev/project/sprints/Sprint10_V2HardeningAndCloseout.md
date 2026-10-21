# Sprint 10: V2 Hardening and Closeout

Status: planned. Dates/capacity: unassigned. Epic: [V2 Hardening and Closeout](../Backlog.md#v2-hardening-and-closeout).
Release: [V2](../V2.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Close broader standard usage while retaining the V1 workflow, consumption, and quality evidence.

## Dependencies and readiness

Review [Sprint 09](Sprint09_V2IndexesAndOperationalCoverage.md) for actual dependencies and evidence. Allow independent
investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and technical
dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration.spec.md] and [LibraryAndConfiguration.design.md].
- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].
- [QuantifiersAndIndexes.spec.md] and [QuantifiersAndIndexes.design.md].
- [IntentsAndEffects.spec.md] and [IntentsAndEffects.design.md].
- [Housekeeping.spec.md] and [Housekeeping.design.md].
- [Distribution.spec.md] and [Distribution.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Remaining work

- Run full regression, V2 combination/negative tests, and the independent Night City consumer.
- Verify all five quality requirements, actual coverage collection, runtime claims, and artifact contents.
- Review matching/index/lifecycle scaling and resource stress evidence.
- Reconcile V2 capabilities, remaining V3 custom-event decisions, actual publication status, and release evidence.

## Acceptance

- Admitted built-in combinations and retained V1 behavior meet documented acceptance and design obligations.
- No release blocker or required missing evidence remains.
- Publication statements match the chosen distribution channel and actual evidence.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Review comparable topic baselines and resource results; investigate material regressions. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs. The independent
consumer command is planned as `mvn -f examples/night_city/pom.xml verify` after library installation. It becomes
runnable when Sprint 04 creates Night City's POM and its executable acceptance demonstration. Run relevant
benchmark/resource scenarios and record retained-regression, artifact, and consumer evidence.

## Non-goals

All new features and silent extension of compatibility/distribution claims.

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
[IntentsAndEffects.spec.md]: ../../specifications/IntentsAndEffects.spec.md
[IntentsAndEffects.design.md]: ../../design/IntentsAndEffects.design.md
[Housekeeping.spec.md]: ../../specifications/Housekeeping.spec.md
[Housekeeping.design.md]: ../../design/Housekeeping.design.md
[Distribution.spec.md]: ../../specifications/Distribution.spec.md
[Distribution.design.md]: ../../design/Distribution.design.md

[review template]: ../templates/SprintReview.md
