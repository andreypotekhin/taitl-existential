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

V1-E03 and V1-E04; Sprint 05 must account for every disabled validation case before release.
Add user-facing positive and negative acceptance cases for each unresolved behavior; passing adjacent unit tests do
not establish the full claim. Preserve inherited completion marks until a requirement-level review changes them.

## Performance

The existing CustomTransactionBenchmark compares static-context, factory-transaction, and caller-supplied rules.
Use the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.

[link-01]: ../../../src/main/java/com/taitl/ex/logic/stages/validation/actions/PrepareTransaction.java
