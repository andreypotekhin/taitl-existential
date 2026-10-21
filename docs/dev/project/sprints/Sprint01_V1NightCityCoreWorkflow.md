# Sprint 01: V1 Night City Core Workflow

Status: planned. Dates/capacity: unassigned. Epic: [V1-E01](../Backlog.md#v1-e01). Release: [V1](../V1.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Follow a documented Night City operation from rule configuration through events to successful or rejected commit.

## Dependencies and readiness

The planning foundation is available; no feature sprint is complete.
Allow independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral
and technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration][LC] and [design][LC-design].
- [TransactionsAndEvaluation][TE] and [design][TE-design].
- [EventsAndConstraints][EC] and [design][EC-design].

Record exact requirement IDs and acceptance evidence for the selected slice in the existing plan/traceability entry.

## Remaining work

- Write the cross-topic design and normative acceptance cases for the complete representative workflow first.
- Use one exact operation context, ordinary programmatic rules, one transaction owner, and validation at commit.
- Define success, a described rule violation, recovery/cleanup, an All/Exists relationship, and an evolution rule.
- Deliver the basic success/violation slice first; identify dependencies for the later V1 relationship/recovery slices.
- Restore actual JaCoCo collection and establish a repeatable correctness/performance evidence baseline.

## Acceptance

- An explicit walkthrough maps proposed behavior to requirement IDs and executable acceptance cases.
- Basic valid and invalid operations demonstrate the promised validation timing and useful error context.
- Unresolved decisions affect only their dependent slices; no claim is promoted using the old completion marks.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure the selected begin/event/commit path and state workload/cardinality; retain comparable run records.
Use [performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn -Dtest=UserCanSendEntityEvents,UserCanConfigureEntities test

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.

## Non-goals

Independent-instance isolation, wildcard precedence, custom events, and general stage/effect customization.

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

[wrap-01]: ../templates/SprintReview.md
