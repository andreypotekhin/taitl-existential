# Transactions and Evaluation Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Collect entity events during a business operation, evaluate at checkpoints or commit, and handle rollback explicitly.

The [specification](../specifications/TransactionsAndEvaluation.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

TransactionLogic constructs and registers transactions, runs lifecycle actions, and disposes successful completions.
Evaluation combines shared context handlers with prepared transaction rule overlays. The actual StageName values
are BEGIN, IMMEDIATE, COMMIT, CHECKPOINT, ROLLBACK, and VALIDATION; the migrated text still uses Early/Precondition.

- [transaction orchestration](../../../src/main/java/com/taitl/ex/logic/transactions/TransactionLogic.java)
- [stage evaluation](../../../src/main/java/com/taitl/ex/logic/evaluation/EvaluationLogic.java)
- [implemented stage names](../../../src/main/java/com/taitl/existential/constants/StageName.java)
- [transaction rule preparation][link-01]

## Approach and tradeoffs

Retain transaction-local prepared overlays to avoid repeated handler-list merging on dispatch. Keep library
transactions distinct from caller-owned persistence transactions; validation does not make side effects atomic.

These observations retain the existing architecture. They do not approve new public interfaces or resolve the open
choices below. Record future decisions here, update the normative specification, and reconcile the execution plan.

## Open decisions

- Reconcile Precondition/Early terminology with BEGIN and specify all lifecycle stage timing.
- Specify deduplication identity, checkpoint continuation/reset behavior, and nested transaction independence.
- Specify behavior after validation or lifecycle handler failure, including disposal and rollback failure.
- Three disabled EvaluationLogicTest methods leave report collection and partial-report propagation unverified.

## Evidence and acceptance work

- [lifecycle specifications](../../../src/test/java/com/taitl/specs/library_usage/UserCanInitiateTransactions.java)
- [custom transaction specifications](../../../src/test/java/com/taitl/specs/configuration_workflow/custom_transactions)
- [disabled report tests](../../../src/test/java/com/taitl/ex/logic/evaluation/EvaluationLogicTest.java)

V1-E01/E03 own core validation/recovery; V2-E03/E04 own broader stages, transactions, and checkpoint/nesting.
Add user-facing positive and negative acceptance cases for each unresolved behavior; passing adjacent unit tests do
not establish the full claim. Preserve inherited completion marks until a requirement-level review changes them.

## Required quality analysis

These obligations must be resolved for each selected capability before its design is ready. Record concrete decisions
and acceptance/evidence in this design and its specification, or justify inapplicability; this list does not itself
establish passing evidence. See [Process](../auto/Process.md#required-design-analysis).

- Correctness: specify lifecycle/stage timing, event identity/deduplication, and the accepted state transitions.
- Actionable errors: identify operation, stage, violated rule, and relevant original cause with a usable remedy.
- Failure recovery: define post-commit/validation/callback failure state, follow-up actions, and dual-failure handling.
- Resource hygiene: prove registry, event, memo, report, and overlay lifetimes across normal and failed exits.
- Performance: measure begin/event/commit/cleanup separately where useful, with rule/event counts and scaling.

## Performance

The existing CustomTransactionBenchmark compares static-context, factory-transaction, and caller-supplied rules.
Use the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.

[link-01]: ../../../src/main/java/com/taitl/ex/logic/stages/validation/actions/PrepareTransaction.java
