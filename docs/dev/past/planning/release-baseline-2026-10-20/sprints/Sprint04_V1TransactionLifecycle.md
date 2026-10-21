# Sprint 04: V1 Transaction Lifecycle

Superseded before implementation on 2026-10-20; this is not completed release work. See [rebaseline][wrap-01].

Status: planned. Dates and capacity: not assigned. Epic: [V1-E04](../Backlog.md#v1-e04).
Release: [V1](../V1.md). No implementation completion is implied by this charter.

## Sprint goal and user-facing outcome

Begin, checkpoint, commit, and roll back independent transactions with defined invalid-operation behavior.

## Entry conditions and dependencies

[Sprint 03](Sprint03_V1EvaluationIntentsAndIndexes.md) is complete with its evidence and unresolved blockers reviewed.
Use the [agent process](../../../../auto/Process.md) to resolve design/specification decisions before implementing them.

## Specification and design

Primary: [TransactionsAndEvaluation specification](../../../../specifications/TransactionsAndEvaluation.spec.md) and
[design](../../../../design/TransactionsAndEvaluation.design.md). The
[traceability matrix](../TraceabilityMatrix.md) supplies the other topic requirements assigned to this epic.
Record exact requirement identifiers in the delivery plan and tests as work is admitted.

## Remaining work

- Specify checkpoint continuation/reset behavior and nested transaction independence.
- Define commit/validation/rollback failure behavior and the valid follow-up actions for each case.
- Exercise custom transaction lifecycle handlers and before-state memo ownership across supported paths.
- Verify basic disposal required by existing V1 behavior; leave comprehensive caching/leak detection in V2.

## Acceptance

- Lifecycle success and failure sequences have expected outputs, state transitions, and regression tests.
- Events outside the valid lifecycle fail consistently; checkpoint does not acquire accidental close semantics.
- Independent/nested operations and caller-supplied transactions do not mix events or prepared rule state.

## Demo and validation

From the repository root:

    mvn -Dtest=UserCanInitiateTransactions,UserCanUseMemoForBiEvents,UserCanConfigureCustomTransactions test

Inspect assertion results and demonstrate the relevant Night City-backed scenario where applicable.
Before completion run `mvn clean install`; record actual executed/skipped totals and inspection of required checks.

## Performance

Measure transaction setup, commit, checkpoint, and rollback with workload sizes and comparable environments.
Store comparable samples under [performance](../../../../performance/Readme.md), linking the relevant record here.

## Risks

Validation failure can bypass ordinary disposal; specify caller recovery before changing resource ownership.

## Non-goals

New lifecycle dashboard/helper APIs and comprehensive V2 global leak reporting.

## Execution plan and review

No feature-delivery execution plan is finalized yet. Create one under `docs/dev/planning/` when the work is selected;
use [Plans.md](../../../../auto/Plans.md). Update this charter with its link and any admitted scope changes.

Review pending. Record completed requirement IDs, exact commands, tool/runtime versions, executed/passed/skipped totals,
demo output, performance record links, remaining blockers, and explicit deferrals. Use the
[review template](../../../../project/templates/SprintReview.md). A planned charter is not a completed sprint.

[wrap-01]: ../../../../project/ReleaseRebaseline.md
