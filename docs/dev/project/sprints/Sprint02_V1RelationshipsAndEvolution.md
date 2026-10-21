# Sprint 02: V1 Relationships and Evolution

Status: planned. Dates/capacity: unassigned. Epic: [V1-E02](../Backlog.md#v1-e02). Release: [V1](../V1.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Validate a representative relationship with basic All/Exists and reject an invalid before/after entity change.

## Dependencies and readiness

Review [Sprint 01](Sprint01_V1NightCityCoreWorkflow.md) for actual dependencies and evidence.
Allow independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral
and technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [QuantifiersAndIndexes][QI] and [design][QI-design].
- [EventsAndConstraints][EC] and [design][EC-design].

Record exact requirement IDs and acceptance evidence for the selected slice in the existing plan/traceability entry.

## Remaining work

- Specify a focused Night City relationship using caller-supplied in-memory collections.
- Cover a satisfied and missing relationship, empty collections, and the selected universal/existence combination.
- Specify a before/after transition or port rule, its valid/invalid changes, and null endpoint handling.
- Verify selected rules through transaction commit, rather than relying on quantifier-only unit tests.

## Acceptance

- The representative relationship and evolution cases pass end to end with direct requirement evidence.
- Failure output identifies the violated rule and relevant operation; resources are released as specified.
- Collection/matching work has stated cardinalities and a measured baseline.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure collection cardinalities and evolution checks, including duplicate-event behavior on the admitted path.
Use [performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn -Dtest=AllTest,ExistsTest test
    mvn -Dtest=UserCanUseMemoForBiEvents test

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.

## Non-goals

Full generic/subclass combinations, maintained joins/indexes, and additional event payload customization.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed IDs, actual commands/versions, passed/skipped totals, five quality obligations,
consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template][wrap-01].

[QI]: ../../specifications/QuantifiersAndIndexes.spec.md
[QI-design]: ../../design/QuantifiersAndIndexes.design.md
[EC]: ../../specifications/EventsAndConstraints.spec.md
[EC-design]: ../../design/EventsAndConstraints.design.md

[wrap-01]: ../templates/SprintReview.md
