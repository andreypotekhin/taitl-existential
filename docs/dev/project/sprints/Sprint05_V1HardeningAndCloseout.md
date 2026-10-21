# Sprint 05: V1 Hardening and Closeout

Status: planned. Dates/capacity: unassigned. Epic: [V1-E05](../Backlog.md#v1-e05). Release: [V1](../V1.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Close V1 with a robust, documented, independently consumed representative workflow and performance evidence.

## Dependencies and readiness

Review [Sprint 04](Sprint04_V1NightCityArtifactAndPerformance.md) for actual dependencies and evidence.
Allow independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral
and technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration][LC] and [design][LC-design].
- [TransactionsAndEvaluation][TE] and [design][TE-design].
- [EventsAndConstraints][EC] and [design][EC-design].
- [QuantifiersAndIndexes][QI] and [design][QI-design].
- [Housekeeping][HK] and [design][HK-design].
- [Distribution][DIST] and [design][DIST-design].

Record exact requirement IDs and acceptance evidence for the selected slice in the existing plan/traceability entry.

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
Recheck workload/complexity assumptions and baseline comparisons; explain material changes before closure.
Use [performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation


Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.
The independent consumer command is planned as `mvn -f examples/night_city/pom.xml verify` after library installation.
It becomes runnable when Sprint 04 creates Night City's POM and its executable acceptance demonstration.
Run relevant benchmark/resource scenarios and record retained-regression, artifact, and consumer evidence.

## Non-goals

All new feature scope and opportunistic API expansion.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed IDs, actual commands/versions, passed/skipped totals, five quality obligations,
consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template][wrap-01].

[LC]: ../../specifications/LibraryAndConfiguration.spec.md
[LC-design]: ../../design/LibraryAndConfiguration.design.md
[TE]: ../../specifications/TransactionsAndEvaluation.spec.md
[TE-design]: ../../design/TransactionsAndEvaluation.design.md
[EC]: ../../specifications/EventsAndConstraints.spec.md
[EC-design]: ../../design/EventsAndConstraints.design.md
[QI]: ../../specifications/QuantifiersAndIndexes.spec.md
[QI-design]: ../../design/QuantifiersAndIndexes.design.md
[HK]: ../../specifications/Housekeeping.spec.md
[HK-design]: ../../design/Housekeeping.design.md
[DIST]: ../../specifications/Distribution.spec.md
[DIST-design]: ../../design/Distribution.design.md

[wrap-01]: ../templates/SprintReview.md
