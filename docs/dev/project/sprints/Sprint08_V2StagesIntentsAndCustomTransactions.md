# Sprint 08: V2 Stages, Intents, and Transaction Customization

Status: planned. Dates/capacity: unassigned. Epic: [V2-E03](../Backlog.md#v2-e03). Release: [V2](../V2.md).
Design readiness: open for the remaining capability decisions. Verification: existing evidence is partial or absent;
this charter does not promote inherited completion marks.

## Goal and user outcome

Use built-in execution stages, intents/effects, and customized transactions with predictable timing and failures.

## Dependencies and readiness

Review [Sprint 07](Sprint07_V2ContextsAndTypeMatching.md) for actual dependencies and evidence.
Allow independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral
and technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [TransactionsAndEvaluation][TE] and [design][TE-design].
- [IntentsAndEffects][IE] and [design][IE-design].

Record exact requirement IDs and acceptance evidence for the selected slice in the existing plan/traceability entry.

## Remaining work

- Resolve migrated stage terminology against BEGIN/IMMEDIATE/COMMIT/CHECKPOINT/ROLLBACK/VALIDATION.
- Complete intent defaults/stage assignment and effect order across matching contexts and transaction overlays.
- Verify transaction factories, caller-supplied instances, lifecycle callbacks, and additional per-call values.
- Specify partial effects, authorization failure, recovery, cleanup, and thread/ownership restrictions.

## Acceptance

- Stage counts/order and intent/effect behavior match acceptance for the admitted combinations.
- Custom transactions preserve ordering and failure semantics without leaking state across invocations.
- Dispatch and callback costs are distinguished in comparable benchmarks.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure no-op dispatch separately from handler work and custom transaction preparation.
Use [performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn -Dtest=UserCanConfigureIntents,UserCanConfigureCustomTransactions test
    mvn -Dtest=UserCanHandleCombinedEventEffects test

Coding-task closure requires `mvn clean install` and inspection of actual test/coverage/check outputs.

## Non-goals

User-defined event registration and automatic compensation of arbitrary external side effects.

## Plan and review

No feature-delivery execution plan is finalized yet. Follow [Plans](../../auto/Plans.md) when the work is selected.
Review pending: record completed IDs, actual commands/versions, passed/skipped totals, five quality obligations,
consumer/benchmark evidence where relevant, blockers, and deferrals. Use the [review template][wrap-01].

[TE]: ../../specifications/TransactionsAndEvaluation.spec.md
[TE-design]: ../../design/TransactionsAndEvaluation.design.md
[IE]: ../../specifications/IntentsAndEffects.spec.md
[IE-design]: ../../design/IntentsAndEffects.design.md

[wrap-01]: ../templates/SprintReview.md
