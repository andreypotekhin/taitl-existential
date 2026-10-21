# Transactions and Evaluation Design

The [specification](../specifications/TransactionsAndEvaluation.spec.md) defines the required behavior. This document
records the transaction ownership and evaluation design. Release evidence and delivery stages belong in the project
records and execution plans.

## User outcome

Callers collect entity events during a business operation, validate those events at checkpoints or commit, and explicitly
run rollback behavior when they abandon a failed operation.

## Architecture

`TransactionLogic` creates a transaction handle, dispatches lifecycle actions, and releases transaction state.
`EvaluationLogic` combines context handlers with transaction-local prepared rules. The transaction handle contains
reported events, memo values, prepared indexes, and per-stage data. Validation does not make application side effects
atomic and does not undo caller-owned entity changes.

The implementation uses the stage names BEGIN, IMMEDIATE, COMMIT, CHECKPOINT, ROLLBACK, and VALIDATION. The historical
Precondition/Early and Middle terminology remains in parts of the public specification; the requirement text is the
normative description of those behaviors.

## Lifecycle ownership

`begin` returns a `Tr`. Callers use that handle for commit, checkpoint, and rollback. Event, access, and memo APIs may
continue to use the handle's string ID so application helpers can report events without owning lifecycle control.
Operation keys remain strings.

Successful commit and explicit rollback close the handle and remove its active registry entry. If commit fails, the
library removes the entry so string-ID event lookup cannot continue. The caller-held handle remains available for an
explicit rollback; rollback callbacks run only when that rollback is called. If the caller abandons the handle, the
registry no longer retains it, so the handle and its transaction-local data can be reclaimed with the handle. No
secondary registry retains failed transactions.

Each execution of an `Op`-annotated method owns the `Tr` returned for that invocation in a local variable. Normal return
commits it and preserves the method result. A method or commit failure attempts explicit rollback on that same handle,
then rethrows the original throwable; a rollback failure is attached as suppressed. The aspect does not share mutable
transaction state between invocations. AspectJ weaving must be enabled for the advice to run.

Validation reports are created for each validation attempt, so violations from a failed attempt do not contaminate a
later attempt. A single `Tr` remains caller-owned and is not safe for concurrent mutation.

## Performance

Prepared transaction overlays avoid repeatedly merging handler lists during dispatch. The transaction-handle change
adds no lookup structure: the active registry remains necessary for string-ID event reporting. The benchmark guide and
measured records live under [performance](../performance/Readme.md).
