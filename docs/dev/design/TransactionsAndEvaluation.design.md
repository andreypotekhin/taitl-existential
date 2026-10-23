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

## Failure and shutdown behavior

Commit callbacks retain their existing position before validation because they are part of the commit attempt and can
prepare required state. A failed commit removes active ID lookup; the caller may roll back its handle explicitly.
Checkpoint failures leave the handle usable. Rollback runs lifecycle callbacks before rollback-stage handlers, and
each phase continues after callback failures. The first thrown failure remains primary and later failures are
suppressed. Disposal runs regardless of callback outcome.

Library shutdown uses an immutable `ShutdownPolicy` that currently configures a graceful wait timeout. Transaction
logic atomically stops new begins, counts active handles and in-flight transaction work, and waits without holding
locks across callbacks. Existing calls can finish during the drain. A timeout or interruption leaves shutdown
resumable; resources are not force-cleared. Direct transaction close deregisters the handle without invoking
callbacks. Close from a callback fails immediately because waiting there would wait on itself. Failed-commit handles
are detached and do not delay shutdown; if shutdown completes before their explicit rollback, local state can be
disposed but rollback callbacks can no longer run.

The existing properties loader accepts `shutdown.timeoutMillis`; classpath defaults are overridden by the selected
environment file, and a later call to `Existential.shutdownPolicy` or `Ex.shutdownPolicy` takes precedence. This keeps
shutdown configuration at the library boundary rather than mixing it into operation rule builders.

## Performance

The [runtime execution-plan design](ExecutionPlans.design.md) compiles stage/selection requirements and specializes
validation-only reporting while retaining lifecycle windows and transaction-local overlays. `ExecutionPlanLogic`
coordinates `CompileExecutionPlan` and transaction binding; lifecycle actions retain their existing timing. Static
configuration alone cannot determine a supplied transaction's report-time requirements. Reporting from a begin
callback stays conservative until the effective plan is prepared. Empty entity stages can skip traversal without
skipping lifecycle callbacks, admission accounting, or disposal.

Prepared transaction overlays avoid repeatedly merging handler lists during dispatch. The transaction-handle change
adds no lookup structure: the active registry remains necessary for string-ID event reporting. The benchmark guide and
measured records live under [performance](../performance/Readme.md).
