# Sprint 05: V1 Hardening and Closeout

Status: complete (2026-10-22). Dates/capacity: unassigned. Epic: [V1 Hardening and Closeout](../Backlog.md#v1-hardening-and-closeout).
Release: [V1](../V1.md).
Design readiness: complete for the admitted V1 scope. Verification: exit criteria passed; Java 11 runtime compatibility
and remote publication remain unclaimed.

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

## Completed scope

- Complete the full regression suite and repair retry after a context factory fails during configuration finalization.
- Verify all five design obligations against current acceptance, resource, artifact, and performance evidence.
- Build and install the stable local 0.0.1 library; independently verify Night City and the three-project reactor.
- Reconcile release requirements, traceability, usage docs, changelog, milestone, and retrospective.

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

The local release and consumer commands are:

    mvn clean install
    mvn -f examples/night_city/pom.xml clean verify exec:java
    mvn -f reactor/pom.xml clean install
    mvn -f examples/night_city/pom.xml org.apache.maven.plugins:maven-dependency-plugin:3.3.0:tree

Three independent consumer workflow benchmark runs use 50 warmups and 250 measured iterations; see the [V1 closeout
record](../../performance/runs/20261022T205600Z.NightCityV1Closeout.md). Both library artifacts and the dependency tree
were inspected after installation.

## Non-goals

All new feature scope and opportunistic API expansion.

## Plan and review

Completed execution plan: [P10222605](../../past/planning/P10222605.V1-hardening-and-closeout.plan.md).

Review complete. On Amazon Corretto 17.0.15, Maven 3.9.10, macOS 15.6.1 arm64, `mvn clean install` passes 455 tests
with zero failures/errors/skips and passes Checkstyle, PMD, and JaCoCo checks. Standalone consumer verification passes
1 test and prints all three expected outcomes. The reactor builds and installs the library, consumer, and aggregator.
Normal and assembled library JARs omit Night City and test libraries; JUnit remains test-scoped in the consumer.
Configuration-finalization retry has direct acceptance coverage. No remote publication or Java 11 runtime claim is made.

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
