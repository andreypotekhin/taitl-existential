# Sprint 03: V1 Evaluation, Intents, and Indexes

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Status: planned. Dates and capacity: not assigned. Epic: [V1-E03](../Backlog.md#v1-e03).
Release: [V1](../V1.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Evaluate applicable rules at documented stages while maintaining predictable intent checks and event indexes.

## Entry conditions and dependencies

[Sprint 02](Sprint02_V1ContextsAndRuleConfiguration.md) is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [TransactionsAndEvaluation specification](../../../../specifications/TransactionsAndEvaluation.spec.md) and
[design](../../../../design/TransactionsAndEvaluation.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Reconcile BEGIN/IMMEDIATE/COMMIT/CHECKPOINT/ROLLBACK/VALIDATION with migrated Precondition/Early terminology.
- Define event identity, stage deduplication, split-event ordering, and context/transaction overlay composition.
- Verify intent defaults and stage assignment, shared configuration indexes, runtime indexes, and V1 index capability.
- Repair and enable the three disabled EvaluationLogicTest cases for report collection and partial-report propagation.
- Link quantifier/index naming discrepancies and broader V2 cases without silently moving release scope.

## Acceptance

- Stage timing and repeated-event behavior match explicit positive and negative acceptance cases.
- The three report tests execute and pass; adjacent test success cannot replace their assertions.
- V1 index/intent claims distinguish supported primitives from V2 relationship/quantifier completion.

## Demo and validation

From the repository root:

    mvn -Dtest=UserCanConfigureEntities,UserCanConfigureIntents,EvaluationLogicTest,TransactionIndexesTest test

Inspect assertion results and demonstrate the relevant Night City-backed scenario where applicable.
Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Measure unique versus repeated events and increasing rule/event counts, isolating handler work from dispatch.
Store comparable samples under [performance](../../../../performance/Readme.md), linking the relevant record here.

## Risks

Changing deduplication or split order can alter handler counts and side effects.

## Non-goals

New custom event registration and the full V2 quantifier/violation-reporting surface.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../../../../project/templates/SprintReview.md). A planned charter is not a completed sprint.

[wrap-01]: ../../../../project/ReleaseRebaseline.md
