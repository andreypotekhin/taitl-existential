# Sprint 04: V1 Night City Artifact Consumption and Performance

Status: complete. Dates/capacity: unassigned. Epic:
[Night City Artifact Consumption and Performance](../Backlog.md#night-city-artifact-consumption-and-performance).
Release: [V1](../V1.md).
Design readiness: ready for the selected local consumer/reactor scope. Verification: complete for this sprint's
acceptance.

## Goal and user outcome

Build and exercise Night City as a Maven project consuming the installed Existential artifact.

## Dependencies and readiness

Review [Sprint 03](Sprint03_V1FailureRecoveryAndResourceHygiene.md) for the transaction recovery and shutdown behavior
used by the story. The consumer/reactor work can be verified independently of remaining V1 closeout items.

## Governing designs and specifications

- [Distribution.spec.md] and [Distribution.design.md].
- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [QuantifiersAndIndexes.spec.md] and [QuantifiersAndIndexes.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Completed implementation

The standalone POM, separate reactor POM, runnable story, and consumer acceptance test are present. The story uses the
existing cat configuration, reports an expected rule rejection with explicit rollback, makes a fresh successful
attempt, and relocates a cat under dwelling and color-preservation rules. Night City remains a shared root test fixture;
application wiring no longer depends on incidental internal logic annotations.

## Acceptance

- Night City compiles/runs from the documented dependency without root source, test-classes, or target/classes inputs.
- Normal library artifacts exclude Night City, tests, and build-only dependencies.
- The independent consumer acceptance test executes, and the separate reactor runs in library-before-consumer order.
- Representative workflow measurements and resource evidence are reproducible and reviewed.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure representative transaction throughput and scaling; record environment, samples, and material tradeoffs. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Execution plan and validation

Current execution plan: [P10222604](../../planning/P10222604.Sprint04-runnable-night-city-and-maven-reactor.plan.md).

Observed initial evidence: root `mvn clean install` passes 445 tests with JaCoCo checks; standalone consumer
`mvn -f examples/night_city/pom.xml clean verify` executes its story test; `mvn -f reactor/pom.xml clean install`
builds all three projects in order. Full final builds and artifact/dependency review remain after the last changes.

## Non-goals

Maven Central publication, changing the root library into an aggregator/parent, and new benchmark frameworks.

## Review

Completed. The independent consumer uses only the installed library artifact and Night City sources. `mvn clean install`
passes 454 tests, Checkstyle, PMD, and JaCoCo checks. Standalone consumer verification runs 1 acceptance test, the story
prints all expected outcomes, and the reactor builds/installs all 3 projects in dependency order. Normal and assembled
library JARs exclude example/test classes and test libraries; the consumer dependency tree keeps JUnit in test scope.
Three independent success/rejection/recovery/repeated-event performance samples are recorded. No numeric threshold was
introduced. Broader runtime support and external publication remain deferred to their own release decisions.

[Distribution.spec.md]: ../../specifications/Distribution.spec.md
[Distribution.design.md]: ../../design/Distribution.design.md
[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[QuantifiersAndIndexes.spec.md]: ../../specifications/QuantifiersAndIndexes.spec.md
[QuantifiersAndIndexes.design.md]: ../../design/QuantifiersAndIndexes.design.md

[review template]: ../templates/SprintReview.md
