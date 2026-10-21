# Sprint 04: V1 Night City Artifact Consumption and Performance

Status: planned. Dates/capacity: unassigned. Epic:
[Night City Artifact Consumption and Performance](../Backlog.md#night-city-artifact-consumption-and-performance).
Release: [V1](../V1.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Build and exercise Night City as a Maven project consuming the installed Existential artifact.

## Dependencies and readiness

Review [Sprint 03](Sprint03_V1FailureRecoveryAndResourceHygiene.md) for actual dependencies and evidence. Allow
independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and
technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [Distribution.spec.md] and [Distribution.design.md].
- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [QuantifiersAndIndexes.spec.md] and [QuantifiersAndIndexes.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Remaining work

- Give examples/night_city its own pom.xml with the library dependency, not a source/test classpath dependency.
- Build the library first, then verify the independent Night City project; retain shared source fixtures for root tests.
- Use public library APIs in the demonstrated workflow and remove incidental dependence on implementation annotations.
- Run the complete representative scenario, including success, violation, recovery, relationships, and evolution.
- Record artifact contents, dependency hygiene, transaction performance, and workload-size evidence.

## Acceptance

- Night City compiles/runs from the documented dependency without root source, test-classes, or target/classes inputs.
- Normal library artifacts exclude Night City, tests, and build-only dependencies.
- Representative workflow measurements and resource evidence are reproducible and reviewed.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure representative transaction throughput and scaling; record environment, samples, and material tradeoffs. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs. The independent
consumer command is planned as `mvn -f examples/night_city/pom.xml verify` after library installation. It becomes
runnable when Sprint 04 creates Night City's POM and its executable acceptance demonstration.

## Non-goals

Maven Central publication, a root reactor restructure, and new benchmark frameworks.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed requirements, actual commands/versions, passed/skipped totals, five quality
obligations, consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template].

[Distribution.spec.md]: ../../specifications/Distribution.spec.md
[Distribution.design.md]: ../../design/Distribution.design.md
[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[QuantifiersAndIndexes.spec.md]: ../../specifications/QuantifiersAndIndexes.spec.md
[QuantifiersAndIndexes.design.md]: ../../design/QuantifiersAndIndexes.design.md

[review template]: ../templates/SprintReview.md
