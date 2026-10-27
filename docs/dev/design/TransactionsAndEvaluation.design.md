# Transactions and Evaluation Design

The [specification](../specifications/TransactionsAndEvaluation.spec.md) defines the required behavior. This document
records the transaction ownership and evaluation design. Release evidence and delivery stages belong in the project
records and execution plans.

## User outcome

Callers collect entity events during a business operation, validate them at checkpoints or commit, and explicitly run
rollback behavior when they abandon a failed operation.

## Architecture

`TransactionLogic` creates a transaction handle, dispatches lifecycle actions, and releases transaction state.
`EvaluationLogic` combines context handlers with transaction-local prepared rules. The transaction handle contains
reported events, memo values, prepared indexes, and per-stage data. Validation does not make application side effects
atomic and does not undo caller-owned entity changes.

Stage names in prose are begin, immediate, commit, checkpoint, rollback, and validation. Uppercase names refer only
to the corresponding `StageName` constants. The selected contract and its implementation evidence are tracked
separately in [Sprint 08](../project/sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md).

## Stage timing

The begin stage evaluates entity rules on the first reported event of each event/type key, independently of entity
identity. It is distinct from a begin lifecycle callback, which runs during transaction creation. The immediate rules
run on every report. The validation rules run at checkpoint or commit against folded events and current values.
Invariants and effects default to validation; intents default to immediate. Explicit stage assignment takes precedence.

A successful checkpoint continues the same transaction. The immediate evaluation stays active, and begin evaluation
keeps its existing once-per-key history: an already encountered key does not run again, while a new key can run.
A checkpoint does not start another transaction or reset event identity. This preserves authorization and report-time effects
throughout the usable handle's lifetime.

## Lifecycle ownership

`begin` returns a `Tr`. Callers use that handle for commit, checkpoint, and rollback. Event, access, and memo APIs may
continue to use the handle's string ID so application helpers can report events without owning lifecycle control.
Operation keys remain strings.

Successful commit and explicit rollback close the handle and remove its active registry entry. Failed checkpoint and
commit attempts make the handle rollback-only and remove that entry, so neither handle-based nor string-ID event
reporting can continue. Checkpoint and commit retries are rejected. The caller-held handle remains available for
explicit rollback or close; rollback callbacks run only when rollback is called. If the caller abandons the handle,
the registry no longer retains it, so the handle and its transaction-local data can be reclaimed with the handle. No
secondary registry retains failed transactions. This uniform failure contract avoids repeating callbacks or effects
against partially changed state, at the cost of requiring a fresh transaction after correction.

Each execution of an `Op`-annotated method owns the `Tr` returned for that invocation in a local variable. Normal return
commits it and preserves the method result. A method or commit failure attempts explicit rollback on that same handle,
then rethrows the original throwable; a rollback failure is attached as suppressed. The aspect does not share mutable
transaction state between invocations. AspectJ weaving must be enabled for the advice to run.

Validation reports are created for each validation attempt, so violations do not contaminate another transaction or a
later evaluation after a successful checkpoint. A failed checkpoint is not a recoverable validation probe on the same
handle. A single `Tr` remains caller-owned and is not safe for concurrent mutation.

## Runtime transaction configuration

Custom `Transaction` objects configure rules at runtime using information available at the point of invocation,
rather than requiring every rule to be declared at application startup. A caller may construct one and supply it to
`begin`, or obtain transaction rules from a configured factory. For example, endpoint parameters can supply a rule's
threshold; the mechanism is equally applicable to non-request business operations.

The active `Tr` owns its binding to the custom rules and any captured values needed for evaluation. These references
stay transaction-local and are released on disposal, rather than becoming durable configuration or shared cache
contents. The library does not clear caller-owned objects. This ownership model does not establish a snapshot contract.

## Failure and shutdown behavior

Commit callbacks retain their existing position before validation because they are part of the commit attempt and can
prepare required state. Checkpoint callbacks likewise precede checkpoint-stage entity evaluation and validation.
Either boundary can fail after application effects have already run. Its exception reports failure; the rollback-only
state then prevents further business work. Neither rollback nor close automatically undoes those effects.

Ordinary violations are collected into a per-evaluation report under the
[effect-aware failure policy](IntentsAndEffects.design.md#evaluation-failure-policy). Checkpoint and commit retain
their exception-based failure signal after collection; this design adds no public report-returning API. Unexpected
callback failures and authorization denials stop evaluation immediately. The
[structured diagnostic design](EventsAndConstraints.design.md#structured-violation-diagnostics) identifies the
operation, stage/boundary, offending rule/declaration context where available, and original cause. Shared consequences
appear once; optional actions supply useful specific corrections rather than stock rollback/retry advice. Recovery
still uses explicit rollback or close followed by a fresh transaction.

Explicit rollback retains its cleanup behavior: lifecycle callbacks precede rollback-stage handlers, each phase
continues after callback failures, and later failures are suppressed under the first. Disposal runs regardless of
callback outcome. This cleanup traversal is distinct from ordinary validation evaluation.

Library shutdown uses an immutable `ShutdownPolicy` that currently configures a graceful wait timeout. Transaction
logic atomically stops new begins, counts active handles and in-flight transaction work, and waits without holding
locks across callbacks. Existing calls can finish during the drain. A timeout or interruption leaves shutdown
resumable; resources are not force-cleared. Direct transaction close deregisters the handle without invoking
callbacks. Close from a callback fails immediately because waiting there would wait on itself. Failed-checkpoint and
failed-commit handles are detached and do not delay shutdown; if shutdown completes before explicit rollback,
local state can be disposed but rollback callbacks can no longer run.

The existing properties loader accepts `shutdown.timeoutMillis`; classpath defaults are overridden by the selected
environment file, and a later call to `Existential.shutdownPolicy` or `Ex.shutdownPolicy` takes precedence. This keeps
shutdown configuration at the library boundary rather than mixing it into operation rule builders.

## Performance

The [runtime execution-plan design](ExecutionPlans.design.md) compiles stage/selection requirements and specializes
validation-only reporting while respecting stage lifetime and transaction-local overlays. `ExecutionPlanLogic`
coordinates `CompileExecutionPlan` and transaction binding; lifecycle actions retain their callback order. Static
configuration alone cannot determine a supplied transaction's report-time requirements. Reporting from a begin
callback stays conservative until the effective plan is prepared. Empty entity stages can skip traversal without
skipping lifecycle callbacks, admission accounting, or disposal.

Prepared transaction overlays avoid repeatedly merging handler lists during dispatch. The transaction-handle change
adds no lookup structure: the active registry remains necessary for string-ID event reporting. The benchmark guide and
measured records live under [performance](../performance/Readme.md).
