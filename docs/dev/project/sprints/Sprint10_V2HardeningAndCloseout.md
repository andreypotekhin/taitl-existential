# Sprint 10: V2 Hardening and Closeout

Status: planned. Dates/capacity: unassigned. Epic: [V2-E05](../Backlog.md#v2-e05). Release: [V2](../V2.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Close broader standard usage while retaining the V1 workflow, consumption, and quality evidence.

## Dependencies and readiness

Review [Sprint 09](Sprint09_V2IndexesAndOperationalCoverage.md) for actual dependencies and evidence.
Allow independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral
and technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration][LC] and [design][LC-design].
- [TransactionsAndEvaluation][TE] and [design][TE-design].
- [EventsAndConstraints][EC] and [design][EC-design].
- [QuantifiersAndIndexes][QI] and [design][QI-design].
- [IntentsAndEffects][IE] and [design][IE-design].
- [Housekeeping][HK] and [design][HK-design].
- [Distribution][DIST] and [design][DIST-design].

Record exact requirement IDs and acceptance evidence for the selected slice in the existing plan/traceability entry.

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
Review comparable topic baselines and resource results; investigate material regressions.
Use [performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation


Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.
The independent consumer command is planned as `mvn -f examples/night_city/pom.xml verify` after library installation.
It becomes runnable when Sprint 04 creates Night City's POM and its executable acceptance demonstration.
Run relevant benchmark/resource scenarios and record retained-regression, artifact, and consumer evidence.

## Non-goals

All new features and silent extension of compatibility/distribution claims.

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
[IE]: ../../specifications/IntentsAndEffects.spec.md
[IE-design]: ../../design/IntentsAndEffects.design.md
[HK]: ../../specifications/Housekeeping.spec.md
[HK-design]: ../../design/Housekeeping.design.md
[DIST]: ../../specifications/Distribution.spec.md
[DIST-design]: ../../design/Distribution.design.md

[wrap-01]: ../templates/SprintReview.md
