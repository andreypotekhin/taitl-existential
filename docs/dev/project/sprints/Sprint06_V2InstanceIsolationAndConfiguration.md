# Sprint 06: V2 Instance Isolation and Configuration

Status: planned. Dates/capacity: unassigned. Epic: [V2-E01](../Backlog.md#v2-e01). Release: [V2](../V2.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Use independent instances and the broader documented configuration options without unintended shared state.

## Dependencies and readiness

V1 meets its release exit criteria before this release delivers new claims.
Allow independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral
and technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration][LC] and [design][LC-design].

Record exact requirement IDs and acceptance evidence for the selected slice in the existing plan/traceability entry.

## Remaining work

- Specify isolation boundaries around instance configuration, flags, rules, events, and transactions.
- Distinguish explicit global facade/injection facilities from per-instance state; test conflicting instances.
- Specify configuration sources/precedence, freezing, flags, invalid input, and supported concurrency assumptions.
- Separate tested runtime compatibility from compiler release; retain the V1 consumer and workflow regressions.

## Acceptance

- Independent instances, sources/flags, freeze behavior, and relevant concurrency cases have direct evidence.
- Errors and cleanup are defined for failed initialization/configuration.
- Configuration/setup overhead and transaction effects are measured separately.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure setup and instance contention where admitted, without adding avoidable transaction-path work.
Use [performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn -Dtest=UserCanAccessLibrary,UserCanConfigureLibrary test

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.

## Non-goals

Hot reload, new configuration strategy APIs, and untested runtime support promises.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed IDs, actual commands/versions, passed/skipped totals, five quality obligations,
consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template][wrap-01].

[LC]: ../../specifications/LibraryAndConfiguration.spec.md
[LC-design]: ../../design/LibraryAndConfiguration.design.md

[wrap-01]: ../templates/SprintReview.md
