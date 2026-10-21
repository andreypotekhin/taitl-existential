# Sprint 09: V2 Indexes and Operational Coverage

Status: planned. Dates/capacity: unassigned. Epic:
[Indexes and Operational Coverage](../Backlog.md#indexes-and-operational-coverage). Release: [V2](../V2.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Use maintained relationships and broader lifecycle/reporting/resource behavior with measured scaling.

## Dependencies and readiness

Review [Sprint 08](Sprint08_V2StagesIntentsAndCustomTransactions.md) for actual dependencies and evidence. Allow
independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and
technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [QuantifiersAndIndexes.spec.md] and [QuantifiersAndIndexes.design.md].
- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].
- [Housekeeping.spec.md] and [Housekeeping.design.md].
- [Distribution.spec.md] and [Distribution.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Remaining work

- Reconcile public index names and complete map/set/join maintenance and composed quantifier cases.
- Specify checkpoint continuation/reset, access events, and broader entity/event combinations.
- Complete richer violation metadata, description options, cache ownership/invalidation, and global close behavior.
- Define and verify admitted leak reporting; avoid relying on deterministic garbage collection.
- Record optional remote-publication readiness separately; remote availability is claimed only after actual evidence.

## Acceptance

- Maintained relationships, lifecycle variations, and metadata have direct positive/failure evidence.
- Cache/global resources have defined ownership and demonstrated release/retention behavior.
- Performance includes maintenance and cardinality scaling, rather than lookup-only timing.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure maintenance, matching, callback, and retained-state costs under the broader workloads. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn -Dtest=SingleIndexTest,MultiIndexTest test
    mvn -Dtest=SingleJoinTest,MultiJoinTest test

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.

## Non-goals

Unapproved dashboard, hot-reload, or specialized optimization features; actual remote publication without authorization.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed requirements, actual commands/versions, passed/skipped totals, five quality
obligations, consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template].

[QuantifiersAndIndexes.spec.md]: ../../specifications/QuantifiersAndIndexes.spec.md
[QuantifiersAndIndexes.design.md]: ../../design/QuantifiersAndIndexes.design.md
[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
[Housekeeping.spec.md]: ../../specifications/Housekeeping.spec.md
[Housekeeping.design.md]: ../../design/Housekeeping.design.md
[Distribution.spec.md]: ../../specifications/Distribution.spec.md
[Distribution.design.md]: ../../design/Distribution.design.md

[review template]: ../templates/SprintReview.md
