# Sprint 13: V3 Hardening and Closeout

Status: planned. Dates/capacity: unassigned. Epic: [V3 Hardening and Closeout](../Backlog.md#v3-hardening-and-closeout).
Release: [V3](../V3.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Close extensibility with robust custom-event behavior, bounded costs, and retained standard-library evidence.

## Dependencies and readiness

Review [Sprint 12](Sprint12_V3CustomEventEvaluationAndExtensions.md) for actual dependencies and evidence. Allow
independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and
technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].
- [IntentsAndEffects.spec.md] and [IntentsAndEffects.design.md].
- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [Housekeeping.spec.md] and [Housekeeping.design.md].
- [Distribution.spec.md] and [Distribution.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Remaining work

- Run full regression, extension success/failure/resource cases, and the independent consumer.
- Verify all five design obligations and actual coverage evidence across retained and custom paths.
- Review custom-event scaling and baseline comparisons, diagnostics, artifacts, and current distribution statements.
- Close blockers, record exact evidence and explicit deferrals, and reconcile all release records.

## Acceptance

- Every admitted extension capability and retained V1/V2 behavior has direct acceptance evidence.
- Extension failures do not create unspecified transaction state or unbounded retained resources.
- No release claim closes on absent, disabled, or skipped required evidence.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Compare built-in/custom paths under matching workloads; investigate material overhead and resource regressions. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs. The independent
consumer command is planned as `mvn -f examples/night_city/pom.xml verify` after library installation. It becomes
runnable when Sprint 04 creates Night City's POM and its executable acceptance demonstration. Run relevant
benchmark/resource scenarios and record retained-regression, artifact, and consumer evidence.

## Non-goals

All new feature families and speculative optimizations.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed requirements, actual commands/versions, passed/skipped totals, five quality
obligations, consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template].

[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
[IntentsAndEffects.spec.md]: ../../specifications/IntentsAndEffects.spec.md
[IntentsAndEffects.design.md]: ../../design/IntentsAndEffects.design.md
[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[Housekeeping.spec.md]: ../../specifications/Housekeeping.spec.md
[Housekeeping.design.md]: ../../design/Housekeeping.design.md
[Distribution.spec.md]: ../../specifications/Distribution.spec.md
[Distribution.design.md]: ../../design/Distribution.design.md

[review template]: ../templates/SprintReview.md
